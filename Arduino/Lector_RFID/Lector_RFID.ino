//
#include <SPI.h>
#include <MFRC522.h>


//
#define SS_PIN 10
#define RST_PIN 9

//
MFRC522 mfrc522(SS_PIN, RST_PIN);

//
int led = 2;

void setup()  {
  //
  pinMode(led, OUTPUT);

  Serial.begin(9600);
  /**/
  SPI.begin();
  mfrc522.PCD_Init();
}

void loop() {
  //
  if (!mfrc522.PICC_IsNewCardPresent())   {
    return;
  }

  //
  if (!mfrc522.PICC_ReadCardSerial())   {
    return;
  }
  //
  Serial.print("UID tag: ");

  for (byte i = 0; i < mfrc522.uid.size; i++){
    Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
    Serial.print(mfrc522.uid.uidByte[i], HEX);
  }

  //
  digitalWrite(led, HIGH);
  delay(1000);
  digitalWrite(led, LOW);

  Serial.println();
  delay(1000);
  }
}
