# sunricher-wifi-mqtt

This tool controls Sunricher [Perfect RF](http://www.sunricher.com/perfect-rf-system-series/waterproof-perfect-rf-receiver.html) LED devices (SR-1009*) using MQTT.

You need to own at least a WiFi-to-RF device (SR-2818WiN) and one RF receiver/actuator (e.g. SR-1009SAC-HP).

The program is written in Java and acts as a "bridge". You can run it on almost any device which can run Java.

# Installing and starting #

Grab a [release](https://github.com/magcode/sunricher-wifi-mqtt/releases) or build it yourself (see below).
A configuration file `sunricher.properties` is needed and must be located beside the *.jar file:

``` 
mqttServer=<protocol and host of MQTT broker>
logLevel=<log level, e.g. ERROR,WARN,INFO,DEBUG,TRACE>
ledHost=<host of sunricher device>
topic=<mqtt topic>
disableWifi=<true|false>
```

Example:

``` 
mqttServer=tcp://192.168.0.2
LOGLEVEL=INFO
ledHost=192.168.0.3
topic=myroom/lights
disableWifi=true
```

Before you can start you need to "pair" your RF device. "Pairing" means "assign a channel". This can be achieved by using the [Easy Lighting App](http://www.sunricher.com/media/resources/manual/easylighting-user-manual.pdf). Note down the channel you have used to pair the device. Channels are count from 1 (top left) to 8 (bottom right) in the App.

To build it yourself run the following commands:

```
mvn clean install
```

To start it:

```
java -jar target/sunricher-mqtt-<version>-jar-with-dependencies.jar
```

A log file will be written: `sunricher-led.log`.

## Implemented commands ##
The following commands are implemented:

- power on/off a channel
- set and dim colors
- set and dim white
- set brightness
- start/stop built in RGB program
- change RGB program speed

In case you miss commands please create a pull request or donate the hardware so that I can implement it.
Be aware that with Sunricher devices it is not possible to read the current state from the controller.

## Tested devices ##
- [SR-2818WiN](http://www.sunricher.com/wifi-rf-convertor-sr-2818win.html)
- [SR-1009SAC-HP](http://www.sunricher.com/rf-wifi-control-ac-phase-cut-dimmer-with-push-dim-sr-1009sac-hp.html)
- [SR-1009FA](http://www.sunricher.com/5a-4ch-12-36v-constant-voltage-rf-led-strip-dimmer-controller-sr-1009fa.html)
- [SR-1009HT](http://www.sunricher.com/high-voltage-led-strip-rf-controller-sr-1009ht.html)

Please report more working devices via Pull Request for [README.md](https://github.com/magcode/sunricher-wifi-mqtt/blob/master/README.md) or via an [issue](https://github.com/magcode/sunricher-wifi-mqtt/issues).


## Send MQTT commands ##
You need to run a MQTT broker and you can control the LEDs using a chosen topic.

To specify the channel use e.g. `1` or a single channel or e.g. `2,3` for multiple channels.


### Power On ###

```
myroom/lights/<channels>/power
```
Send `0` or `OFF` for Power Off and `1` or `ON` for Power On.


### Brightness ###
This sets the combined brightness with all LEDs on. Use this for 1-channel devices.

```
myroom/lights/<channels>/brightness
```

Send a number between `0` and `100`

### Color control ###

#### Set single channels directly ####

```
myroom/lights/<channels>/[red|green|blue|white]
```

Send a number between `0` and `255` to set the channel brightness.

#### Set colors via RGB ####
```
myroom/lights/<channels>/rgb
```
Send a string `<r>,<g>,<b>`. The value for each color can be between `0` and `255`.
Example: `100,0,10`

#### Set colors via HSB ####
```
myroom/lights/<channels>/hsb
```
Send a string `<h>,<s>,<b>`.

The value for hue `h` can be between `0` and `360`.

The values for saturation `s` can be between `0` and `100`.

The values for brightness `b` can be between `0` and `100`.

Example: `310,50,50`

### RGB fading program ###

```
myroom/lights/<channels>/prg
```
Send `0` to turn off the color fading program.

Send a number between `1` and `10` to select and start one of the built in color fading programs.

### RGB fading program speed ###

```
myroom/lights/<channels>/prgspeed
```
Send a number between `1` and `10` to select the color fading speed.

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
* Now use the AT commands above to disable WIFI. Alternatively use the setting `disableWifi=true` in `sunricher.properties`
* In my case power consumption went down from 3.0 Watts (WiFi+LAN) to 2.1 Watts (LAN only)

# Openhab integration #

The file [sunricher.items](/openhab/sunricher.items) provides an example setup.
You need a [transformation file](/openhab/sunricherrgbw.js).
