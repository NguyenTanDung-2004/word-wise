package com.example.WordWise.aspect;

import com.example.WordWise.entity.LoggerEntity;
import com.example.WordWise.repository.LogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Aspect
@Component
public class ExceptionLoggingAspect {

    private final LogRepository logRepository; // your JPA repository

    public ExceptionLoggingAspect(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @AfterThrowing(pointcut = "execution(* com.example.WordWise..*(..))", throwing = "ex")
    @Transactional(propagation = Propagation.REQUIRED)
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String message = ex.getMessage();

        LoggerEntity log = new LoggerEntity();
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setExceptionMessage(message);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);  // Log to DB
    }
}
