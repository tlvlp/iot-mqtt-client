package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import com.tlvlp.iot.server.mqtt.client.rpc.IncomingMessageForwarder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
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

    public MessagingService(MessageDbService messageDbService, IncomingMessageForwarder forwarder,
                            MqttClient client, ObjectMapper jsonMapper) {
        this.messageDbService = messageDbService;
        this.forwarder = forwarder;
        this.client = client;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Handles incoming messages
     * 1. Creates a new {@link Message} with the details from the incoming MQTT message
     * 2. Saves the created message to the database
     * 3. Forwards it to the processing service
     *
     * @param topic   - MQTT topic
     * @param message - MQTT message
     */
    public void handleIncomingMessage(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) {
        try {
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            Map<String, String> payloadMap =
                    this.jsonMapper.readValue(new String(message.getPayload()), typeRef);
            Message newMessage = new Message()
                    .setTimeArrived(LocalDateTime.now())
                    .setDirection(Message.Direction.INCOMING)
                    .setTopic(topic)
                    .setUnitID(payloadMap.get("unitID"))
                    .setPayload(payloadMap);
            if (isValidMessage(newMessage)) {
                messageDbService.save(newMessage);
                forwarder.forwardMessage(newMessage);
            } else {
                log.error("Error cannot save new MQTT message! Incomplete details: {}", newMessage);
            }
        } catch (IOException e) {
            log.error("Error deserializing MQTT message", e);
        }
    }

    public void handleOutgoingMessage(Message message) throws MqttException, IllegalArgumentException {
        message.setDirection(Message.Direction.OUTGOING);
        message.setTimeArrived(LocalDateTime.now());
        if (isValidMessage(message)) {
            try {
                sendMessage(message);
                messageDbService.save(message);
            } catch (JsonProcessingException e) {
                String er = String.format("Error sending MQTT message: %s", e.getMessage());
                log.error(er);
                throw new IllegalArgumentException(er);
            }
        } else {
            String er = String.format("Error cannot process outgoing MQTT message! Incomplete details: %s", message);
            log.error(er);
            throw new IllegalArgumentException(er);
        }
        log.info("MQTT message sent: {}", message);
    }

    private void sendMessage(Message message) throws MqttException, JsonProcessingException {
        String topic = message.getTopic();
        byte[] payload = this.jsonMapper.writeValueAsBytes(message.getPayload());
        client.publish(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(payload));
    }

    private boolean isValidMessage(Message message) {
        return message.getUnitID() != null;
    }

}

