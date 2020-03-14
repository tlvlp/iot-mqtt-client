package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Subscription service")
class SubscriptionServiceTest {

    @Mock
    private Properties properties;
    @Mock
    private MqttClient client;
    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    @DisplayName("Subscribe to topics")
    void subscribeToTopicsTest() throws MqttException {
        // given
        String topics = "topic_1, topic_2";
        given(properties.getMQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV()).willReturn(topics);
        given(properties.getMQTT_CLIENT_DEFAULT_QOS()).willReturn(0);

        // when
        subscriptionService.subscribeToTopics();

        // then
        then(properties).should().getMQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV();
        then(properties).should(times(2)).getMQTT_CLIENT_DEFAULT_QOS();
        then(client).should(times(2)).subscribe(
                anyString(), anyInt(), any(IMqttMessageListener.class)
        );
        then(messagingService).shouldHaveNoMoreInteractions();
    }
}