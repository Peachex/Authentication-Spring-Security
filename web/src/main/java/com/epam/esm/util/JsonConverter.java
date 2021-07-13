package com.epam.esm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {
    public String convert(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return e.getLocalizedMessage();
        }
    }
}
