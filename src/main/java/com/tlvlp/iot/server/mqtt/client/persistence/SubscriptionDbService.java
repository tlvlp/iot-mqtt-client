package com.tlvlp.iot.server.mqtt.client.persistence;

import com.tlvlp.iot.server.mqtt.client.mqtt.Subscription;
import com.tlvlp.iot.server.mqtt.client.mqtt.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionDbService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionDbService.class);
    @Autowired
    private SubscriptionRepository repository;
    @Autowired
    private SubscriptionService subscriptionService;

    public Subscription save(Subscription subscription) {
        Subscription updatedSubscription = repository.save(subscription);
        log.info("Subscription saved: {}", updatedSubscription.toString()); //TODO - correct logs to include details properly
        return updatedSubscription;
    }

    public Optional<Subscription> findSubscription(String unit) {
        return repository.findById(unit);
    }

    /**
     * Appends unit specific topics to an existing subscription
     *
     * @param newTopics unit specific mqtt topics
     * @param unit      the unit's id = unit name
     * @return
     */
    public Boolean addTopicsToSubscription(Collection<String> newTopics, String unit) {
        Optional<Subscription> subscriptionLocal = findSubscription(unit);
        if (subscriptionLocal.isPresent()) {
            // Add new topics to the database
            Subscription subscription = subscriptionLocal.get();
            subscription.appendTopics(newTopics);
            log.info("Adding new topics to the existing subscription: {}", unit);
            save(subscription);
            // Subscribe to new topics
            subscriptionService.subscribeToAll(newTopics);
            return true;
        } else {
            return false;
        }
    }

    public List<Subscription> findAllSubscription() {
        return repository.findAll();
    }
}
