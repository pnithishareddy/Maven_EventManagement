package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record VenueRequest(@NotBlank String name, String address, @Min(1) int capacity) {}
