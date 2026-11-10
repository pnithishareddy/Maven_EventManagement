package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.dto.RegistrationRequest;
import com.example.eventmgmt.dto.RegistrationResponse;
import com.example.eventmgmt.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Registrations")
@RestController
@RequestMapping("/api/events/{eventId}/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService service;

    @Operation(summary = "Register an attendee for an event (capacity and duplicate checks)")
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@PathVariable Long eventId,
                                                         @Valid @RequestBody RegistrationRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(eventId, r.attendeeId()));
    }

    @Operation(summary = "List registrations of an event (paginated)")
    @GetMapping
    public ResponseEntity<PageResponse<RegistrationResponse>> list(@PathVariable Long eventId,
                                                                   @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.list(eventId, pageable));
    }

    @Operation(summary = "JPQL search: registrations by attendee name")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<RegistrationResponse>> search(@PathVariable Long eventId,
                                                                     @RequestParam String name,
                                                                     @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.search(eventId, name, pageable));
    }
}
