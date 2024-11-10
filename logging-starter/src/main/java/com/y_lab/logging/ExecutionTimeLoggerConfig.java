package com.y_lab.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Configuration
@Slf4j
public class ExecutionTimeLoggerConfig {

    @Before("execution(public * *(..))")
    public void logBeforeExecution(JoinPoint joinPoint) {
        log.info("Starting execution of: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(public * *(..))", returning = "result")
    public void logAfterExecution(JoinPoint joinPoint, Object result) {
        log.info("Completed execution of: {} with result: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(public * *(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with cause: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }
}
