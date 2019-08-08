package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import com.tlvlp.iot.server.mqtt.client.mqtt.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class IncomingMessageForwarder {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageForwarder.class);
    private RestTemplate restTemplate;
    private Properties properties;

    public IncomingMessageForwarder(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void forwardMessage(Message message) {
        try {
            ResponseEntity response = restTemplate.postForEntity(
                    String.format("http://%s", properties.UNIT_SERVICE_MESSAGE_RESOURCE_URI),
                    message,
                    ResponseEntity.class);
            if (response.getStatusCodeValue() == 202) {
                log.debug("Message (id: {}) forwarded to the Unit Service.", message.getTimeArrived());
            } else {
                log.warn("Message (id: {}) could not be forwarded to Unit Service: {} {}",
                        message.getTimeArrived(), response.getStatusCode(), response.getBody());
            }
        } catch (ResourceAccessException e) {
            log.error("Error! Unit Service is not responding: {}", e.getMessage());
        }
    }


}
