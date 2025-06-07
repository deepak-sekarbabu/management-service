package com.deepak.management.config;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class LoggingConfig {

  @Autowired private Environment environment;

  @PostConstruct
  public void init() {
    try {
      // Determine the base path for logs
      String basePath = environment.getProperty("logging.file.path", "/app/logs");
      Path logsPath = Paths.get(basePath);

      // Create logs directory
      if (!Files.exists(logsPath)) {
        Files.createDirectories(logsPath);
      }

      // Create archived directory
      Path archivedPath = logsPath.resolve("archived");
      if (!Files.exists(archivedPath)) {
        Files.createDirectories(archivedPath);
      }

      // Set appropriate permissions
      try {
        Files.setPosixFilePermissions(
            logsPath,
            java.util.EnumSet.of(
                java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                java.nio.file.attribute.PosixFilePermission.OWNER_WRITE,
                java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE,
                java.nio.file.attribute.PosixFilePermission.GROUP_READ,
                java.nio.file.attribute.PosixFilePermission.GROUP_WRITE,
                java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE));
      } catch (UnsupportedOperationException e) {
        // Ignore on Windows or if POSIX permissions are not supported
      }
    } catch (Exception e) {
      System.err.println("Failed to create logs directory: " + e.getMessage());
    }
  }
}
