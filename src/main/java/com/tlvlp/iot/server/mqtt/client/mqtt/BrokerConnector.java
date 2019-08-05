package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BrokerConnector {

    private static final Logger log = LoggerFactory.getLogger(BrokerConnector.class);
    private Properties properties;
    private MqttClient client;
    private MqttConnectOptions connectOptions;
    private Boolean connectionInProgress;


    public BrokerConnector(MqttClient client, Properties properties) {
        this.client = client;
        this.properties = properties;
        connectionInProgress = false;
        connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(properties.MQTT_CLIENT_MQTT_BROKER_USER);
        connectOptions.setPassword(properties.MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED.toCharArray());
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setConnectionTimeout(30);
        connectOptions.setKeepAliveInterval(30);
        connectOptions.setCleanSession(false);
    }

    //TODO - IS THE SCHEDULED CHECKER NECESSARY?
    @Scheduled(fixedDelayString = "${MQTT_CLIENT_CONNECTION_CHECK_MS}", initialDelay = 5000)
    public void checkConnection() {
        if (!client.isConnected() && !connectionInProgress) {
            log.warn("Connection lost to MQTT broker!");
            connectToBroker();
            //TODO - does it automatically resubscribe to topics?
        }
    }

    public void connectToBroker() {
        connectionInProgress = true;
        log.info("Connecting to MQTT broker");
        while (!client.isConnected()) {
            try {
                client.connect(connectOptions);
                connectionInProgress = false;
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
