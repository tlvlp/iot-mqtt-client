package com.tlvlp.iot.server.mqtt.client.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
@DisplayName("Outgoing Message API Test")
class OutgoingMessageAPITest {

    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private OutgoingMessageAPI outgoingMessageAPI;

    @Captor
    ArgumentCaptor<Message> messageCaptor;

    private Message validIncomingMessage;

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        validIncomingMessage = new Message()
                .setTopic("/topic")
                .setPayload(Map.of());
    }

    @Test
    @DisplayName("receiving a valid message to be posted")
    void postMessageValid() throws Exception {
        // when
        ResponseEntity response = outgoingMessageAPI.postMessage(validIncomingMessage);
        // then
        then(messagingService).should().handleOutgoingMessage(validIncomingMessage);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }


}