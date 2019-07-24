package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tlvlp.iot.api.entities.MqttMessage;
import com.tlvlp.iot.api.entities.Unit;
import com.tlvlp.iot.server.mqtt.api.SubscriptionApiService;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessagingService {
    private static final Logger log = LoggerFactory.getLogger(MessagingService.class);

    @Autowired
    private MessageDbService messageDbService;
    @Autowired
    private SubscriptionApiService subscriptionApiService;
    @Autowired
    private MqttClient client;
    @Autowired
    private JsonParser jsonParser;
    @Autowired
    private Properties properties;


    /**
     * Sends a message to a selected topic
     */
    public void handleOutgoingMessage(Message message) throws MqttException, JsonProcessingException {
        if (!message.getDirection().equals(Message.Direction.OUTGOING)) {
            String error = "Error sending MQTT message: Illegal argument. Message direction should be " + MqttMessage.Direction.OUTGOING;
            log.error(error);
            throw new IllegalArgumentException(error);
        }
        message.setTimeArrived(LocalDateTime.now());
        try {
            sendMessage(message);
            message.setProcessed(true);
            messageDbService.save(message);
        } catch (MqttException e) {
            message.setProcessed(false);
            messageDbService.save(message);
            log.warn("Warning! Connection lost to MQTT broker. Message queued: {}", message);
            throw e;
        } catch (JsonProcessingException e) {
            log.error("Error sending MQTT message: {}", e.getMessage());
            throw e;
        }
    }

    private void sendMessage(MqttMessage message) throws MqttException, JsonProcessingException {
        String topic = message.getTopic();
        String payload = jsonParser.getJsonFromObject(message.getPayload());
        sendMessage(topic, payload);
    }

    private void sendMessage(String topic, String payload) throws MqttException {
        client.publish(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(payload.getBytes()));
        log.info("MQTT message sent to topic: {} with payload: {}", topic, payload);
    }

    /**
     * Sends messages that were previously unsent due to MQTT broker outage
     */
    public void sendUnsentMessages() {
        MqttMessage exampleMessage = new MqttMessage()
                .setProcessed(false)
                .setDirection(MqttMessage.Direction.OUTGOING);
        List<MqttMessage> messageListDb = messageDbService.getMessagesByExample(exampleMessage);
        if (!messageListDb.isEmpty()) {
            log.info("Sending queued outgoing messages. Total number:{}", messageListDb.size());
            List<MqttMessage> lastMessages = getLatestMessagePerUnit(messageListDb);
            lastMessages.forEach(message -> {
                try {
                    sendMessage(message);
                    message.setProcessed(true);
                    messageDbService.save(message);
                } catch (MqttException e) {
                    // Do nothing - Message will be sent in a later retry
                } catch (JsonProcessingException e) {
                    messageDbService.delete(message, "Invalid payload");
                }
            });
        }
    }

    private List<MqttMessage> getLatestMessagePerUnit(List<MqttMessage> allMessage) {
        Set<String> unitSet = allMessage.stream()
                .map(MqttMessage::getUnitID)
                .collect(Collectors.toSet());
        return unitSet.stream()
                .map(unit -> {
                    MqttMessage latestMessage = allMessage.stream()
                            .filter(message -> message.getUnitID().equals(unit))
                            .max(Comparator.comparing(MqttMessage::getTimeArrived))
                            .get();
                    return latestMessage;
                })
                .collect(Collectors.toList());
    }

    /**
     * Broadcasts a check in request to all units on the previously agreed channel
     * The units should respond by checking in to the server.
     */
    public void sendCheckinRequest() throws MqttException {
        sendMessage(properties.MQTT_TOPIC_CHECKIN_REQUEST, "");
    }

    /**
     * Handles incoming messages
     * 1. Creates a new {@link MqttMessage} with the details from the incoming mqtt message
     * 2. Saves the created message to the database
     * 3. Notifies possible subscribers
     * <p>
     * Logs the error on failure
     *
     * @param topic   - MQTT topic
     * @param message - MQTT message
     */
    public void handleIncomingMessage(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) {
        try {
            // Create new message
            Map<String, String> payloadMap =
                    jsonParser.getObjectFromJson(new String(message.getPayload()), HashMap.class);

            MqttMessage newMessage = new MqttMessage()
                    .setTimeArrived(LocalDateTime.now())
                    .setModule(payloadMap.get("module"))
                    .setDirection(MqttMessage.Direction.INCOMING)
                    .setTopic(topic)
                    .setUnitID(Unit.generateUnitId(
                            payloadMap.get("module"),
                            payloadMap.get("project"),
                            payloadMap.get("name")))
                    .setPayload(payloadMap)
                    .setProcessed(false);

            // Save message
            messageDbService.save(newMessage);

            // Send webhook notification to subscribers
            subscriptionApiService.notifySubscriberIfExists(payloadMap.get("module"));

        } catch (IOException e) {
            log.error("Error deserializing mqtt message", e);
        }
    }

}

