package com.tlvlp.iot.server.mqtt.client.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsonParser {

    ObjectMapper objectMapper;

    public JsonParser() {
        this.objectMapper = new ObjectMapper();
    }

    public <T> T getObjectFromJson(String json, Class<T> targetClass) throws IOException {
        return objectMapper.readValue(json, targetClass);
    }

    public <T> String getJsonFromObject(T objectToParse) throws JsonProcessingException {
        return objectMapper.writeValueAsString(objectToParse);
    }

}
