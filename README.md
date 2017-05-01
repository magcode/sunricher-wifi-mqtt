# sunricher-wifi-mqtt

Controls LED devices from Sunricher ([http://www.sunricher.com/perfect-rf-system-series/wifi-transmitter.html]) using MQTT


In order to build it run the following commands
```
mvn clean install
java -jar target/sunricher-wifi-mqtt-<version>-jar-with-dependencies.jar <server e.g. tcp://192.168.155.20> <topic> <controller ip> <controller-port
```

Sample:
```
java -jar target/sunricher-wifi-mqtt-0.2.0-SNAPSHOT-jar-with-dependencies.jar tcp://192.168.0.20 myroom/lights 192.168.0.123
8899
```

##Implemented commands##
The following commands are implemented:

- power on/off a channel
- dim/brightness (white)

In case you miss commands please create a pull request or donate the hardware so that I can implement it.

##Tested devices##
- SR-2818WiN
- SR-1009SAC-HP

##Send MQTT commands##
You need to run a MQTT broker and you can controll the LEDs using a choosen topic.

###Power On###

```
myroom/lights/1/power
```
Send `0` for Power Off and `1` for Power On.

###Brightness###

```
myroom/lights/1/brightness
```

Send an integer between `0` and `255`