package com.example.eventmgmt.dto;

import com.example.eventmgmt.entity.EventStatus;
import java.time.LocalDateTime;

public record EventResponse(Long id, String title, LocalDateTime startTime, LocalDateTime endTime,
                            int maxCapacity, EventStatus status, Long organizerId, Long venueId,
                            boolean hasBrochure) {}
