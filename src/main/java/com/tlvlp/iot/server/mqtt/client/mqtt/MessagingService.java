package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import com.tlvlp.iot.server.mqtt.client.rpc.IncomingMessageForwarder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public MessagingService(MessageDbService messageDbService, IncomingMessageForwarder forwarder, MqttClient client) {
        this.messageDbService = messageDbService;
        this.forwarder = forwarder;
        this.client = client;
        this.jsonMapper = new ObjectMapper();
    }

    public void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            Map<String, String> payloadMap =
                    jsonMapper.readValue(new String(message.getPayload()), typeRef);
            Message newMessage = new Message()
                    .setTimeArrived(LocalDateTime.now())
                    .setDirection(Message.Direction.INCOMING)
                    .setTopic(topic)
                    .setPayload(payloadMap);

            System.out.println("MSG" + newMessage);

            checkMessageValidity(newMessage);
            messageDbService.save(newMessage);
            forwarder.forwardMessage(newMessage);
        } catch (IOException | IllegalArgumentException e) {
            log.error("Error parsing MQTT message: {}", e.getMessage());
        }
    }

    public void handleOutgoingMessage(Message message) throws MqttException, IllegalArgumentException {
        try {
            checkMessageValidity(message);
            message.setDirection(Message.Direction.OUTGOING);
            message.setTimeArrived(LocalDateTime.now());
            sendMessage(message);
            messageDbService.save(message);
            log.info("MQTT message sent: {}", message);
        } catch (JsonProcessingException e) {
            String er = String.format("Error sending MQTT message: %s", e.getMessage());
            log.error(er);
            throw new IllegalArgumentException(er);
        }
    }

    private void sendMessage(Message message) throws MqttException, JsonProcessingException {
        String topic = message.getTopic();
        byte[] payload = jsonMapper.writeValueAsBytes(message.getPayload());
        client.publish(topic, new MqttMessage(payload));
    }

    private void checkMessageValidity(Message message) throws IllegalArgumentException {
        if (message.getPayload() == null) {
            throw new IllegalArgumentException("Missing payload");
        } else if (message.getTopic() == null) {
            throw new IllegalArgumentException("Missing topic");
        }
    }

}

