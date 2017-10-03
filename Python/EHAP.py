import time
import socket

TRUEVALUES=['True','true','yes','1']
FALSEVALUES=['False','false','no','0']
PORT=3264

def parseRequest(req,fromAddr1=""):
    reqArr=req.split(";")
    reqObj=EHAPRequest(reqArr[3],reqArr[4])
    reqObj.protocol=reqArr[0]
    reqObj.version=float(reqArr[1])
    reqObj.timestamp=int(reqArr[2])
    reqObj.address=reqArr[3]
    reqObj.addressfrom=reqArr[4]
    reqObj.requesttype=reqArr[5]
    reqObj.datatype=int(reqArr[6])
    reqObj.data=reqArr[7]
    reqObj.additionaldata=reqArr[8]
    
    reqObj.fromAddr1=fromAddr1
    return reqObj

def sendOverSocket(address,port,data):
    print "Request Sent:"+address+":"+str(port)+" : "+data+"\n"
    #if "PULL" in data: handle("EHAP;1.0;"+str(int(time.time()))+";http://192.168.0.137;http://192.168.0.137/http://192.168.0.30/binary://7;PUSH;1;True;;","192.168.0.30")
    if "0.0.0.0" in address:
        return
    s=socket.socket()
    s.connect((address,port))
    s.send(data)
    s.close()

def getRealAddr(addr,index):
    addr1=""
    proto=""
    i=-1

    for part in addr.split("/"):
        if ":" in part:
            proto=part.replace(":","")
        elif part is not "":
            addr1=part
            i+=1
            
        if i==index:
            break

    return proto,addr1
        

class EHAPRequest:
    def __init__(self,addr,reqtype):
        self.protocol="EHAP"
        self.version=1.1
        self.timestamp=time.time()
        self.address=addr
        self.addressfrom="EHAP://"+socket.gethostbyname(socket.gethostname())
        self.requesttype=reqtype
        self.datatype=0
        self.data=""
        self.additionaldata=""

        self.fromAddr1=""

    def addChildAddress(self,addr):
        self.address+="/"+addr

    def addAdditionalData(self,key,value):
        self.additionaldata+=","+key+":"+value

    def getAdditionalData(self):
        arr=self.additionaldata.split(",")
        mydict={}
        for a in arr:
            arr1=a.split(":")
            if len(a)>1:
                mydict[arr1[0]]=arr1[1]
        return mydict

    def toStr(self):
        return self.protocol+";"+str(self.version)+";"+str(int(self.timestamp))+";"+self.address+";"+self.addressfrom+";"+self.requesttype+";"+str(self.datatype)+";"+str(self.data)+";"+self.additionaldata+";"

    def send(self):
        proto,addr=getRealAddr(self.address,0)
        if proto == "EHAP":
            sendOverSocket(addr,PORT,self.toStr())
        
class EHAPDevice:
    def __init__(self,addr):
        self.protocol="EHAP"
        self.version=1.1
        self.address=addr
        self.state=False
        self.lastupdated=0

        #5 minutes in seconds
        self.datalife=5*60
        self.timeout=5

    #Set state on device
    def updateState(self,state):
        req=EHAPRequest("/".join(self.address.split("/")[3:]),"SET")
        req.datatype=1
        req.data=state
        req.send()
        return self.waitForPush()
    
    #Get state from device
    def refreshState(self):
        req=EHAPRequest(self.address,"PULL")
        req.send()
        return self.waitForPush()
    
    def waitForPush(self):
        timeStarted=time.time()
        while ((time.time()-self.lastupdated)>self.datalife):
            if (time.time()-timeStarted)>self.timeout:
                self.state=-1
                return False
            time.sleep(0.25)
        return True
