# IoT Server MQTT Client Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for the communication between 
IoT units (via an mqtt broker) and the rest of the services.
* Subscribes to global mqtt topics to accept status updates from units
* Handles all incoming and outgoing messages.
* Persist all messages to the database.

## Deployment
For settings and deployemnt details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)


## Server-side API
Actual API endpoints are inherited from the he project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### POST outgoing messages:

Takes a Message object but the mandatory fields are:
- topic: a String containing the targeted MQTT topic
- payload: A Map<String, String> of the payload to be sent to the subscribers of the topic

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