package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BrokerConnector {

    private static final Logger log = LoggerFactory.getLogger(BrokerConnector.class);
    private MqttClient client;
    private MqttConnectOptions connectOptions;


    public BrokerConnector(MqttClient client, Properties properties) {
        this.client = client;
        connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(properties.getMQTT_CLIENT_MQTT_BROKER_USER());
        connectOptions.setPassword(properties.getMQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED().toCharArray());
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setConnectionTimeout(30);
        connectOptions.setKeepAliveInterval(30);
        connectOptions.setCleanSession(false);
    }

    public void connectToBroker() {
        log.info("Connecting to MQTT broker");
        while (!client.isConnected()) {
            try {
                client.connect(connectOptions);
                log.info("Connected to MQTT broker with client ID: " + client.getClientId());
            } catch (MqttException e) {
                log.debug("Error connecting to MQTT broker: ", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    log.warn("Sleep interrupted while waiting for MQTT broker connection. ", ie);
                }
            }
        }
    }
}
