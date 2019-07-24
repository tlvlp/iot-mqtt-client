package com.tlvlp.iot.server.mqtt.client.mqtt;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String module;
    private String hookUri;
    private LocalDateTime lastUpdated;
    private Set<String> topics;


    public Subscription() {
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "{\"Subscription\":{"
                + "\"module\":\"" + module + "\""
                + ", \"hookUri\":\"" + hookUri + "\""
                + ", \"lastUpdated\":" + lastUpdated
                + ", \"topics\":" + topics
                + "}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(module, that.module) &&
                Objects.equals(hookUri, that.hookUri) &&
                Objects.equals(lastUpdated, that.lastUpdated) &&
                Objects.equals(topics, that.topics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, hookUri, lastUpdated, topics);
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Subscription setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public String getModule() {
        return module;
    }

    public Subscription setModule(String module) {
        this.module = module;
        return this;
    }

    public String getHookUri() {
        return hookUri;
    }

    public Subscription setHookUri(String hookUri) {
        this.hookUri = hookUri;
        return this;
    }

    public Set<String> getTopics() {
        if (topics != null) {
            return topics;
        } else {
            return new HashSet<>();
        }
    }

    public Subscription setTopics(Set<String> topics) {
        this.topics = topics;
        return this;
    }

    public Subscription appendTopics(Collection<String> newTopics) {
        if (topics == null) {
            this.topics = new HashSet<>();
        }
        this.topics.addAll(newTopics);
        return this;
    }

    public Subscription removeTopics(Collection<String> removeTopics) {
        if (topics != null) {
            this.topics.removeAll(removeTopics);
        }
        return this;
    }
}
