package com.y_lab.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger {
    private final Logger logger = Logger.getLogger(CustomLogger.class.getName());

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public void error(String message, Object param, Throwable throwable) {
        logger.log(Level.SEVERE, message.replace("{}", param.toString()), throwable);
    }
}


