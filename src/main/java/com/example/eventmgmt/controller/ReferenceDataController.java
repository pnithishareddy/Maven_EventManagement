package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.*;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import com.example.eventmgmt.service.ReferenceDataService;
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

@Tag(name = "Organizers, Venues, Attendees")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReferenceDataController {
    private final ReferenceDataService service;

    @Operation(summary = "Create an organizer (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/organizers")
    public ResponseEntity<Organizer> createOrganizer(@Valid @RequestBody OrganizerRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrganizer(r));
    }

    @Operation(summary = "List organizers (paginated)")
    @GetMapping("/organizers")
    public ResponseEntity<PageResponse<Organizer>> listOrganizers(@PageableDefault(size = 10) Pageable p) {
        return ResponseEntity.ok(service.listOrganizers(p));
    }

    @Operation(summary = "Create a venue (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/venues")
    public ResponseEntity<Venue> createVenue(@Valid @RequestBody VenueRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createVenue(r));
    }

    @Operation(summary = "List venues (paginated)")
    @GetMapping("/venues")
    public ResponseEntity<PageResponse<Venue>> listVenues(@PageableDefault(size = 10) Pageable p) {
        return ResponseEntity.ok(service.listVenues(p));
    }

    @Operation(summary = "Create an attendee")
    @PostMapping("/attendees")
    public ResponseEntity<Attendee> createAttendee(@Valid @RequestBody AttendeeRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAttendee(r));
    }

    @Operation(summary = "List attendees (paginated)")
    @GetMapping("/attendees")
    public ResponseEntity<PageResponse<Attendee>> listAttendees(@PageableDefault(size = 10) Pageable p) {
        return ResponseEntity.ok(service.listAttendees(p));
    }
}
