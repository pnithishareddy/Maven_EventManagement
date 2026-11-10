package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.AttendeeRequest;
import com.example.eventmgmt.dto.OrganizerRequest;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.dto.VenueRequest;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.OrganizerRepository;
import com.example.eventmgmt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Function;

/** Organizers, Venues and Attendees: create (INSERT) with unique email / name, and paginated lists. */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceDataService {
    private final OrganizerRepository organizerRepo;
    private final VenueRepository venueRepo;
    private final AttendeeRepository attendeeRepo;

    @Transactional(rollbackFor = Exception.class)
    public Organizer createOrganizer(OrganizerRequest r) {
        if (organizerRepo.existsByEmail(r.email()))
            throw new DuplicateResourceException("Organizer email already exists: " + r.email());
        Organizer o = new Organizer();
        o.setName(r.name());
        o.setEmail(r.email());
        log.info("Creating organizer {}", r.email());
        return organizerRepo.save(o);
    }

    @Transactional(rollbackFor = Exception.class)
    public Venue createVenue(VenueRequest r) {
        if (venueRepo.existsByName(r.name()))
            throw new DuplicateResourceException("Venue name already exists: " + r.name());
        Venue v = new Venue();
        v.setName(r.name());
        v.setAddress(r.address());
        v.setCapacity(r.capacity());
        log.info("Creating venue {}", r.name());
        return venueRepo.save(v);
    }

    @Transactional(rollbackFor = Exception.class)
    public Attendee createAttendee(AttendeeRequest r) {
        if (attendeeRepo.existsByEmail(r.email()))
            throw new DuplicateResourceException("Attendee email already exists: " + r.email());
        Attendee a = new Attendee();
        a.setName(r.name());
        a.setEmail(r.email());
        log.info("Creating attendee {}", r.email());
        return attendeeRepo.save(a);
    }

    @Transactional(readOnly = true)
    public PageResponse<Organizer> listOrganizers(Pageable pageable) {
        return PageResponse.from(organizerRepo.findAll(pageable), Function.identity());
    }

    @Transactional(readOnly = true)
    public PageResponse<Venue> listVenues(Pageable pageable) {
        return PageResponse.from(venueRepo.findAll(pageable), Function.identity());
    }

    @Transactional(readOnly = true)
    public PageResponse<Attendee> listAttendees(Pageable pageable) {
        return PageResponse.from(attendeeRepo.findAll(pageable), Function.identity());
    }
}
