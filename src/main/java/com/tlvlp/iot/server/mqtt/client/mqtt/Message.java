package com.tlvlp.iot.server.mqtt.client.mqtt;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;


@Document(collection = "messages")
public class Message {

    public enum Direction {
        INCOMING, OUTGOING
    }

    @Id
    private LocalDateTime timeArrived;
    private String module;
    private Direction direction;
    private String topic;
    private String unitID;
    private Map<String, String> payload;
    private boolean processed;

    public Message() {
    }

    @Override
    public String toString() {
        return "{\"MqttMessageEntity\":{"
                + "\"timeArrived\":" + timeArrived
                + ", \"module\":\"" + module + "\""
                + ", \"direction\":\"" + direction + "\""
                + ", \"topic\":\"" + topic + "\""
                + ", \"unitID\":\"" + unitID + "\""
                + ", \"payload\":" + payload
                + ", \"processed\":\"" + processed + "\""
                + "}}";
    }

    public LocalDateTime getTimeArrived() {
        return timeArrived;
    }


    //TODO GETSET


}
