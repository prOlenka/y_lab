package com.y_lab.aspect;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class ValidationAspect {

    private final Validator validator;

    public ValidationAspect(Validator validator) {
        this.validator = validator;
    }

    @Before("@within(com.y_lab.annotation.EnableAudit)")
    public void validate(JoinPoint joinPoint) throws ConstraintViolationException {
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }
            }
        }
    }
}
