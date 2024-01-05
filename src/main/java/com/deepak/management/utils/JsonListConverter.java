package com.deepak.management.utils;

import com.deepak.management.service.doctorabsence.DoctorAbsenceServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Converter
public class JsonListConverter implements AttributeConverter<List<String>, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonListConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting list of strings to json", e);
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
            LOGGER.error("Error converting json to list of strings", e);
        }
        return Collections.emptyList();
    }
}