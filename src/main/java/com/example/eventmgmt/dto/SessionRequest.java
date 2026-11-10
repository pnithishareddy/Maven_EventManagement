package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SessionRequest(@NotBlank String title, @NotNull LocalDateTime startTime,
                             @NotNull LocalDateTime endTime, @Min(1) int capacity) {}
