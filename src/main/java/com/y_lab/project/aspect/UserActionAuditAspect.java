package com.y_lab.project.aspect;

import com.y_lab.project.dto.UserDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class UserActionAuditAspect {

    private final UserDTO userDTO = new UserDTO();

    @Before("execution(* com.y_lab.project.controller.UserController.*(..))")
    public void logUserControllerAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Пользователь {} выполняет действие {} с параметрами: {}", userDTO.getId(), methodName, Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.y_lab.project.controller.UserController.*(..))", returning = "result")
    public void logUserControllerSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Действие {} пользователя {} завершилось успешно. Результат: {}", methodName, userDTO.getId(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.y_lab.project.controller.UserController.*(..))", throwing = "exception")
    public void logUserControllerError(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Ошибка при выполнении действия {} пользователем {}: {}", methodName, userDTO.getId(), exception.getMessage());
    }

    @Before("execution(* com.y_lab.project.service.HabitService.addHabit(..)) || "
            + "execution(* com.y_lab.project.service.HabitService.updateHabit(..)) || "
            + "execution(* com.y_lab.project.service.HabitService.deleteHabit(..))")
    public void logHabitServiceAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Пользователь {} выполняет действие {} с параметрами: {}", userDTO.getId(), methodName, Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.y_lab.project.service.HabitService.*(..))", returning = "result")
    public void logHabitServiceSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Действие {} пользователя {} завершилось успешно. Результат: {}", methodName, userDTO.getId(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.y_lab.project.service.HabitService.*(..))", throwing = "exception")
    public void logHabitServiceError(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Ошибка при выполнении действия {} пользователем {}: {}", methodName, userDTO.getId(), exception.getMessage());
    }
}

