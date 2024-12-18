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

// Configuración de Firebase
FirebaseData firebaseData;
FirebaseConfig config;
FirebaseAuth auth;

const char* firebaseHost = "quickpay-f9e40-default-rtdb.firebaseio.com";
const char* firebaseAuth = "19PmYFlOsV3BrmkLFxE61XxD9xz2kOLmbkpoUjSd";

// Pines LED
const int ledPinR = D2; // LED rojo
const int ledPinG = D3; // LED verde

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
        errorLed();
        return;
    }

    // Obtener código de tarjeta
    String tarjetaUID = printHex(rfid.uid.uidByte, rfid.uid.size);
    Serial.print("Código de Tarjeta: ");
    Serial.println(tarjetaUID);

    // Buscar la tarjeta asociada en los usuarios de Firebase
    if (!buscarTarjetaEnUsuarios(tarjetaUID)) {
        Serial.println("Tarjeta no encontrada en ningún usuario.");
        errorLed();
    }

    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();
}

// Función para buscar la tarjeta en todos los usuarios
bool buscarTarjetaEnUsuarios(String tarjetaUID) {
    FirebaseJson jsonUsuarios;

    // Obtener toda la información de /Users de una vez
    if (Firebase.getJSON(firebaseData, "/Users")) {
        jsonUsuarios.setJsonData(firebaseData.jsonString());

        FirebaseJsonData jsonDataResult; // Variable para obtener valores del JSON

        // Usar un solo bucle para iterar sobre todos los usuarios
        size_t count = jsonUsuarios.iteratorBegin();
        for (size_t i = 0; i < count; i++) {
            int type;
            String uuid, jsonData;

            jsonUsuarios.iteratorGet(i, type, uuid, jsonData);

            // Convertir jsonData a FirebaseJson para extraer el valor de 'tarjetaID'
            FirebaseJson usuario;
            usuario.setJsonData(jsonData);

            usuario.get(jsonDataResult, "tarjetaID"); // Obtener la clave 'Tarjeta'
            String tarjetaGuardada = jsonDataResult.to<String>();

            if (tarjetaUID == tarjetaGuardada) {
                Serial.println("Tarjeta encontrada en usuario: " + uuid);
                procesarTransaccion(uuid);
                return true;
            }
        }
        jsonUsuarios.iteratorEnd();
    } else {
        Serial.print("Error al obtener usuarios: ");
        Serial.println(firebaseData.errorReason());
    }
    return false;
}

// Función para procesar la transacción
void procesarTransaccion(String uuid) {
    int montoPasajero = 0;

    // Obtener monto del pasajero
    if (Firebase.getInt(firebaseData, "/Users/" + uuid + "/monto")) {
        montoPasajero = firebaseData.intData();

        if (montoPasajero >= 2) {
            // Restar el monto al pasajero
            montoPasajero -= 2;
            Firebase.setInt(firebaseData, "/Users/" + uuid + "/monto", montoPasajero);

            // Registrar la transacción
            FirebaseJson transaccion;
            String fecha = "18/11/2024 14:35"; // Simulación de fecha
            transaccion.set("descripcion", "Transporte Público - NFC");
            transaccion.set("fecha", fecha);
            transaccion.set("monto", 2);

            Firebase.pushJSON(firebaseData, "/Users/" + uuid + "/transacciones", transaccion);

            // Aumentar el monto al conductor
            Firebase.getInt(firebaseData, "/Users/wEmRWarPm4dfx98kCSasUTu8FII3/monto");
            int montoConductor = firebaseData.intData();
            montoConductor += 2;
            Firebase.setInt(firebaseData, "/Users/wEmRWarPm4dfx98kCSasUTu8FII3/monto", montoConductor);

            Serial.println("Transacción realizada con éxito.");
            successLed();
        } else {
            Serial.println("Monto insuficiente en la cuenta del pasajero.");
            errorLed();
        }
    } else {
        Serial.println("Error al obtener el monto del pasajero.");
        errorLed();
    }
}

// Función para encender LED verde
void successLed() {
    digitalWrite(ledPinG, HIGH);
    delay(1000);
    digitalWrite(ledPinG, LOW);
}

// Función para encender LED rojo
void errorLed() {
    digitalWrite(ledPinR, HIGH);
    delay(1000);
    digitalWrite(ledPinR, LOW);
}

// Función para convertir datos de la tarjeta a formato hexadecimal
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