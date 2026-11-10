package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.CapacityInfo;
import com.example.eventmgmt.dto.EventRequest;
import com.example.eventmgmt.dto.EventResponse;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.OrganizerRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import com.example.eventmgmt.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import com.example.eventmgmt.mapper.EventMapper;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock EventRepository eventRepo;
    @Mock OrganizerRepository organizerRepo;
    @Mock VenueRepository venueRepo;
    @Mock RegistrationRepository registrationRepo;
    @Spy EventMapper mapper = new EventMapper();
    @InjectMocks EventService service;

    Organizer organizer;
    Venue venue;
    LocalDateTime start = LocalDateTime.of(2030, 1, 1, 9, 0);

    @BeforeEach
    void setUp() {
        organizer = new Organizer(); organizer.setId(1L);
        venue = new Venue(); venue.setId(2L); venue.setCapacity(100);
    }

    private EventRequest request(int capacity) {
        return new EventRequest("Conf", start, start.plusHours(8), capacity, 1L, 2L);
    }

    private void stubLookups() {
        when(organizerRepo.findById(1L)).thenReturn(Optional.of(organizer));
        when(venueRepo.findById(2L)).thenReturn(Optional.of(venue));
    }

    @Test
    void create_savesEvent_whenValid() {
        stubLookups();
        when(eventRepo.existsByVenueIdAndStartTime(2L, start)).thenReturn(false);
        when(eventRepo.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        EventResponse res = service.create(request(50));

        assertEquals("Conf", res.title());
        assertEquals(50, res.maxCapacity());
        verify(eventRepo).save(any(Event.class));
    }

    @Test
    void create_throws_whenCapacityExceedsVenue() {
        stubLookups();
        assertThrows(BusinessRuleException.class, () -> service.create(request(101)));
        verify(eventRepo, never()).save(any());
    }

    @Test
    void create_throws_whenVenueSlotAlreadyTaken() {
        stubLookups();
        when(eventRepo.existsByVenueIdAndStartTime(2L, start)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.create(request(50)));
    }

    @Test
    void create_throws_whenEndBeforeStart() {
        stubLookups();
        EventRequest bad = new EventRequest("Conf", start, start.minusHours(1), 10, 1L, 2L);
        assertThrows(BusinessRuleException.class, () -> service.create(bad));
    }

    @Test
    void get_throws_whenNotFound() {
        when(eventRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(99L));
    }

    @Test
    void checkCapacity_returnsRemainingSeats() {
        Event e = new Event(); e.setId(5L); e.setMaxCapacity(10);
        when(eventRepo.findById(5L)).thenReturn(Optional.of(e));
        when(registrationRepo.countByEventId(5L)).thenReturn(10L);

        CapacityInfo info = service.checkCapacity(5L);

        assertEquals(0, info.remaining());
        assertTrue(info.full());
    }

    @Test
    void update_throws_whenCapacityBelowExistingRegistrations() {
        Event e = new Event(); e.setId(5L); e.setVenue(venue); e.setStartTime(start); e.setMaxCapacity(50);
        when(eventRepo.findById(5L)).thenReturn(Optional.of(e));
        stubLookups();
        when(registrationRepo.countByEventId(5L)).thenReturn(30L);

        assertThrows(BusinessRuleException.class, () -> service.update(5L, request(20)));
    }
}
