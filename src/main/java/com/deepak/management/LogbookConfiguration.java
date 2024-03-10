package com.deepak.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.*;

import java.io.IOException;

@Configuration
public class LogbookConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbookConfiguration.class);

    public static Logbook createLogbook() {
        return Logbook.builder().strategy((Strategy) new CustomLoggingStrategy()).build();
    }

    private static class CustomLoggingStrategy implements HttpLogFormatter, HttpLogWriter {

        public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
            return String.format("[INFO] Incoming Request: %s %s", precorrelation.getId(), request.getRequestUri());
        }

        public String format(Correlation correlation, HttpResponse response) {
            return String.format("[INFO] Outgoing Response: %s %d", correlation.getId(), response.getStatus());
        }

        public void write(final Precorrelation precorrelation, final HttpRequest request) throws IOException {
            LOGGER.info(format(precorrelation, request));
        }

        public void write(final Correlation correlation, final HttpResponse response) throws IOException {
            LOGGER.info(format(correlation, response));
        }

        @Override
        public void write(Precorrelation precorrelation, String request) throws IOException {
            // Empty implementation
        }

        @Override
        public void write(Correlation correlation, String response) throws IOException {
            // Empty implementation
        }
    }
}