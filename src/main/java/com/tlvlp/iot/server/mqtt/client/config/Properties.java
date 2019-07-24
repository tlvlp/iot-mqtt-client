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

    @Value("${MQTT_CLIENT_MQTT_BROKER_USER}")
    public String MQTT_CLIENT_MQTT_BROKER_USER;

    @Value("${MQTT_CLIENT_DEFAULT_QOS}")
    public Integer MQTT_CLIENT_DEFAULT_QOS;


    @Value("${DB_SERVICE_NAME}")
    public String DB_SERVICE_NAME;

    @Value("${MQTT_CLIENT_DB}")
    public String MQTT_CLIENT_DB;

    @Value("${MQTT_CLIENT_DB_USER}")
    public String MQTT_CLIENT_DB_USER;

    @Value("${DB_PORT_INTERNAL}")
    public Integer DB_PORT_INTERNAL;

    @Value("${DB_USER_DB}")
    public String DB_USER_DB;


}
