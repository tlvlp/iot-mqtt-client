package com.tlvlp.iot.server.mqtt.client.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "messages")
public class Message {

    public enum Direction {
        INCOMING, OUTGOING
    }

    @Id
    private LocalDateTime timeArrived;
    private String msg;

    public Message(LocalDateTime timeArrived, String msg) {
        this.timeArrived = timeArrived;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timeArrived=" + timeArrived +
                ", msg='" + msg + '\'' +
                '}';
    }

    public LocalDateTime getTimeArrived() {
        return timeArrived;
    }

    public void setTimeArrived(LocalDateTime timeArrived) {
        this.timeArrived = timeArrived;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
