package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Client {

    @Autowired
    private Properties properties;

    @Bean
    public MqttClient getMqttClient() throws MqttException {
        String brokerUri = String.format("%s:%s", properties.MQTT_BROKER_SERVICE_NAME, properties.MQTT_BROKER_PORT);
        return new MqttClient(brokerUri, properties.MQTT_CLIENT_MQTT_BROKER_USER);
    }
}
