package com.tlvlp.iot.server.mqtt.client.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class MessageDbService {

    private static final Logger log = LoggerFactory.getLogger(MessageDbService.class);
    private MessageRepository repository;

    public MessageDbService(MessageRepository repository) {
        this.repository = repository;
    }

    public Message save(Message message) {
        Message savedMessage = repository.save(message);
        log.info("Message saved: {}", savedMessage);
        return savedMessage;
    }
}
