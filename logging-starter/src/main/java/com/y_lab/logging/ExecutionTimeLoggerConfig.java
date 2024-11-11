package com.y_lab.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Aspect
@Configuration
public class ExecutionTimeLoggerConfig {

    private static final Logger logger = Logger.getLogger(ExecutionTimeLoggerConfig.class.getName());

    @Before("execution(public * *(..))")
    public void logBeforeExecution(JoinPoint joinPoint) {
        logger.info(String.format("Starting execution of: %s with arguments: %s",
                joinPoint.getSignature().getName(), joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(public * *(..))", returning = "result")
    public void logAfterExecution(JoinPoint joinPoint, Object result) {
        logger.info(String.format("Completed execution of: %s with result: %s",
                joinPoint.getSignature().getName(), result));
    }

    @AfterThrowing(pointcut = "execution(public * *(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        logger.severe(String.format("Exception in method: %s with cause: %s",
                joinPoint.getSignature().getName(), exception.getMessage()));
    }
}

