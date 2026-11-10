package com.example.eventmgmt.aop;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditAspectTest {

    static class Dummy {
        public String register(Long eventId, Long attendeeId) { return "ok"; }
    }

    private final AuditAspect aspect = new AuditAspect();

    @Test
    void evaluate_resolvesParametersAndResultWithSpel() throws Exception {
        Method m = Dummy.class.getMethod("register", Long.class, Long.class);
        String text = aspect.evaluate(
                "'Registered ' + #attendeeId + ' for event ' + #eventId + ' -> ' + #result",
                m, new Object[]{1L, 10L}, "ok");
        assertEquals("Registered 10 for event 1 -> ok", text);
    }

    @Test
    void evaluate_canCallMethodsOnTheResult() throws Exception {
        Method m = Dummy.class.getMethod("register", Long.class, Long.class);
        assertEquals("OK", aspect.evaluate("#result.toUpperCase()", m, new Object[]{1L, 2L}, "ok"));
    }

    @Test
    void evaluate_returnsMessage_whenExpressionIsInvalid() throws Exception {
        Method m = Dummy.class.getMethod("register", Long.class, Long.class);
        String text = aspect.evaluate("#result.noSuchMethod()", m, new Object[]{1L, 2L}, "ok");
        assertTrue(text.startsWith("audit expression failed"));
    }
}
