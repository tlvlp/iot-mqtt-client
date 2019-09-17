# IoT Server MQTT Client Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for the communication between 
IoT units (via an MQTT broker) and the rest of the services.
- Subscribes to global MQTT topics to accept status updates from units
- Forwards all incoming messages to the API Gateway.
- Sends all outgoing messages to the topics specified in each message.
- Persists all incoming and outgoing message to the database.

## Building and publishing JAR + Docker image
This project is using the using the [Palantir Docker Gradle plugin](https://github.com/palantir/gradle-docker).
All configuration can be found in the [Gradle build file](build.gradle) file 
and is recommended to be run with the docker/dockerTagsPush task.

## Deployment
- This service is currently designed as **stateless** but still should only have one instance running per Docker Swarm Stack 
since the Mosquitto MQTT broker is not set up to be able to load balance topic subscriptions between multiple clients.
[VerneMQ](https://vernemq.com/) would be a good candidate for that scenario.
- For settings and deployment details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)


## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### POST Message (outgoing messages):

##### Related environment variables:
- ${MQTT_CLIENT_API_OUTGOING_MESSAGE}
- ${MQTT_CLIENT_API_OUTGOING_MESSAGE_URL}

##### Fields:
Takes a Message object in the RequestBody but the mandatory fields are:
- **topic**: String containing the targeted MQTT topic
- **payload**: Map<String, String> of the payload to be sent to the subscribers of the topic
- all the other fields from the Message object are ignored.


```
{
    "topic": "/units/my_test_unitID/control",
    "payload": 
        {
            "first": "value",
            "second": "value"
        }
}
```

## MQTT API

The MQTT API is detailed in the project's [Unit service](https://gitlab.com/tlvlp/iot.server.unit.service).