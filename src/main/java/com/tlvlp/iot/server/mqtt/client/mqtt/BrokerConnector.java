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
    private MqttClient client;
    private Properties properties;
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
        connectOptions.setCleanSession(true);
//        try {
//            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, null, null);
//            connectOptions.setSocketFactory(sslContext.getSocketFactory());
//        } catch (NoSuchAlgorithmException e) {
//            log.error("Critical Error! Unable to configure MQTT Client: ", e);
//            System.exit(1);
//        } catch (KeyManagementException e) {
//            log.error("Critical Error! Unable to configure MQTT Client: ", e);
//            System.exit(1);
//        }
        java.util.Properties props = new java.util.Properties();
        props.setProperty("com.ibm.ssl.keyStore", "jksFilePath.jks");
        props.setProperty("com.ibm.ssl.keyStorePassword", "jksPassword");
        connectOptions.setSSLProperties(props);


    }

    //TODO - IS THE SCHEDULED CHECKER NECESSARY?
    @Scheduled(fixedDelayString = "${MQTT_CLIENT_CONNECTION_CHECK_MS}", initialDelay = 5000)
    public void checkConnection() {
        if (!isConnectedToBroker() && !connectionInProgress) {
            log.warn("Connection lost to MQTT broker!");
            connectToBroker();
            //TODO - does it automatically resubscribe to topics?
        }
    }

    public Boolean isConnectedToBroker() {
        return client.isConnected();
    }

    public void connectToBroker() {
        connectionInProgress = true;
        log.info("Attempting to connect to MQTT broker");
        while (!isConnectedToBroker()) {
            try {
                attemptConnection();
                connectionInProgress = false;
                log.info("Connected to MQTT broker with client ID: " + properties.MQTT_CLIENT_MQTT_BROKER_USER);
            } catch (MqttException e) {
                log.debug("Error connecting to MQTT broker: {}", e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    private void attemptConnection() throws MqttException {
        System.out.println("ATTEMPTING CONNECTION");
        String result = client.connectWithResult(connectOptions).getException().getMessage();
        System.out.println("CONNECTION ERROR: " + result);
//        client.connect(connectOptions);
    }

}
