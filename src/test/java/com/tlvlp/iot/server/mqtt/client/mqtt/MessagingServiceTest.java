package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import com.tlvlp.iot.server.mqtt.client.persistence.MessageDbService;
import com.tlvlp.iot.server.mqtt.client.rpc.IncomingMessageForwarder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Messaging service")
class MessagingServiceTest {

    @Mock
    private MqttClient client;
    @Mock
    private MessageDbService messageDbService;
    @Mock
    private IncomingMessageForwarder forwarder;

    @InjectMocks
    private MessagingService messagingService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private Message message;
    private MqttMessage mqttMessage;
    private String topic = "topic";
    private Map<String, String> payloadMap;
    private byte[] payloadBytes;

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        topic = "topic";
        payloadMap = Collections.singletonMap("k", "v");
        message = new Message()
                .setTopic(topic)
                .setPayload(payloadMap);
        payloadBytes = om.writeValueAsBytes(payloadMap);
        mqttMessage = new MqttMessage(payloadBytes);
    }

    @Test
    @DisplayName("Handle an incoming message that is correct")
    void handleIncomingMessageCorrect() throws JsonProcessingException {
        // when
        messagingService.handleIncomingMessage(topic, mqttMessage);

        // then
        then(messageDbService).should().save(messageCaptor.capture());
        then(forwarder).should().forwardMessage(any(Message.class));

        Message updatedMessage = messageCaptor.getValue();
        assertNotNull(updatedMessage);
        assertNotNull(updatedMessage.getTimeArrived(), "Timestamp is added");
        assertEquals(updatedMessage.getDirection(), Message.Direction.INCOMING, "Direction is added");
        assertEquals(updatedMessage.getTopic(), topic, "Topic remains unchanged");
        assertEquals(updatedMessage.getPayload(), payloadMap, "Payload remains unchanged");
    }


    @Test
    void handleOutgoingMessage() throws MqttException {
        // when
        messagingService.handleOutgoingMessage(message);

        // then
        then(messageDbService).should().save(messageCaptor.capture());
        Message updatedMessage = messageCaptor.getValue();
        assertNotNull(updatedMessage);
        assertNotNull(updatedMessage.getTimeArrived(), "Timestamp is added");
        assertEquals(updatedMessage.getDirection(), Message.Direction.OUTGOING, "Direction is added");
        assertEquals(updatedMessage.getTopic(), topic, "Topic remains unchanged");
        assertEquals(updatedMessage.getPayload(), payloadMap, "Payload remains unchanged");
    }

    @Test
    @DisplayName("Handle an incorrect outgoing message where the topic is null")
    void handleOutgoingMessageNullTopic() {
        message.setTopic(null);
        assertThrows(IllegalArgumentException.class,
                () -> messagingService.handleOutgoingMessage(message),
                "null topic should throw an exception");
        then(messageDbService).shouldHaveZeroInteractions();
    }

    @Test
    @DisplayName("Handle an incorrect outgoing message where the payload is null")
    void handleOutgoingMessageNullPayload() {
        message.setPayload(null);
        assertThrows(IllegalArgumentException.class,
                () -> messagingService.handleOutgoingMessage(message),
                "null payload should throw an exception");
        then(messageDbService).shouldHaveZeroInteractions();
    }
}