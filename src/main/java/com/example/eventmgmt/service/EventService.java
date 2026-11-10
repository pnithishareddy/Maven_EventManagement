package com.example.eventmgmt.service;

import com.example.eventmgmt.aop.Audited;
import com.example.eventmgmt.dto.CapacityInfo;
import com.example.eventmgmt.dto.EventRequest;
import com.example.eventmgmt.dto.EventResponse;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.mapper.EventMapper;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.OrganizerRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import com.example.eventmgmt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepo;
    private final OrganizerRepository organizerRepo;
    private final VenueRepository venueRepo;
    private final RegistrationRepository registrationRepo;
    private final EventMapper mapper;

    /** Create (INSERT). */
    @Audited("'Created event ' + #result.id() + ' titled ' + #result.title()")
    @Transactional(rollbackFor = Exception.class)
    public EventResponse create(EventRequest r) {
        Organizer organizer = organizerRepo.findById(r.organizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", r.organizerId()));
        Venue venue = venueRepo.findById(r.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", r.venueId()));
        validate(r, venue);
        if (eventRepo.existsByVenueIdAndStartTime(venue.getId(), r.startTime()))
            throw new DuplicateResourceException("Venue already has an event starting at " + r.startTime());

        Event e = new Event();
        mapper.updateEntity(e, r, organizer, venue);
        Event saved = eventRepo.save(e);
        log.info("Event created id={} title={}", saved.getId(), saved.getTitle());
        return mapper.toResponse(saved);
    }

    /** Cached read: the second call for the same id is served from the "events" cache. */
    @Cacheable(value = "events", key = "#id", condition = "#id > 0")
    @Transactional(readOnly = true)
    public EventResponse get(Long id) {
        log.debug("Loading event {} from the database", id);
        return mapper.toResponse(find(id));
    }

    /** Pagination + sorting come from the Pageable (page, size, sort). */
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> list(Long venueId, Pageable pageable) {
        Page<Event> page = (venueId == null) ? eventRepo.findAll(pageable)
                                             : eventRepo.findByVenueId(venueId, pageable);
        return PageResponse.from(page, mapper::toResponse);
    }

    /** JPQL query. */
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> search(EventStatus status, LocalDateTime from, LocalDateTime to,
                                              Pageable pageable) {
        EventStatus s = status == null ? EventStatus.PUBLISHED : status;
        LocalDateTime f = from == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : from;
        LocalDateTime t = to == null ? LocalDateTime.of(2100, 1, 1, 0, 0) : to;
        return PageResponse.from(eventRepo.findByStatusAndDateRange(s, f, t, pageable), mapper::toResponse);
    }

    /** Named query. */
    @Transactional(readOnly = true)
    public List<EventResponse> byOrganizerName(String name) {
        return eventRepo.findEventsByOrganizerName(name).stream().map(mapper::toResponse).toList();
    }

    /** Native SQL query. */
    @Transactional(readOnly = true)
    public List<EventResponse> largeEvents(int minCapacity) {
        return eventRepo.findLargeEventsNative(minCapacity).stream().map(mapper::toResponse).toList();
    }

    @CachePut(value = "events", key = "#id")
    @Transactional(rollbackFor = Exception.class)
    public EventResponse update(Long id, EventRequest r) {
        Event e = find(id);
        Organizer organizer = organizerRepo.findById(r.organizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", r.organizerId()));
        Venue venue = venueRepo.findById(r.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", r.venueId()));
        validate(r, venue);

        boolean slotChanged = !e.getVenue().getId().equals(venue.getId())
                || !e.getStartTime().equals(r.startTime());
        if (slotChanged && eventRepo.existsByVenueIdAndStartTime(venue.getId(), r.startTime()))
            throw new DuplicateResourceException("Venue already has an event starting at " + r.startTime());

        long registered = registrationRepo.countByEventId(id);
        if (r.maxCapacity() < registered)
            throw new BusinessRuleException("Capacity cannot be lower than current registrations (" + registered + ")");

        mapper.updateEntity(e, r, organizer, venue);
        log.info("Event updated id={}", id);
        return mapper.toResponse(eventRepo.save(e));
    }

    @CacheEvict(value = "events", key = "#id")
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        eventRepo.delete(find(id));
        log.info("Event deleted id={}", id);
    }

    /** Capacity checking API. */
    @Transactional(readOnly = true)
    public CapacityInfo checkCapacity(Long id) {
        Event e = find(id);
        long registered = registrationRepo.countByEventId(id);
        long remaining = Math.max(0, e.getMaxCapacity() - registered);
        return new CapacityInfo(id, e.getMaxCapacity(), registered, remaining, remaining == 0);
    }

    private Event find(Long id) {
        return eventRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    private void validate(EventRequest r, Venue venue) {
        if (!r.endTime().isAfter(r.startTime()))
            throw new BusinessRuleException("Event end time must be after start time");
        if (r.maxCapacity() > venue.getCapacity())
            throw new BusinessRuleException("Event capacity (" + r.maxCapacity()
                    + ") exceeds venue capacity (" + venue.getCapacity() + ")");
    }
}
