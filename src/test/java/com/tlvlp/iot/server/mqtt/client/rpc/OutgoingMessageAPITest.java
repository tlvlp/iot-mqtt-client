package com.tlvlp.iot.server.mqtt.client.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlvlp.iot.server.mqtt.client.mqtt.MessagingService;
import com.tlvlp.iot.server.mqtt.client.persistence.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Outgoing Message API Test")
class OutgoingMessageAPITest {

    private MockMvc mockMvc;

    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private OutgoingMessageAPI outgoingMessageAPI;

    private Message message;
    private String messageJSON;

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        message = new Message().setTopic("/topic").setPayload(Map.of());
        mockMvc = MockMvcBuilders.standaloneSetup(outgoingMessageAPI)
                .addPlaceholderValue("MQTT_CLIENT_API_OUTGOING_MESSAGE", "/endpoint")
                .build();
    }

    @Test
    @DisplayName("Post message - Valid")
    void postMessageValid() throws Exception {
        mockMvc.perform(post("/endpoint")
                .content(new ObjectMapper().writeValueAsString(message))
                .contentType("application/json"))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Post message - Missing topic")
    void postMessageMissingTopic() throws Exception {
        message.setTopic(null);
        mockMvc.perform(post("/endpoint")
                .content(new ObjectMapper().writeValueAsString(message))
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Post message - Missing payload")
    void postMessageMissingPayload() throws Exception {
        message.setPayload(null);
        mockMvc.perform(post("/endpoint")
                .content(new ObjectMapper().writeValueAsString(message))
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

}