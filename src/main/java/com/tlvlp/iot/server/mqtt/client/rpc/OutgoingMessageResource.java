package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.mqtt.Message;
import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutgoingMessageResource {

    private MessagingService service;

    public OutgoingMessageResource(MessagingService service) {
        this.service = service;
    }

    @PostMapping("${MQTT_CLIENT_MESSAGE_RESOURCE}")
    public ResponseEntity postMessage(@RequestBody Message message) {
        try {
            service.handleOutgoingMessage(message);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MqttException e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
