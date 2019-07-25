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
public class MessageForwarder {

    private static final Logger log = LoggerFactory.getLogger(MessageForwarder.class);
    private RestTemplate restTemplate;
    private Properties properties;

    public MessageForwarder(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void forwardMessage(Message message) {
        try {
            ResponseEntity response = restTemplate.postForEntity(
                    String.format("http://%s", properties.UNITS_MESSAGE_RESOURCE_URI),
                    message,
                    ResponseEntity.class); //sponseEntity.class TODO?
            if (response.getStatusCodeValue() == 202) { // If the response is 202 - Accepted
                log.debug("Message forwarded to the Unit Service {}", message);
            } else {
                log.warn("Message could not be forwarded to Unit Service {} {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (ResourceAccessException e) {
            log.error("Warning! Unit Service is not responding:", e);
        }
    }


}
