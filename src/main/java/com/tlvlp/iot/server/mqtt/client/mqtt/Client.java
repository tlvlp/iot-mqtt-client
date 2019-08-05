package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class Client {

    private Properties properties;

    public Client(Properties properties) {
        this.properties = properties;
    }

    @Bean
    public MqttClient getMqttClient() throws MqttException {

        String brokerUri = String.format("tcp://%s:%s",
                properties.MQTT_BROKER_SERVICE_NAME,
                properties.MQTT_BROKER_PORT_INTERNAL);

        String randomizedClientID = String.format("%s_%s",
                properties.MQTT_CLIENT_MQTT_BROKER_USER,
                LocalDateTime.now().hashCode());

        return new MqttClient(brokerUri, randomizedClientID);
    }

}
