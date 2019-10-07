package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.mqtt.InvalidMessageException;
import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class OutgoingMessageAPI {

    private MessagingService service;

    public OutgoingMessageAPI(MessagingService service) {
        this.service = service;
    }

    @PostMapping("${MQTT_CLIENT_API_OUTGOING_MESSAGE}")
    public ResponseEntity postMessage(@RequestBody @Valid Message message) {
        try {
            service.handleOutgoingMessage(message);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (InvalidMessageException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MqttException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}
