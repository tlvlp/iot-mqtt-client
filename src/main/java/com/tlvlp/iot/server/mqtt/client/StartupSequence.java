package com.tlvlp.iot.server.mqtt.client;

import com.tlvlp.iot.server.mqtt.client.mqtt.BrokerConnector;
import com.tlvlp.iot.server.mqtt.client.mqtt.SubscriptionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupSequence {

    private BrokerConnector brokerConnector;
    private SubscriptionService subscriptionService;

    public StartupSequence(BrokerConnector brokerConnector, SubscriptionService subscriptionService) {
        this.brokerConnector = brokerConnector;
        this.subscriptionService = subscriptionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        brokerConnector.connectToBroker();
        subscriptionService.subscribeToTopics();
    }

}
