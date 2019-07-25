package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private MessagingService messagingService;
    private MqttClient client;
    private Properties properties;

    public SubscriptionService(MessagingService messagingService, MqttClient client, Properties properties) {
        this.messagingService = messagingService;
        this.client = client;
        this.properties = properties;
    }

    public void subscribeToTopics() {
        List<String> topics = Arrays.asList(
                properties.MCU_MQTT_TOPIC_GLOBAL_STATUS,
                properties.MCU_MQTT_TOPIC_GLOBAL_INACTIVE);
        topics.forEach(topic -> subscribe(topic, properties.MQTT_CLIENT_DEFAULT_QOS, getDefaultMqttMessageListener()));
    }

    private IMqttMessageListener getDefaultMqttMessageListener() {
        return (String topic, MqttMessage message) -> messagingService.handleIncomingMessage(topic, message);
    }

    private void subscribe(String topic, int qos, IMqttMessageListener messageListener) {
        try {
            client.subscribe(topic, qos, messageListener);
            log.info("MQTT broker subscription added for topic: {}", topic);
        } catch (MqttException e) {
            log.error("Error adding subscription to MQTT broker for topic: {}", topic, e.getMessage());
        }
    }

}
