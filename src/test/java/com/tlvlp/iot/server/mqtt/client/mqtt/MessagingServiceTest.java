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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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
    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private MessagingService messagingService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private Message message;
    private MqttMessage mqttMessage;
    private String topic = "topic";
    private Map<String, String> payloadMap;

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        topic = "topic";
        payloadMap = Collections.singletonMap("k", "v");
        message = new Message()
                .setTopic(topic)
                .setPayload(payloadMap);
        mqttMessage = new MqttMessage(new byte[0]);
    }

    @Test
    @DisplayName("Handle an incoming message that is correct")
    void handleIncomingMessage() throws IOException {
        // given
        given(objectMapperMock.readValue(anyString(), any(TypeReference.class))).willReturn(payloadMap);

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
    void handleOutgoingMessage() throws MqttException, IOException, InvalidMessageException {
        var payload = new byte[0];
        // given
        given(objectMapperMock.writeValueAsBytes(anyMap())).willReturn(payload);

        // when
        messagingService.handleOutgoingMessage(message);

        // then
        then(objectMapperMock).should().writeValueAsBytes(anyMap());
        then(client).should().publish(anyString(), any(MqttMessage.class));
        then(messageDbService).should().save(messageCaptor.capture());
        Message updatedMessage = messageCaptor.getValue();
        assertNotNull(updatedMessage);
        assertNotNull(updatedMessage.getTimeArrived(), "Timestamp is added");
        assertEquals(updatedMessage.getDirection(), Message.Direction.OUTGOING, "Direction is added");
        assertEquals(updatedMessage.getTopic(), topic, "Topic remains unchanged");
        assertEquals(updatedMessage.getPayload(), payloadMap, "Payload remains unchanged");
    }

}