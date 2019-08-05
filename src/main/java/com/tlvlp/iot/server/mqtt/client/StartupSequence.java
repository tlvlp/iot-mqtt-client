package com.tlvlp.iot.server.mqtt.client;

import com.tlvlp.iot.server.mqtt.client.mqtt.BrokerConnector;
import com.tlvlp.iot.server.mqtt.client.mqtt.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupSequence {

    @Autowired
    private BrokerConnector brokerConnector;

    @Autowired
    private SubscriptionService subscriptionService;

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        brokerConnector.connectToBroker();
        subscriptionService.subscribeToTopics();
    }

}
