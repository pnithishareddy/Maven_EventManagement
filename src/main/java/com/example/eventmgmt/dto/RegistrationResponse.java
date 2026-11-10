package com.example.eventmgmt.dto;

import com.example.eventmgmt.entity.Registration;
import java.time.LocalDateTime;

public record RegistrationResponse(Long id, Long eventId, Long attendeeId, LocalDateTime registeredAt) {
    public static RegistrationResponse from(Registration r) {
        return new RegistrationResponse(r.getId(), r.getEvent().getId(),
                r.getAttendee().getId(), r.getRegisteredAt());
    }
}
