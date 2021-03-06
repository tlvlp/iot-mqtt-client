package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import com.tlvlp.iot.server.mqtt.client.services.IncomingMessageForwarder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagingService {
    private static final Logger log = LoggerFactory.getLogger(MessagingService.class);

    private MqttClient client;
    private MessageDbService messageDbService;
    private IncomingMessageForwarder forwarder;
    private ObjectMapper jsonMapper;

    public MessagingService(MessageDbService messageDbService, IncomingMessageForwarder forwarder,
                            MqttClient client, ObjectMapper objectMapper) {
        this.messageDbService = messageDbService;
        this.forwarder = forwarder;
        this.client = client;
        this.jsonMapper = objectMapper;
    }

    public void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            var typeRef = new TypeReference<HashMap<String, String>>() {
            };
            Map<String, String> payloadMap =
                    jsonMapper.readValue(new String(message.getPayload()), typeRef);
            Message newMessage = new Message()
                    .setTimeArrived(LocalDateTime.now())
                    .setDirection(Message.Direction.INCOMING)
                    .setTopic(topic)
                    .setPayload(payloadMap);
            var validationProblems = Validation.buildDefaultValidatorFactory().getValidator().validate(newMessage);
            if (!validationProblems.isEmpty()) {
                throw new InvalidMessageException(validationProblems.toString());
            }
            messageDbService.save(newMessage);
            forwarder.forwardMessage(newMessage);
        } catch (IOException | InvalidMessageException e) {
            log.error("Error parsing MQTT message: {}", e.getMessage());
        }
    }

    public void handleOutgoingMessage(Message message) throws MqttException, InvalidMessageException {
        try {
            message.setDirection(Message.Direction.OUTGOING);
            message.setTimeArrived(LocalDateTime.now());
            sendMessage(message);
            messageDbService.save(message);
            log.info("MQTT message sent: {}", message);
        } catch (JsonProcessingException e) {
            String err = String.format("Error sending MQTT message: %s", e.getMessage());
            log.error(err);
            throw new InvalidMessageException(err);
        }
    }

    private void sendMessage(Message message) throws MqttException, JsonProcessingException {
        String topic = message.getTopic();
        byte[] payload = jsonMapper.writeValueAsBytes(message.getPayload());
        client.publish(topic, new MqttMessage(payload));
    }

}

