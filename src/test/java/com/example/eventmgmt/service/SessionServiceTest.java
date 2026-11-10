package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.SessionRequest;
import com.example.eventmgmt.dto.SessionResponse;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Session;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.CapacityExceededException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import com.example.eventmgmt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    @Mock SessionRepository sessionRepo;
    @Mock EventRepository eventRepo;
    @Mock AttendeeRepository attendeeRepo;
    @Mock RegistrationRepository registrationRepo;
    @InjectMocks SessionService service;

    Event event;
    Session session;
    Attendee attendee;
    LocalDateTime start = LocalDateTime.of(2030, 1, 1, 9, 0);

    @BeforeEach
    void setUp() {
        event = new Event(); event.setId(1L); event.setMaxCapacity(50);
        event.setStartTime(start); event.setEndTime(start.plusHours(8));
        session = new Session(); session.setId(7L); session.setCapacity(1); session.setEvent(event);
        attendee = new Attendee(); attendee.setId(10L);
    }

    private void stubEnrollLookups() {
        when(sessionRepo.findByIdForUpdate(7L)).thenReturn(Optional.of(session));
        when(attendeeRepo.findById(10L)).thenReturn(Optional.of(attendee));
    }

    @Test
    void enroll_succeeds_whenRegisteredAndSeatAvailable() {
        stubEnrollLookups();
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(true);
        when(sessionRepo.save(any(Session.class))).thenAnswer(i -> i.getArgument(0));

        SessionResponse res = service.enroll(7L, 10L);

        assertEquals(1, res.enrolled());
    }

    @Test
    void enroll_throws_whenNotRegisteredForEvent() {
        stubEnrollLookups();
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(false);
        assertThrows(BusinessRuleException.class, () -> service.enroll(7L, 10L));
    }

    @Test
    void enroll_throws_whenAlreadyEnrolled() {
        session.getAttendees().add(attendee);
        stubEnrollLookups();
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.enroll(7L, 10L));
    }

    @Test
    void enroll_throws_whenSessionFull() {
        Attendee other = new Attendee(); other.setId(11L);
        session.getAttendees().add(other);
        stubEnrollLookups();
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(true);
        assertThrows(CapacityExceededException.class, () -> service.enroll(7L, 10L));
    }

    @Test
    void create_throws_whenSessionOutsideEventWindow() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        SessionRequest r = new SessionRequest("Keynote", start.minusHours(2), start.plusHours(1), 10);
        assertThrows(BusinessRuleException.class, () -> service.create(1L, r));
    }
}
