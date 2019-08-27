package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
            ResponseEntity<String> response = restTemplate.postForEntity(
                    properties.UNIT_SERVICE_API_INCOMING_MESSAGE_URL,
                    message,
                    String.class);
            log.info("Message (id: {}) forwarded to the Unit Service.", message.getTimeArrived());
        } catch (ResourceAccessException e) {
            log.error("Unit Service is not responding: {}", e.getMessage());
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Cannot forward message to Unit Service: {}", e.getResponseBodyAsString());
        }
    }


}
