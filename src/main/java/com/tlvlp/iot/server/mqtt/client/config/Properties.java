package com.tlvlp.iot.server.mqtt.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_CLIENT_BROKER_URI}")
    private String MQTT_CLIENT_BROKER_URI;

    @Value("${MQTT_CLIENT_DEFAULT_QOS}")
    private Integer MQTT_CLIENT_DEFAULT_QOS;

    @Value("${MQTT_CLIENT_MQTT_BROKER_USER}")
    private String MQTT_CLIENT_MQTT_BROKER_USER;

    @Value("${MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED}")
    private String MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED;

    @Value("${MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV}")
    private String MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV;

    @Value("${API_GATEWAY_NAME}")
    private String API_GATEWAY_NAME;

    @Value("${API_GATEWAY_PORT}")
    private String API_GATEWAY_PORT;

    @Value("${API_GATEWAY_API_INCOMING_MQTT_MESSAGE}")
    private String API_GATEWAY_API_INCOMING_MQTT_MESSAGE;


    public String getMQTT_CLIENT_BROKER_URI() {
        return MQTT_CLIENT_BROKER_URI;
    }

    public Integer getMQTT_CLIENT_DEFAULT_QOS() {
        return MQTT_CLIENT_DEFAULT_QOS;
    }

    public String getMQTT_CLIENT_MQTT_BROKER_USER() {
        return MQTT_CLIENT_MQTT_BROKER_USER;
    }

    public String getMQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED() {
        return MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED;
    }

    public String getMQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV() {
        return MQTT_CLIENT_TOPIC_SUBSCRIPTIONS_CSV;
    }

    public String getAPI_GATEWAY_NAME() {
        return API_GATEWAY_NAME;
    }

    public String getAPI_GATEWAY_PORT() {
        return API_GATEWAY_PORT;
    }

    public String getAPI_GATEWAY_API_INCOMING_MQTT_MESSAGE() {
        return API_GATEWAY_API_INCOMING_MQTT_MESSAGE;
    }
}
