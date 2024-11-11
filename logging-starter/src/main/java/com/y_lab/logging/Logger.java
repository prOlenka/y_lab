package com.y_lab.logging;

import java.util.logging.Logger;

public class CustomLogger {
    private static final Logger logger = Logger.getLogger(CustomLogger.class.getName());

    public static Logger getLogger() {
        return logger;
    }
}

