package com.tlvlp.iot.server.mqtt.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_CLIENT_BROKER_URI}")
    public String MQTT_CLIENT_BROKER_URI;

    @Value("${MQTT_CLIENT_DEFAULT_QOS}")
    public Integer MQTT_CLIENT_DEFAULT_QOS;

    @Value("${MQTT_CLIENT_MQTT_BROKER_USER}")
    public String MQTT_CLIENT_MQTT_BROKER_USER;

    @Value("${MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED}")
    public String MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED;

    @Value("${MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV}")
    public String MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV;

    @Value("${UNIT_SERVICE_API_INCOMING_MESSAGE_URL}")
    public String UNIT_SERVICE_API_INCOMING_MESSAGE_URL;

}
