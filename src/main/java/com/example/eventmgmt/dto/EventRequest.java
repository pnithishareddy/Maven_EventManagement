package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank @Size(max = 150) String title,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @Min(1) int maxCapacity,
        @NotNull Long organizerId,
        @NotNull Long venueId) {}
