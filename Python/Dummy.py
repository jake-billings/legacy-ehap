import socket,time

PORT=3264
running=True
state=False

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

def handle(txt):
    arr=txt.split(";")
    global state
    if "SET" in txt:
        state="True" in txt
    res="EHAP;1.1;"+str(time.time())+";"+arr[4]+";"+arr[3]+";PUSH;1;"+str(state)+";;"

    fulladdr=arr[4]
    aps=fulladdr.split("/")
    proto, addr=getRealAddr(arr[4],0)
    
    print res
    print addr
    
    s=socket.socket()
    s.connect((addr,PORT))
    s.send(res)
    s.close()

def mainServer():
    global PORT
    s=socket.socket()
    host=socket.gethostname()
    port=PORT
    s.bind((host,port))

    s.listen(5)
    while running:
        c,addr=s.accept()
        socktxt=c.recv(1024)
        c.close()
        handle(socktxt)

if __name__=="__main__":
    s=socket.socket()
    s.connect(("192.168.0.137",PORT))
    s.send("EHAP;1.1;"+str(int(time.time()))+";EHAP://192.168.0.137/EHAP://192.168.1.119/binary://7;EHAP://192.168.1.104;PUSH;1;True;;")
    s.close()
