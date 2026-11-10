package com.example.eventmgmt.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method for auditing. The value is a SpEL expression evaluated after the method
 * returns. Method parameters are available as #paramName and the return value as #result.
 * Example: @Audited("'Registered attendee ' + #attendeeId + ' for event ' + #eventId")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audited {
    String value();
}
