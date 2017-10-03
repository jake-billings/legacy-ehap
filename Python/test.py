import socket,time               # Import socket module

s = socket.socket()         # Create a socket object
host = socket.gethostname() # Get local machine name
port = 3264          # Reserve a port for your service.

s.connect((host, port))
s.send("EHAP;1.1;"+str(int(time.time()))+";http://192.168.0.137;http://192.168.0.137/192.168.1.104;PULL;3;;response:samesock;")
print s.recv(1024)
s.close()
