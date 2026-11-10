package com.example.eventmgmt.dto;

public record FileUploadResponse(Long eventId, String fileName, String contentType, long size) {}
