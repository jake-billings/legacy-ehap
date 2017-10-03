import time
import threading
import socket
from EHAP import parseRequest,sendOverSocket,getRealAddr,EHAPRequest,EHAPDevice,FALSEVALUES,TRUEVALUES,PORT

"""
REQTYPES
    PULL
    PUSH
    SET
"""
        
running=True

fakeaddr="EHAP://192.168.1.104/EHAP://192.168.0.137/binary://17"
devices={}#fakeaddr:EHAPDevice(fakeaddr)}

def getDeviceFromCache(addr):
    global devices
    device=None
    
    if addr in devices.keys():
        device=devices[addr]
    else:
        device=EHAPDevice(addr)
        devices[addr]=device

    return device

def handle(socktxt,addr,c):
    print "Request Received: "+(str(addr)+":"+str(socktxt)).replace("\n","")+"\n"
    #parse a request object form the recieved data
    reqObj=parseRequest(socktxt,fromAddr1=addr)
    #handle request based on type //TODO: add SET
    if reqObj.requesttype == "PUSH":
        push(reqObj,addr,c)
    elif reqObj.requesttype == "PULL":
        pull(reqObj,addr,c)
    elif reqObj.requesttype == "SET":
        iset(reqObj,addr,c)
    #Close socket to free system resourcess
    c.close()

def iset(req,addr1,c):
    device=getDeviceFromCache(req.address)
    device.state=3
    device.updateState(req.data)

def pull(req,addr1,c):
    res=EHAPRequest(req.addressfrom,"PUSH")
    res.fromaddress=req.address
    
    if req.datatype==3 and "response" in req.getAdditionalData().keys()and req.getAdditionalData()['response']=="samesock":
        #Send client list
        res.datatype=3

        
        #Check for timeout flag and wait on state 3 (transitioning) devices
        if "transitiontimeout" in req.getAdditionalData().keys() and int(req.getAdditionalData()['transitiontimeout'])>0:
            timeout=int(req.getAdditionalData()['transitiontimeout'])
            timeStarted=time.time()
            transitioning=True
            while transitioning and ((time.time()-timeStarted)<timeout):
                transitioning=False
                for key in devices.keys():
                    d=devices[key]
                    if d.state==3 or d.state=="3":
                        transitioning=True
                        break                   
        
        for key in devices.keys():
            #if (time.time()-devices[key].lastupdated)>devices[key].datalife:
            #    devices[key].refreshState()
            res.data+=key+"-"+str(devices[key].state)+","
        print "Samesock Response: "+ res.toStr()
        c.send(res.toStr())
    elif req.datatype==0 or req.datatype==1 or req.datatype==2:
        #Send client data
        device=getDeviceFromCache(req.address)
        
        #if (time.time()-device.lastupdated)>device.datalife:
        #    device.refreshState()
        
        res.datatype=1
        res.data=device.state
        res.send()

def push(req,addr1,c):    
    device=getDeviceFromCache(req.addressfrom)
    if req.data in TRUEVALUES:
        device.state=True
    elif req.data in FALSEVALUES:
        device.state=False
    else:
        device.state=req.data
    
    device.lastupdated=time.time()
    
def mainServer(port,host):
    #Initialize server socket
    s=socket.socket()
    s.bind((host,port))

    
    #Accept clients until running=False
    s.listen(5)
    while running:
        #Waits for a client then initializes a soket and and an address for that client
        c,addr=s.accept()
        #Recieve text from the client (1024 bytes)
        socktxt=c.recv(1024)
        #Start a new thread using function handle() to handle the client
        t=threading.Thread(target=handle,args=(socktxt,addr,c))
        t.start()
        #Tempory!  Stops server after accepting one client
        #running=False

if __name__=="__main__":
    #Start main server
    t=threading.Thread(target=mainServer,args=(3265,""))
    t.start()
    mainServer(PORT,"")
    
