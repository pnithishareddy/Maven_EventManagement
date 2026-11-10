package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.*;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.service.EventService;
import com.example.eventmgmt.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Events")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final FileStorageService fileService;

    @Operation(summary = "Create an event (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest r) {
        EventResponse created = service.create(r);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get one event (cached)")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "List events: pagination and sorting, e.g. ?page=0&size=5&sort=startTime,desc&venueId=1")
    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> list(
            @RequestParam(required = false) Long venueId,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(service.list(venueId, pageable));
    }

    @Operation(summary = "JPQL search by status and date range")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<EventResponse>> search(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(service.search(status, from, to, pageable));
    }

    @Operation(summary = "Named query: events of an organizer")
    @GetMapping("/by-organizer")
    public ResponseEntity<List<EventResponse>> byOrganizer(@RequestParam String name) {
        return ResponseEntity.ok(service.byOrganizerName(name));
    }

    @Operation(summary = "Native SQL query: events with at least this capacity")
    @GetMapping("/large")
    public ResponseEntity<List<EventResponse>> large(@RequestParam(defaultValue = "50") int minCapacity) {
        return ResponseEntity.ok(service.largeEvents(minCapacity));
    }

    @Operation(summary = "Update an event (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventRequest r) {
        return ResponseEntity.ok(service.update(id, r));
    }

    @Operation(summary = "Delete an event (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Capacity check: max, registered, remaining")
    @GetMapping("/{id}/capacity")
    public ResponseEntity<CapacityInfo> capacity(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkCapacity(id));
    }

    @Operation(summary = "Upload an event brochure (PDF/PNG/JPEG, max 5MB) - ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}/brochure", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadBrochure(@PathVariable Long id,
                                                             @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.store(id, file));
    }

    @Operation(summary = "Download the event brochure")
    @GetMapping("/{id}/brochure")
    public ResponseEntity<Resource> downloadBrochure(@PathVariable Long id) {
        FileStorageService.StoredFile f = fileService.load(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(f.fileName()).build().toString())
                .body(f.resource());
    }
}
