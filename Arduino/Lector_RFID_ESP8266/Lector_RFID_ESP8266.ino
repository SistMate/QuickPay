#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <Arduino.h>
#include <SPI.h>
#include <MFRC522.h>

#if defined(ESP32)
  #define SS_PIN 5
  #define RST_PIN 22
#elif defined(ESP8266)
  #define SS_PIN D8
  #define RST_PIN D0
#endif

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class
MFRC522::MIFARE_Key key;
// Init array that will store new NUID
byte nuidPICC[4];

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
String DatoHex;
const String UserReg_1 = "23B36511";
const String UserReg_2 = "B33786A3";
const String UserReg_3 = "7762C83B";
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
const int ledPinR = D2;
const int ledPinG = D3;
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
const char* ssid = "WIFI-ANDREWZZYX";       // Nombre de tu red Wi-Fi
const char* password = "zvaWm3Yw";          // Contraseña de tu red Wi-Fi

void setup() 
{
   Serial.begin(9600);
   SPI.begin(); // Init SPI bus
   rfid.PCD_Init(); // Init MFRC522
   Serial.println();
   Serial.print(F("Reader :"));
   rfid.PCD_DumpVersionToSerial();
   for (byte i = 0; i < 6; i++) {
     key.keyByte[i] = 0xFF;
   } 
   DatoHex = printHex(key.keyByte, MFRC522::MF_KEY_SIZE);
   Serial.println();
   Serial.println();
   //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   WiFi.begin(ssid, password); // Conecta a la red Wi-Fi

   Serial.print("Conectando a Wi-Fi");
   while (WiFi.status() != WL_CONNECTED) { // Espera a que se conecte
      delay(1000);
      Serial.print(".");
   }

   Serial.println();
   Serial.println("Conectado a la red Wi-Fi");
   Serial.print("Dirección IP: ");
   Serial.println(WiFi.localIP()); // Imprime la dirección IP
   //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   Serial.println();
   Serial.println();
   Serial.println("Iniciando el Programa");
   pinMode(ledPinR,OUTPUT);
   pinMode(ledPinG,OUTPUT);
}

void loop() 
{
     // Reset the loop if no new card present on the sensor/reader. This saves the entire process when idle.
     if ( ! rfid.PICC_IsNewCardPresent()){return;}
     
     // Verify if the NUID has been readed
     if ( ! rfid.PICC_ReadCardSerial()){return;}
     
     Serial.print(F("PICC type: "));
     MFRC522::PICC_Type piccType = rfid.PICC_GetType(rfid.uid.sak);
     Serial.println(rfid.PICC_GetTypeName(piccType));
     // Check is the PICC of Classic MIFARE type
     if (piccType != MFRC522::PICC_TYPE_MIFARE_MINI && piccType != MFRC522::PICC_TYPE_MIFARE_1K && piccType != MFRC522::PICC_TYPE_MIFARE_4K)
     {
       Serial.println("Su Tarjeta no es del tipo MIFARE Classic.");
       digitalWrite(ledPinR, HIGH);
       delay(1000);
       digitalWrite(ledPinR, LOW);
       return;
     }
    
     Serial.println("Se ha detectado una tarjeta.");
     for (byte i = 0; i < 4; i++) {nuidPICC[i] = rfid.uid.uidByte[i];}
    
     DatoHex = printHex(rfid.uid.uidByte, rfid.uid.size);
     Serial.print("Codigo Tarjeta: "); Serial.println(DatoHex);
     digitalWrite(ledPinG, HIGH);
     delay(1000);
     digitalWrite(ledPinG, LOW);
     // Halt PICC
     rfid.PICC_HaltA();
     // Stop encryption on PCD
     rfid.PCD_StopCrypto1();
}

String printHex(byte *buffer, byte bufferSize)
  {  
    String DatoHexAux = "";
      for (byte i = 0; i < bufferSize; i++){
            if (buffer[i] < 0x10){
               DatoHexAux = DatoHexAux + "0";
               DatoHexAux = DatoHexAux + String(buffer[i], HEX);
            }
            else { 
              DatoHexAux = DatoHexAux + String(buffer[i], HEX); 
            }
      }
            
      for (int i = 0; i < DatoHexAux.length(); i++) {DatoHexAux[i] = toupper(DatoHexAux[i]);}
      return DatoHexAux;
  }

