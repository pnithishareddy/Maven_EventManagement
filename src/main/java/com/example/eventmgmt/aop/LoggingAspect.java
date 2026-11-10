package com.example.eventmgmt.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/** AOP: logs the execution time of every service method and any exception it throws. */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("within(com.example.eventmgmt.service..*)")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();
        try {
            Object result = pjp.proceed();
            log.debug("{} finished in {} ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.warn("{} failed after {} ms: {} - {}", method, System.currentTimeMillis() - start,
                    t.getClass().getSimpleName(), t.getMessage());
            throw t;
        }
    }
}
