# sunricher-wifi-mqtt

Controls LED devices from Sunricher (http://www.sunricher.com/perfect-rf-system-series/wifi-transmitter.html) using MQTT


To build it run

mvn clean install
java -jar target/sunricher-wifi-mqtt-<version>-jar-with-dependencies.jar <server e.g. tcp://192.168.155.20> <topic>
