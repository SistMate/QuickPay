#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <SPI.h>
#include <MFRC522.h>

// Pines y configuración de la tarjeta RFID
#define SS_PIN D8
#define RST_PIN D0
MFRC522 rfid(SS_PIN, RST_PIN);

// Pines LED
const int ledPinR = D2; // LED rojo
const int ledPinG = D3; // LED verde

// Configuración de Wi-Fi
const char* ssid = "WIFI-ANDREWZZYX";
const char* password = "zvaWm3Yw";

// Configuración de Firebase
FirebaseData firebaseData;
FirebaseConfig config;
FirebaseAuth auth;

const char* firebaseHost = "quickpay-f9e40-default-rtdb.firebaseio.com";
const char* firebaseAuth = "19PmYFlOsV3BrmkLFxE61XxD9xz2kOLmbkpoUjSd";

// Variables para la comunicación serial
const int baudRate = 9600;

void setup() {
    Serial.begin(baudRate);
    SPI.begin();
    rfid.PCD_Init();

    pinMode(ledPinR, OUTPUT);
    pinMode(ledPinG, OUTPUT);

    // Conexión a Wi-Fi
    WiFi.begin(ssid, password);
    Serial.print("Conectando a Wi-Fi");
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.print(".");
    }
    Serial.println("\nConectado a Wi-Fi");

    // Configuración de Firebase
    config.host = firebaseHost;
    config.signer.tokens.legacy_token = firebaseAuth;
    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);

    // Inicializar LEDs
    digitalWrite(ledPinR, LOW);
    digitalWrite(ledPinG, LOW);
}

void loop() {
    if (Serial.available()) {
        String signal = Serial.readStringUntil('\n');
        signal.trim(); // Aplicamos trim() para eliminar espacios y modificamos el objeto original
        handleSignal(signal); // Pasamos el objeto ya modificado
    }
}

void handleSignal(String signal) {
    if (signal == "GREEN") {
        successLed();
    } else if (signal == "RED") {
        errorLed();
    } else if (signal == "RED_GREEN") {
        redGreenLed();
    } else {
        Serial.println("Señal desconocida: " + signal);
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

// Función para encender LED rojo y verde
void redGreenLed() {
    digitalWrite(ledPinR, HIGH);
    digitalWrite(ledPinG, HIGH);
    delay(1000);
    digitalWrite(ledPinR, LOW);
    digitalWrite(ledPinG, LOW);
}
