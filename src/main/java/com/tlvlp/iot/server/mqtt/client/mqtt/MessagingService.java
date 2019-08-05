package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import com.tlvlp.iot.server.mqtt.client.rpc.MessageForwarder;
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

//    private MqttClient client;
    private MessageDbService messageDbService;
    private MessageForwarder forwarder;
    private JsonParser jsonParser;
    private Properties properties;

    public MessagingService(MessageDbService messageDbService, MessageForwarder forwarder,
                            JsonParser jsonParser, Properties properties) {
        this.messageDbService = messageDbService;
        this.forwarder = forwarder;
        this.jsonParser = jsonParser;
        this.properties = properties;
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
                    .setModule(payloadMap.get("module"))
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

    private boolean isValidMessage(Message message) {
        return message.getModule() != null && message.getUnitID() != null;
    }

//    /**
//     * Sends a message to a selected topic
//     */
//    public void handleOutgoingMessage(Message message) throws MqttException, JsonProcessingException {
//        if (!message.getDirection().equals(Message.Direction.OUTGOING)) {
//            String error =
//                    String.format("Error sending MQTT message: essage direction must be %s", Message.Direction.OUTGOING);
//            log.error(error);
//            throw new IllegalArgumentException(error);
//        }
//        message.setTimeArrived(LocalDateTime.now());
//        try {
//            sendMessage(message);
//            message.setProcessed(true);
//            messageDbService.save(message);
//        } catch (MqttException e) {
//            message.setProcessed(false);
//            messageDbService.save(message);
//            log.warn("Warning! Connection lost to MQTT broker. Message queued: {}", message);
//            throw e;
//        } catch (JsonProcessingException e) {
//            log.error("Error sending MQTT message: {}", e.getMessage());
//            throw e;
//        }
//    }
//
//    private void sendMessage(Message message) throws MqttException, JsonProcessingException {
//        String topic = message.getTopic();
//        String payload = jsonParser.getJsonFromObject(message.getPayload());
//        client.publish(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(payload.getBytes()));
//        log.info("MQTT message sent to topic: {} with payload: {}", topic, payload);

//    }
//    /**
//     * Sends messages that were previously unsent due to MQTT broker outage
//     */
//    public void sendUnsentMessages() {
//        Message exampleMessage = new Message()
//                .setProcessed(false)
//                .setDirection(Message.Direction.OUTGOING);
//        List<Message> messageListDb = messageDbService.getMessagesByExample(exampleMessage);
//        if (!messageListDb.isEmpty()) {
//            log.info("Sending queued outgoing messages. Total number:{}", messageListDb.size());
//            List<Message> lastMessages = getLatestMessagePerUnit(messageListDb);
//            lastMessages.forEach(message -> {
//                try {
//                    sendMessage(message);
//                    message.setProcessed(true);
//                    messageDbService.save(message);
//                } catch (MqttException e) {
//                    // Do nothing - Message will be sent in a later retry
//                } catch (JsonProcessingException e) {
//                    messageDbService.delete(message, "Invalid payload");
//                }
//            });
//        }

//    }
//    private List<MqttMessage> getLatestMessagePerUnit(List<MqttMessage> allMessage) {
//        Set<String> unitSet = allMessage.stream()
//                .map(MqttMessage::getUnitID)
//                .collect(Collectors.toSet());
//        return unitSet.stream()
//                .map(unit -> {
//                    MqttMessage latestMessage = allMessage.stream()
//                            .filter(message -> message.getUnitID().equals(unit))
//                            .max(Comparator.comparing(MqttMessage::getTimeArrived))
//                            .get();
//                    return latestMessage;
//                })
//                .collect(Collectors.toList());

//    }

}

