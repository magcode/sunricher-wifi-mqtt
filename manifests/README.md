# Kubernetes Manifest to run `sunricher-mqtt` on Kubernetes
1. Create secret with name `sunricher-properties` and the content of `sunricher.properties` according to the main readme file.
2. Create target namespace (e.g. `kubectl create namespace sunricher-mqtt`)
3. Deploy container with `kubectl apply -f sunricher-mqtt.yaml -n sunricher-mqtt`
