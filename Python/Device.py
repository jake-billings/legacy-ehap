from EHAP import EHAPRequest, parseRequest, getRealAddr, TRUEVALUES, PORT
import socket
import threading

pins={0:False}
serverAddr="EHAP://192.168.0.137"
running=True

def getPinValue(index):
    if (index not in pins.keys()):
        pins[index]=False
    
    return pins[index]


def pull(req,pin):
    sendPush(req.addressfrom,pin)

def iset(req,pin):
    pins[pin]=req.data in TRUEVALUES
    sendPush(req.addressfrom,pin)

def sendPush(addr,pin):
    req=EHAPRequest(addr,"PUSH")
    req.datatype=1
    req.data=getPinValue(pin)
    req.addressfrom=serverAddr+"/"+req.addressfrom+"/binary://"+str(pin)
    req.send()
    print req.toStr()

def handle(socktxt,addr,c):
    print "Request Received: "+(str(addr)+":"+str(socktxt)).replace("\n","")+"\n"
    #parse a request object form the recieved data
    reqObj=parseRequest(socktxt,fromAddr1=addr)

    pin=int(getRealAddr(reqObj.address,reqObj.address.count("//")-1)[1])
    
    #handle request based on type //TODO: add SET
    if reqObj.requesttype == "PUSH":
        pass
    elif reqObj.requesttype == "PULL":
        pull(reqObj,pin)
    elif reqObj.requesttype == "SET":
        iset(reqObj,pin)
    #Close socket to free system resourcess
    c.close()
    print pins

def mainServer():
    global PORT
    global running
    
    #Initialize server socket
    s=socket.socket()
    host=""
    port=PORT
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
    sendPush(serverAddr,0)
    mainServer()
