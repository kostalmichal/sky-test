package com.kostalmichal.sky.domain.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kostalmichal.sky.exception.InvalidEmailValueException;

import java.io.IOException;

public class EmailDeserializer extends JsonDeserializer<Email> {
    @Override
    public Email deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        try {
            return new Email(value);
        } catch (InvalidEmailValueException e) {
            throw new JsonMappingException(p, "Neplatný email: " + value, e);
        }
    }
}