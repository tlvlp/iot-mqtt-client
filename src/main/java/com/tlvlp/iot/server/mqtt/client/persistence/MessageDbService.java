package com.tlvlp.iot.server.mqtt.client.persistence;

import com.tlvlp.iot.server.mqtt.client.mqtt.Message;
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


    //TODO REMOVE UNUSED METHODS
//    public List<Message> getMessagesByExample(Message exampleMessage) {
//        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
//        Example<Message> exampleQuery = Example.of(exampleMessage, matcher);
//        return repository.findAll(exampleQuery);
//    }

//    public void delete(Message message, @Nullable String reason) {
//        repository.delete(message);
//        log.info("Message deleted({}): {}", reason, message);
//    }
//
//    public void delete(Collection<Message> messages, @Nullable String reason) {
//        repository.deleteAll(messages);
//        log.info("Messages deleted({}): {}", reason, messages);
//    }
}
