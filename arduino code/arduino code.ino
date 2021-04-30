#include <SoftwareSerial.h>


float val;

int temp;

String ssid     = "Simulator Wifi";
String password = "";

String host     = "api.thingspeak.com";
const int httpPort   = 80;

String writeUrl = "/update?api_key=62FJS1CM2EG2RG62&field1="; //replace with your write api key

void setupESP8266(void) {
  
  // Start our ESP8266 Serial Communication
  Serial.begin(115200);   
  Serial.println("AT");
  delay(10);
  
  if (Serial.find("OK"))
    Serial.println("ESP8266 OK!!!");
    
  // Connect to Simulator Wifi
  Serial.println("AT+CWJAP=\"" + ssid + "\",\"" + password + "\"");
  delay(10);
  if (Serial.find("OK"))
    Serial.println("Connected to WiFi!!!");
  
  // Open TCP connection to the host:
  //ESP8266 connects to the server as a TCP client. 

  Serial.println("AT+CIPSTART=\"TCP\",\"" + host + "\"," + httpPort);
  delay(50);
  if (Serial.find("OK")) 
   Serial.println("ESP8266 Connected to server!!!") ;
 
}

void WriteData(void) {
  
  Serial.println("Write Data got called!");
  
  val = analogRead(A0);
  temp = map(val,20,358,-40,125);
  
  if(temp != prevTemp){
  
    prevTemp = temp;
    int humidity = random(10, 100);
    
    // Construct our HTTP call
  String httpPacket = "GET " + writeUrl + String(temp) + "&field2=" + String(humidity) + " HTTP/1.1\r\nHost: " + host + "\r\n\r\n";
  int length = httpPacket.length();
  
  // Send our message length
  Serial.print("AT+CIPSEND=");
  Serial.println(length);
  delay(10);

  // Send our http request
  Serial.print(httpPacket);
  delay(10);
  if (Serial.find("SEND OK\r\n"))
    Serial.println("ESP8266 sends data to the server");
  }
}


void setup() {
  pinMode(A0, INPUT);
  setupESP8266();
               
}

void loop() {
  
  WriteData();
  delay(1000);
  
}
