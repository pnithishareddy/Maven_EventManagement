package com.example.eventmgmt.dto;

import com.example.eventmgmt.entity.Session;
import java.time.LocalDateTime;

public record SessionResponse(Long id, String title, LocalDateTime startTime, LocalDateTime endTime,
                              int capacity, int enrolled, Long eventId) {
    public static SessionResponse from(Session s) {
        return new SessionResponse(s.getId(), s.getTitle(), s.getStartTime(), s.getEndTime(),
                s.getCapacity(), s.getAttendees().size(), s.getEvent().getId());
    }
}
