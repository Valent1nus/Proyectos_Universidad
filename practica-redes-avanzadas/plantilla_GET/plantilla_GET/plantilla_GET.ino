/**
 * Redes Avanzadas
 * Modelo para peticiones GET y POST
 */

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

String serverName = "http://192.168.137.1:5001";
const char* ssid     = "miyazaki";
const char* password = "hidetaka";
String nombreNodo = "darksouls";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

   WiFi.mode(WIFI_STA);
   WiFi.begin(ssid, password);
   while (WiFi.status() != WL_CONNECTED) 
   { 
     delay(100);  
     Serial.print('.'); 
   }
 
   Serial.println("");
   Serial.print("Iniciado STA:\t");
   Serial.println(ssid);
   Serial.print("IP address:\t");
   Serial.println(WiFi.localIP());
}

void loop() {
  // put your main code here, to run repeatedly:

   WiFiClient client;
   HTTPClient http;
   String id_nodo = "darksouls";
   int temp = 123, hum = 3, co2 = 777, vol = 69;

   String serverPath = serverName + "/record?id_nodo=" + id_nodo + "&temperatura=" + temp + "&humedad=" + hum + "&co2=" + co2 + "&volatiles=" + vol;

   http.begin(client, serverPath.c_str());
   // Send HTTP GET request
   int httpResponseCode = http.GET();
   
   if (httpResponseCode <= 0) {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);

    
  }
  // Free resources
  http.end();
  delay(1000);
}
