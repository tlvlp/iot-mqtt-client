# IoT Server MQTT Client Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for the communication between 
IoT units (via an mqtt broker) and the rest of the services.
* Subscribes to global mqtt topics to accept status updates from units
* Handles all incoming and outgoing messages.
* Persist all messages to the database.

## Deployment
See the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)


## Server-side API
Actual API endpoints are inherited from the he project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### POST outgoing messages:
Mandatory fields:
- unitID: The ID of the MCU to be targeted by the message
- topic

```
{
    "unitID": "my_test_unitID",
    "topic": "/units/my_test_unitID/control"
}
```

## MQTT API

The MQTT API is detailed in the project's [Unit service](https://gitlab.com/tlvlp/iot.server.unit.service) that is the end consumer.