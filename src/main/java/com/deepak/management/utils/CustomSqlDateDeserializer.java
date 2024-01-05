package com.deepak.management.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomSqlDateDeserializer extends StdDeserializer<Date> {
    private static final DateTimeFormatter[] DATE_FORMATS = {DateTimeFormatter.ofPattern("dd-MM-yyyy"), DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")};
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSqlDateDeserializer.class);

    public CustomSqlDateDeserializer() {
        this(null);
    }

    public CustomSqlDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String date = parser.getValueAsString(); // Get the date as a String directly

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                LocalDate localDate = LocalDate.parse(date, formatter);
                return Date.valueOf(localDate);
            } catch (DateTimeParseException e) {
                // Log the parsing attempt
            }
        }
        // Log an error if none of the formats matched
        LOGGER.error("Error when Parsing Date: Cannot parse date '{}'", date);
        throw new IOException("Unparseable date: " + date);
    }
}
