package com.tlvlp.iot.server.mqtt.client.persistence;

import com.tlvlp.iot.server.mqtt.client.mqtt.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

}
