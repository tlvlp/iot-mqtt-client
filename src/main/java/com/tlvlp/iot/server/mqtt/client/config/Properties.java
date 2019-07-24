package com.tlvlp.iot.server.mqtt.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_BROKER_SERVICE_NAME}")
    public String MQTT_BROKER_SERVICE_NAME;

    @Value("${MQTT_BROKER_PORT}")
    public Integer MQTT_BROKER_PORT;

    @Value("${MQTT_CLIENT_DEFAULT_QOS}")
    public Integer MQTT_CLIENT_DEFAULT_QOS;

    @Value("${MQTT_CLIENT_MQTT_BROKER_USER}")
    public String MQTT_CLIENT_MQTT_BROKER_USER;

    @Value("${MCU_MQTT_TOPIC_CHECKIN}")
    public String MCU_MQTT_TOPIC_CHECKIN;

    @Value("${MCU_MQTT_TOPIC_CHECKOUT}")
    public String MCU_MQTT_TOPIC_CHECKOUT;

    @Value("${MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED}")
    public String MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED;

}
