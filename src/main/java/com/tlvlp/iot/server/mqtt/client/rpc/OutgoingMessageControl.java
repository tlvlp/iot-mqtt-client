package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OutgoingMessageControl {

    private MessagingService service;

    public OutgoingMessageControl(MessagingService service) {
        this.service = service;
    }

    @PostMapping("${MQTT_CLIENT_MESSAGE_CONTROL}")
    public ResponseEntity postMessage(@RequestBody Message message) {
        try {
            service.handleOutgoingMessage(message);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MqttException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}
