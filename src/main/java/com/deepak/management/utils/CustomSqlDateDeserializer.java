package com.deepak.management.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomSqlDateDeserializer extends StdDeserializer<Date> {
    private static final SimpleDateFormat[] DATE_FORMATS = {
            new SimpleDateFormat("dd-MM-yyyy"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSqlDateDeserializer.class);

    public CustomSqlDateDeserializer() {
        this(null);
    }

    public CustomSqlDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        JsonNode node = jsonparser.getCodec().readTree(jsonparser);
        String date = node.asText();

        for (SimpleDateFormat dateFormat : DATE_FORMATS) {
            try {
                java.util.Date utilDate = dateFormat.parse(date);
                return new Date(utilDate.getTime());
            } catch (ParseException e) {
                // Log the parsing attempt with this format
                LOGGER.debug("Failed to parse date '{}' with format '{}'", date, dateFormat.toPattern());
            }
        }

        // Log an error if none of the formats matched
        LOGGER.error("Error when Parsing Date: Cannot parse date '{}'", date);
        throw new IOException("Unparseable date: " + date);
    }
}