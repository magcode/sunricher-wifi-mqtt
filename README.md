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
java -jar target/sunricher-wifi-mqtt-0.3.0-SNAPSHOT-jar-with-dependencies.jar tcp://192.168.0.20 myroom/lights 192.168.0.123
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

## AT Commands ##

You can control the LED controller WiFi module (HF-A11) using AT commands.
Use the following topic to send commands:


```
myroom/lights/0/at
```

Example to disable WIFI (and save 1 Watt power consumption):

```
HF-A11ASSISTHREAD
```
```
+ok
```
```
AT+MSLP=off
```

# SR-2818WiN operating hints #
You might want to use the LAN interface only and turn off WiFi in order to save some energy and improve security. This is possible.
* Connect the SR-2818WiN to your wired local network
* Reset the SR-2818WiN (holding reset button approx 5 seconds)
* You should find the "EASYCOLOR" WiFi access point
* Check your network router / DHCP server. The LAN interface of the device should have received an IP address in your local network.
* Optional: make sure that the device always gets the same IP address from your network router / DHCP server.
* Optional: disallow Internet access for this particular IP address in your network router.
* Connect to this IP address with the tool as described above. (Do not connect to the wifi access point)
* The connection should work and you should be able to control your LED receivers now
* Now use the AT commands above to disable WIFI
* In my case power consumption went down from 3.0 Watts (WIFI+LAN) to 2.1 Watts (LAN only)