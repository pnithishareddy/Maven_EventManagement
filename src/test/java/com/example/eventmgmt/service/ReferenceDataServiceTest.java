package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.AttendeeRequest;
import com.example.eventmgmt.dto.OrganizerRequest;
import com.example.eventmgmt.dto.VenueRequest;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.OrganizerRepository;
import com.example.eventmgmt.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceTest {
    @Mock OrganizerRepository organizerRepo;
    @Mock VenueRepository venueRepo;
    @Mock AttendeeRepository attendeeRepo;
    @InjectMocks ReferenceDataService service;

    @Test
    void createOrganizer_saves_whenEmailIsNew() {
        when(organizerRepo.existsByEmail("a@x.com")).thenReturn(false);
        when(organizerRepo.save(any(Organizer.class))).thenAnswer(i -> i.getArgument(0));
        Organizer o = service.createOrganizer(new OrganizerRequest("Asha", "a@x.com"));
        assertEquals("a@x.com", o.getEmail());
    }

    @Test
    void createAttendee_throws_whenEmailExists() {
        when(attendeeRepo.existsByEmail("b@x.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> service.createAttendee(new AttendeeRequest("Ben", "b@x.com")));
        verify(attendeeRepo, never()).save(any(Attendee.class));
    }

    @Test
    void createVenue_throws_whenNameExists() {
        when(venueRepo.existsByName("Hall A")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> service.createVenue(new VenueRequest("Hall A", "Addr", 100)));
    }
}
