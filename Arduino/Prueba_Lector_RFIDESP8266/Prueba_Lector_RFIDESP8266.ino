#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <SPI.h>
#include <MFRC522.h>

// Pines y configuración de la tarjeta RFID
#define SS_PIN D8
#define RST_PIN D0
MFRC522 rfid(SS_PIN, RST_PIN);
MFRC522::MIFARE_Key key;
byte nuidPICC[4];

// Configuración de Wi-Fi
const char* ssid = "WIFI-ANDREWZZYX";
const char* password = "zvaWm3Yw";

// Configuración de Firebase usando FirebaseConfig y FirebaseAuth
FirebaseData firebaseData;
FirebaseConfig config;
FirebaseAuth auth;

const char* firebaseHost = "quickpay-f9e40-default-rtdb.firebaseio.com";
const char* firebaseAuth = "19PmYFlOsV3BrmkLFxE61XxD9xz2kOLmbkpoUjSd";

// Pines LED
const int ledPinR = D2;
const int ledPinG = D3;

void setup() {
   Serial.begin(9600);
   SPI.begin();
   rfid.PCD_Init();
   pinMode(ledPinR, OUTPUT);
   pinMode(ledPinG, OUTPUT);
   
   for (byte i = 0; i < 6; i++) {
     key.keyByte[i] = 0xFF;
   }

   // Conexión Wi-Fi
   WiFi.begin(ssid, password);
   Serial.print("Conectando a Wi-Fi");
   digitalWrite(ledPinR, HIGH);
   while (WiFi.status() != WL_CONNECTED) {
      delay(1000);
      Serial.print(".");
   }
   Serial.println();
   Serial.println("Conectado a la red Wi-Fi");   
   digitalWrite(ledPinR, LOW);
   digitalWrite(ledPinG, HIGH);
   delay(1000);
   digitalWrite(ledPinG, LOW);

   // Configuración de Firebase
   config.host = firebaseHost;
   config.signer.tokens.legacy_token = firebaseAuth;
   
   // Iniciar Firebase con config y auth
   Firebase.begin(&config, &auth);
   Firebase.reconnectWiFi(true);
}

void loop() {
    if (!rfid.PICC_IsNewCardPresent()) return;
    if (!rfid.PICC_ReadCardSerial()) return;

    // Verificar tipo de tarjeta
    MFRC522::PICC_Type piccType = rfid.PICC_GetType(rfid.uid.sak);
    if (piccType != MFRC522::PICC_TYPE_MIFARE_MINI && piccType != MFRC522::PICC_TYPE_MIFARE_1K && piccType != MFRC522::PICC_TYPE_MIFARE_4K) {
        Serial.println("Tarjeta no compatible.");
        digitalWrite(ledPinR, HIGH);
        delay(1000);
        digitalWrite(ledPinR, LOW);
        return;
    }

    // Obtener código de tarjeta
    String DatoHex = printHex(rfid.uid.uidByte, rfid.uid.size);
    Serial.print("Código de Tarjeta: ");
    Serial.println(DatoHex);

    // Verificar si el código ya existe en Firebase
    if (Firebase.getString(firebaseData, "/Cards/Tarjeta/" + DatoHex)) {
        if (firebaseData.stringData() == DatoHex) {
            Serial.println("El código ya existe en Firebase. No se enviará.");
            digitalWrite(ledPinR, HIGH);
            delay(1000);
            digitalWrite(ledPinR, LOW);
        } else {
            // Enviar el código a Firebase si no existe
            if (Firebase.setString(firebaseData, "/Cards/Tarjeta/" , DatoHex)) {
                Serial.println("Código enviado a Firebase con éxito.");
                digitalWrite(ledPinG, HIGH);
                delay(1000);
                digitalWrite(ledPinG, LOW);
            } else {
                Serial.print("Error enviando a Firebase: ");
                Serial.println(firebaseData.errorReason());
                digitalWrite(ledPinR, HIGH);
                delay(1000);
                digitalWrite(ledPinR, LOW);
            }
        }
    } else {
        // Si el código no existe, lo enviamos
        if (Firebase.setString(firebaseData, "/Cards/Tarjeta/" , DatoHex)) {
            Serial.println("Código enviado a Firebase con éxito.");
            digitalWrite(ledPinG, HIGH);
            delay(1000);
            digitalWrite(ledPinG, LOW);
        } else {
            Serial.print("Error enviando a Firebase: ");
            Serial.println(firebaseData.errorReason());
            digitalWrite(ledPinR, HIGH);
            delay(1000);
            digitalWrite(ledPinR, LOW);
        }
    }

    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();
}

String printHex(byte *buffer, byte bufferSize) {
    String DatoHexAux = "";
    for (byte i = 0; i < bufferSize; i++) {
        if (buffer[i] < 0x10) {
            DatoHexAux += "0";
        }
        DatoHexAux += String(buffer[i], HEX);
    }
    for (int i = 0; i < DatoHexAux.length(); i++) {
        DatoHexAux[i] = toupper(DatoHexAux[i]);
    }
    return DatoHexAux;
}
