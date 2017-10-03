#include <SPI.h>
#include <Ethernet.h>

byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,2,30);
EthernetServer server(3264);

void setup() {
  Serial.begin(9600);
   while (!Serial) {
    ;
  }

  Ethernet.begin(mac, ip);
  server.begin();
  Serial.print("server is at ");
  Serial.println(Ethernet.localIP());
  pinMode(13,OUTPUT);
}


void loop() {
  EthernetClient client = server.available();
  if (client) {
    Serial.println("new client");
    
    //Read Data
    String data="";
    while (client.available()) {
      char c=client.read();
      data+=c;
    }
    Serial.println(data);
    client.stop();
    Serial.println("client disonnected");
    
    //Get port from EHAP Request
    int port=getFirstAddress(getValue(data,';',3),"binary").toInt();
    
    String reqType=getValue(data,';',5);
    Serial.println(reqType);
    
    //Handle SET
    if (reqType=="SET") {
      boolean state=false;
      String stateRaw=getValue(data,';',7);
      stateRaw.replace(";","");
      Serial.println(stateRaw);
      
      state=(stateRaw.indexOf("rue")>=0||stateRaw.indexOf("1")>=0);
      Serial.println(state);
      Serial.println(port);
      
      pinMode(port,OUTPUT);
      
      if (state==1) {
        Serial.println("YOLOSWAG");
        digitalWrite(port, HIGH);
      } else {
        Serial.println("YOLO");
        digitalWrite(port,LOW); 
      }
      
      //Send PUSH
      String myaddr=getFirstAddress(getValue(data,';',4),"EHAP");
      char server[myaddr.length()];
      myaddr.toCharArray(server,myaddr.length()+1);
      Serial.println(server);
      
      EthernetClient client1;
      client1.connect(server,3264);
      String r="EHAP;1.1;-1;"+getValue(data,';',4)+";"+getValue(data,';',3)+";PUSH;1;"+state+";;";
      Serial.println(r);
      
      char msg[r.length()];
      r.toCharArray(msg,r.length()+1);
      
      client1.write(msg);
      client1.flush();
      client1.stop();
    }
  }
}

String getFirstAddress(String addr, String protocol) {
  int index=0;
  
  String result="-1";
  while (result=="-1"&&index<addr.length()) {
    Serial.println("Processing"+addr);
    if (getValue(addr,'/',index).indexOf(protocol)!=-1) {
      result=getValue(addr,'/',index+2);
    }
    
    index+=1;
  }
  
  Serial.println(result);
  return result;
}

//Credit to http://stackoverflow.com/questions/9072320/split-string-into-string-array
String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

