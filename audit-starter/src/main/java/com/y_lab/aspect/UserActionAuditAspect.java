package com.y_lab.aspect;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@NoArgsConstructor
@Component
@Slf4j
public class UserActionAuditAspect {

    @Pointcut("@annotation(com.y_lab.annotation.EnableAudit)")
    public void enableAuditMethods() {}

    @Before("enableAuditMethods()")
    public void logAction(JoinPoint joinPoint) {
        log.info("Пользователь {} выполняет действие {} с параметрами: {}",
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "enableAuditMethods()", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        log.info("Действие {} пользователя завершилось успешно. Результат: {}",
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "enableAuditMethods()", throwing = "exception")
    public void logError(JoinPoint joinPoint, Throwable exception) {
        log.error("Ошибка при выполнении действия {}: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }
}
