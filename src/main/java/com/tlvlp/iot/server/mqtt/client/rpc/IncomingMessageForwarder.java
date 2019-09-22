package com.tlvlp.iot.server.mqtt.client.rpc;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            restTemplate.postForEntity(
                    String.format("http://%s:%s%s",
                            properties.getAPI_GATEWAY_NAME(),
                            properties.getAPI_GATEWAY_PORT(),
                            properties.getAPI_GATEWAY_API_INCOMING_MQTT_MESSAGE()),
                    message,
                    String.class);
            log.info("Message (id: {}) forwarded to the API Gateway.", message.getTimeArrived());
        } catch (ResourceAccessException e) {
            log.error("API Gateway is not responding: {}", e.getMessage());
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Cannot forward message to API Gateway: {}", e.getResponseBodyAsString());
        }
    }


}
