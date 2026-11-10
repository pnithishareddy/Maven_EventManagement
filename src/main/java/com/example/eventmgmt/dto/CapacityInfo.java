package com.example.eventmgmt.dto;

public record CapacityInfo(Long eventId, int maxCapacity, long registered, long remaining, boolean full) {}
