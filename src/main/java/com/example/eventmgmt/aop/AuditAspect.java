package com.example.eventmgmt.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/** AOP + SpEL: writes an audit log line built from a SpEL expression on @Audited methods. */
@Slf4j
@Aspect
@Component
public class AuditAspect {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result",
                    argNames = "joinPoint,audited,result")
    public void audit(JoinPoint joinPoint, Audited audited, Object result) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        log.info("AUDIT | {}", evaluate(audited.value(), method, joinPoint.getArgs(), result));
    }

    /** Evaluates the SpEL expression with the method's arguments and return value. */
    public String evaluate(String expression, Method method, Object[] args, Object result) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] names = nameDiscoverer.getParameterNames(method);
        if (names != null) {
            for (int i = 0; i < names.length && i < args.length; i++) {
                context.setVariable(names[i], args[i]);
            }
        }
        context.setVariable("result", result);
        try {
            return parser.parseExpression(expression).getValue(context, String.class);
        } catch (Exception ex) {
            return "audit expression failed: " + ex.getMessage();
        }
    }
}
