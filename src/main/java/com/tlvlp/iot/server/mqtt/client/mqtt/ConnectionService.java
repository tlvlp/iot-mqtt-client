package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.tlvlp.iot.server.mqtt.client.config.Properties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);

    @Autowired
    private MessagingService messagingService;
    @Autowired
    private MqttClient client;
    @Autowired
    private Properties properties;

    private Boolean connectionInProgress = false;


    /**
     * Returns the status of the MQTT client's connection to the broker
     *
     * @return true if connected
     */
    public Boolean isConnectedToBroker() {
        return client.isConnected();
    }

    /**
     * Attempts to connect to the MQTT broker until it succeeds
     */
    public void connectToBroker() {
        connectionInProgress = true;
        log.info("Attempting to connect to MQTT broker");
        while (!isConnectedToBroker()) {
            try {
                attemptConnection();
                connectionInProgress = false;
                log.info("Connected to MQTT broker with client ID: " + properties.MQTT_CLIENT_MQTT_BROKER_USER);
                messagingService.sendUnsentMessages();
            } catch (MqttException e) {
                log.debug("Error connecting to MQTT broker: {}", e.getMessage());
            }
        }

    }

    /**
     * Attempts to connect to the MQTT broker
     *
     * @throws MqttException is thrown when the connection fails
     */
    private void attemptConnection() throws MqttException {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(properties.MQTT_CLIENT_MQTT_BROKER_USER);
        connectOptions.setPassword(properties.MQTT_CLIENT_MQTT_BROKER_PASS_SECRET_FILE_PARSED.toCharArray());
        connectOptions.setAutomaticReconnect(true);
        client.connect(connectOptions);
    }

    /**
     * Scheduled service that makes sure that the client remains connected to the MQTT broker
     * Outgoing messages received from other services during the outage are sent upon reconnection.
     */
    @Scheduled(fixedDelayString = "${mqtt.connection-check-frequency}", initialDelay = 5000)
    public void checkConnection() {
        if (!isConnectedToBroker() && !connectionInProgress) {
            log.warn("Connection lost to MQTT broker!");
            connectToBroker();
        }
    }


}
