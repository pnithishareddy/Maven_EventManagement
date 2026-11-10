package com.example.eventmgmt.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(@NotNull Long attendeeId) {}
