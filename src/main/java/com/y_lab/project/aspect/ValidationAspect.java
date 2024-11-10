//package com.y_lab.project.aspect;
//
//import jakarta.validation.ValidationException;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.BeanPropertyBindingResult;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Aspect
//@Component
//public class ValidationAspect {
//    private final Validator validator;
//
//    @Autowired
//    public ValidationAspect(Validator validator) {
//        this.validator = validator;
//    }
//
//    @Before("execution(* com.y_lab.project.service..*(.., @javax.validation.Valid (*), ..)) && args(object,..)")
//    public void validate(Object object) throws ValidationException {
//        Errors errors = new BeanPropertyBindingResult(object, object.getClass().getName());
//        validator.validate(object, errors);
//
//        if (errors.hasErrors()) {
//            StringBuilder errorMessage = new StringBuilder();
//            errors.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
//            throw new ValidationException(errorMessage.toString());
//        }
//    }
//}
//
