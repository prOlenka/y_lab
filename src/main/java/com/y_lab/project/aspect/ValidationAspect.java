package com.y_lab.project.aspect;

import jakarta.validation.ValidationException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import jakarta.validation.*;
import java.util.Set;

@Aspect
@Component
public class ValidationAspect {
    private final Validator validator;

    public ValidationAspect(Validator validator) {
        this.validator = validator;
    }

    @Before("execution(* com.y_lab.project.service..*(.., @javax.validation.Valid (*), ..)) && args(object,..)")
    public void validate(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Object> violation : violations) {
                errorMessage.append(violation.getMessage()).append("; ");
            }
            throw new ValidationException(errorMessage.toString());
        }
    }
}
