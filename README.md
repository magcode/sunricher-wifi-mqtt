# sunricher-wifi-mqtt

Controls LED devices from Sunricher [http://www.sunricher.com/perfect-rf-system-series/wifi-transmitter.html] using MQTT.
You need to own at least one WIFI-to-RF device (SR-2818WiN) and one RF receiver/actuator (e.g. SR-1009SAC-HP)

The program is written in Java and acts as a "bridge". You can run it on almost any device which can run Java.

To build run the following commands:
```
mvn clean install
java -jar target/sunricher-wifi-mqtt-<version>-jar-with-dependencies.jar <protocol://mqtt broker> <topic> <LED controller ip> <LED controller-port
```

Sample:
```
java -jar target/sunricher-wifi-mqtt-0.2.0-SNAPSHOT-jar-with-dependencies.jar tcp://192.168.0.20 myroom/lights 192.168.0.123
8899
```

## Implemented commands ##
The following commands are implemented:

- power on/off a channel
- dim/brightness (white)

In case you miss commands please create a pull request or donate the hardware so that I can implement it.

## Tested devices ##
- SR-2818WiN
- SR-1009SAC-HP

## Send MQTT commands ##
You need to run a MQTT broker and you can control the LEDs using a chosen topic.

### Power On ###

```
myroom/lights/<channels>/power
```
To specifiy the channel use e.g. `1` or a single channel or e.g. `2,3` for multiple channels.
Send `0` or `OFF` for Power Off and `1` or `ON` for Power On.


### Brightness ###

```
myroom/lights/<channels>/brightness
```
To specifiy the channel use e.g. `1` or a single channel or e.g. `2,3` for multiple channels.
Send a number between `0` and `100`