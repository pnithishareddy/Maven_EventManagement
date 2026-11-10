package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.dto.SessionRequest;
import com.example.eventmgmt.dto.SessionResponse;
import com.example.eventmgmt.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sessions")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService service;

    @Operation(summary = "Create a session inside an event (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/events/{eventId}/sessions")
    public ResponseEntity<SessionResponse> create(@PathVariable Long eventId,
                                                  @Valid @RequestBody SessionRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId, r));
    }

    @Operation(summary = "List sessions of an event (paginated)")
    @GetMapping("/events/{eventId}/sessions")
    public ResponseEntity<PageResponse<SessionResponse>> list(@PathVariable Long eventId,
                                                              @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listForEvent(eventId, pageable));
    }

    @Operation(summary = "Enroll a registered attendee in a session (capacity check)")
    @PostMapping("/sessions/{sessionId}/enroll")
    public ResponseEntity<SessionResponse> enroll(@PathVariable Long sessionId, @RequestParam Long attendeeId) {
        return ResponseEntity.ok(service.enroll(sessionId, attendeeId));
    }
}
