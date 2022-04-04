# ethereum-gsheets-telegram-raspi-messaging

## DIY: Build Stupid and Crazy Expensive Messaging for LED Switches

<img src="https://user-images.githubusercontent.com/2828581/161578594-9472a52c-42ad-41a1-934a-787f750cd214.png" width="60%"/>

## Setup

### Start on Raspberry Pi

_Note: Pi4j must be invoked as **sudo**_

```
sudo `which java` -jar raspi-messaging-0.0.1-SNAPSHOT.jar --spring.config.location=./application.properties --spring.profiles.active=prod
```