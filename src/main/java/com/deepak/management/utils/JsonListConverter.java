package com.deepak.management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class JsonListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Handle the exception according to your needs
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData != null) {
                return objectMapper.readValue(dbData, List.class);
            }
        } catch (JsonProcessingException e) {
            // Handle the exception according to your needs
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}