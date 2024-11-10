package com.y_lab.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class UserActionAuditAspect {

    private final UserDTO userDTO;

    public UserActionAuditAspect(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Before("execution(* com.y_lab.project.controller.UserController.*(..))")
    public void logUserControllerAction(JoinPoint joinPoint) {
        log.info("Пользователь {} выполняет действие {} с параметрами: {}", userDTO.getId(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.y_lab.project.controller.UserController.*(..))", returning = "result")
    public void logUserControllerSuccess(JoinPoint joinPoint, Object result) {
        log.info("Действие {} пользователя {} завершилось успешно. Результат: {}", joinPoint.getSignature().getName(), userDTO.getId(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.y_lab.project.controller.UserController.*(..))", throwing = "exception")
    public void logUserControllerError(JoinPoint joinPoint, Throwable exception) {
        log.error("Ошибка при выполнении действия {} пользователем {}: {}", joinPoint.getSignature().getName(), userDTO.getId(), exception.getMessage());
    }

    @Before("execution(* com.y_lab.project.service.HabitService.addHabit(..)) || "
            + "execution(* com.y_lab.project.service.HabitService.updateHabit(..)) || "
            + "execution(* com.y_lab.project.service.HabitService.deleteHabit(..))")
    public void logHabitServiceAction(JoinPoint joinPoint) {
        log.info("Пользователь {} выполняет действие {} с параметрами: {}", userDTO.getId(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.y_lab.project.service.HabitService.*(..))", returning = "result")
    public void logHabitServiceSuccess(JoinPoint joinPoint, Object result) {
        log.info("Действие {} пользователя {} завершилось успешно. Результат: {}", joinPoint.getSignature().getName(), userDTO.getId(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.y_lab.project.service.HabitService.*(..))", throwing = "exception")
    public void logHabitServiceError(JoinPoint joinPoint, Throwable exception) {
        log.error("Ошибка при выполнении действия {} пользователем {}: {}", joinPoint.getSignature().getName(), userDTO.getId(), exception.getMessage());
    }
}
