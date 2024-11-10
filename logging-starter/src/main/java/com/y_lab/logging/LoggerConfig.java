package com.y_lab.logging;

import lombok.Getter;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    @Getter
    private static final Logger timeLogger = Logger.getLogger("TimeLogger");
    @Getter
    private static final Logger auditLogger = Logger.getLogger("AuditLogger");

    static {
        configureLogger(timeLogger, "execution-time.log");
        configureLogger(auditLogger, "audit.log");
    }

    private static void configureLogger(Logger logger, String fileName) {
        try {
            FileHandler fileHandler = new FileHandler(fileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
