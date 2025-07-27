package com.example.WordWise.aspect;

import com.example.WordWise.entity.LoggerEntity;
import com.example.WordWise.repository.LogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class ExceptionLoggingAspect {

    private final LogRepository logRepository;

    public ExceptionLoggingAspect(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @AfterThrowing(pointcut = "execution(* com.example.WordWise..*(..))", throwing = "ex")
    @Transactional(propagation = Propagation.REQUIRED)
    public void logException(JoinPoint joinPoint, Throwable ex) {
        LoggerEntity log = new LoggerEntity();

        // Timestamp
        log.setTimestamp(LocalDateTime.now());

        // Log level
        log.setLevel("ERROR");

        // Class and method where the error occurred
        log.setClassName(joinPoint.getTarget().getClass().getName());
        log.setMethodName(joinPoint.getSignature().getName());

        // Exception details
        log.setExceptionType(ex.getClass().getSimpleName());
        log.setExceptionMessage(ex.getMessage());

        // Stack trace (optional, but useful)
        log.setStackTrace(getStackTraceAsString(ex));

        // Thread name
        log.setThreadName(Thread.currentThread().getName());

        // Request context: optional, requires helper or interceptor
        log.setRequestUrl(getCurrentRequestUrl());

        log.setParams(getMethodArgsAsString(joinPoint));

        logRepository.save(log);
    }

    private String getStackTraceAsString(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getMethodArgsAsString(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
    }

    private String getCurrentRequestUrl() {
        try {
            return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes reqAttr
                    ? reqAttr.getRequest().getRequestURI()
                    : null;
        } catch (Exception e) {
            return null;
        }
    }
}
