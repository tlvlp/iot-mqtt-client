package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private Properties properties;
    private MqttClient client;
    private MessagingService messagingService;

    public SubscriptionService(Properties properties, MqttClient client, MessagingService messagingService) {
        this.properties = properties;
        this.client = client;
        this.messagingService = messagingService;
    }

    public void subscribeToTopics() {
        String[] topics = properties.MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV.split(",");
        for (String topic : topics) {
            subscribe(topic, properties.MQTT_CLIENT_DEFAULT_QOS, getDefaultMqttMessageListener());
        }
    }

    private IMqttMessageListener getDefaultMqttMessageListener() {
        return (String topic, MqttMessage message) -> messagingService.handleIncomingMessage(topic, message);
    }

    private void subscribe(String topic, int qos, IMqttMessageListener messageListener) {
        try {
            client.subscribe(topic, qos, messageListener);
            log.info("MQTT broker subscription added for topic: {}", topic);
        } catch (MqttException e) {
            log.error("Error adding subscription to MQTT broker for topic: {} \n{}", topic, e.getMessage());
        }
    }

}
