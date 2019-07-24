package com.tlvlp.iot.server.mqtt.client.persistence;

import com.tlvlp.iot.server.mqtt.client.mqtt.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

}
