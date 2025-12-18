/**
 * Redes Avanzadas
 * Modelo para peticiones GET y POST
 */

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <ArduinoJson.h>
#include <AESLib.h>

String serverName = "http://192.168.137.1:5001/record"; 
const char* ssid = "miyazaki";
const char* password = "hidetaka";
String nombreNodo = "darksouls";
byte aesKey = 0x2b;
byte iv = 0x00;
AESLib aesLib;

void setup() {
  // Inicialización de la comunicación serial
  Serial.begin(9600);

  // Conexión WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) { 
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
  // Crear objeto cliente HTTP
  WiFiClient client;
  HTTPClient http;

  // Datos del nodo
  String id_nodo = "darksouls";
  int temp = 123, hum = 3, co2 = 777, vol = 69;

  // Crear el JSON con el tamaño adecuado
  StaticJsonDocument<256> doc;  
  doc["id_nodo"] = id_nodo;
  doc["temperatura"] = temp;
  doc["humedad"] = hum;
  doc["co2"] = co2;
  doc["volatiles"] = vol;

  // Serializar el JSON en un String
  String payload;
  serializeJson(doc, payload);
  byte encrypted[256];  // Array para almacenar el payload encriptado
  int payloadLength = payload.length();
  int encryptedLength = aesLib.encrypt((byte*)payload.c_str(), payloadLength, encrypted, aesKey, 1, iv);
  
  
  String encryptedPayload = "";
  for (int i = 0; i < encryptedLength; i++) {
    encryptedPayload += (char)encrypted[i];
  }

  // Configurar la solicitud HTTP
  http.begin(client, serverName); 
  http.addHeader("Content-Type", "application/json"); 

  // Enviar la solicitud POST
  int httpResponseCode = http.POST(encryptedPayload);
  
  // Verificar la respuesta
  if (httpResponseCode > 0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String response = http.getString();
    Serial.println("Response: " + response);
  } else {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }

  // Liberar recursos
  http.end();

  // Esperar 1 segundo antes de la siguiente petición
  delay(1000);
}

