package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import com.tlvlp.iot.server.mqtt.client.persistence.SubscriptionDbService;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private MessagingService messagingService;
    @Autowired
    private SubscriptionDbService subscriptionEntityService;
    @Autowired
    private MqttClient client;
    @Autowired
    private Properties properties;

    private IMqttMessageListener getDefaultMqttMessageListener() {
        return (String topic, MqttMessage message) -> messagingService.handleIncomingMessage(topic, message);
    }

    /**
     * Subscribes to the global topics where all the units send information to the server
     */
    public void addApiSubscriptions() {
        List<String> apiTopics = Arrays.asList(properties.MCU_MQTT_TOPIC_CHECKIN, properties.MCU_MQTT_TOPIC_CHECKOUT);
        subscribeToAll(apiTopics);
    }

    /**
     * Subscribes to the global topics where all the units send information to the server
     */
    public void addUnitSubscriptions() {
        // Retrieve subscriptions from the DB
        List<String> unitTopics = subscriptionEntityService.findAllSubscription()
                .stream()
                .map(subscription -> subscription.getTopics())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        // Subscribe to all
        subscribeToAll(unitTopics);
    }

    public void subscribeToAll(Collection<String> topics) {
        topics.forEach(topic -> subscribe(topic, properties.MQTT_CLIENT_DEFAULT_QOS, getDefaultMqttMessageListener()));
    }

    /**
     * Subscribes to an mqtt topic at the broker
     *
     * @param topic
     * @param qos
     * @param messageListener
     */
    private void subscribe(String topic, int qos, IMqttMessageListener messageListener) {
        try {
            client.subscribe(topic, qos, messageListener);
            log.info("MQTT broker subscription added for topic: {}", topic);
        } catch (MqttException e) {
            log.error("Error adding subscription to MQTT broker for topic: {}", topic, e.getMessage());
        }
    }

    /**
     * Unsubscribes from an mqtt topic at the broker
     *
     * @param topic
     */
    private void unsubscribe(String topic) {
        try {
            client.unsubscribe(topic);
            log.info("MQTT broker subscription removed for topic: {}", topic);
        } catch (MqttException e) {
            log.error("Error removing subscription from MQTT broker for topic: {}", topic, e);
        }
    }

}
