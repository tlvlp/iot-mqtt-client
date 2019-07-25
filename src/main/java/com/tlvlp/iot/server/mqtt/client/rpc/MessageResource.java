package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageResource {

    @Autowired
    private MessagingService service;

//    @PostMapping("${api.mqtt.messages.post}")
//    public ResponseEntity postMessage(@RequestBody Message message) {
//        try {
//            service.handleOutgoingMessage(message);
//            return new ResponseEntity(HttpStatus.ACCEPTED);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (JsonProcessingException e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (MqttException e) {
//            return new ResponseEntity(HttpStatus.ACCEPTED);
//        }
//    }

}
