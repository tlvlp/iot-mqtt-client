package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private JsonParser jsonParser;

    public MessagingService(MessageDbService messageDbService, IncomingMessageForwarder forwarder,
                            JsonParser jsonParser, MqttClient client) {
        this.messageDbService = messageDbService;
        this.forwarder = forwarder;
        this.jsonParser = jsonParser;
        this.client = client;
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
            Map<String, String> payloadMap =
                    jsonParser.getObjectFromJson(new String(message.getPayload()), HashMap.class);
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
        String payload = jsonParser.getJsonFromObject(message.getPayload());
        client.publish(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(payload.getBytes()));
    }

    private boolean isValidMessage(Message message) {
        return message.getUnitID() != null;
    }

}

