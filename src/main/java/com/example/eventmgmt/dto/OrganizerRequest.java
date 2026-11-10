package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrganizerRequest(@NotBlank String name, @NotBlank @Email String email) {}
