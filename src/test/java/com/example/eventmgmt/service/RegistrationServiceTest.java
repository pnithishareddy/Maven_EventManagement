package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.RegistrationResponse;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.entity.Registration;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.CapacityExceededException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock RegistrationRepository registrationRepo;
    @Mock EventRepository eventRepo;
    @Mock AttendeeRepository attendeeRepo;
    @InjectMocks RegistrationService service;

    Event event;
    Attendee attendee;

    @BeforeEach
    void setUp() {
        event = new Event(); event.setId(1L); event.setMaxCapacity(2); event.setStatus(EventStatus.PUBLISHED);
        attendee = new Attendee(); attendee.setId(10L);
        when(eventRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        when(attendeeRepo.findById(10L)).thenReturn(Optional.of(attendee));
    }

    @Test
    void register_succeeds_whenSeatsAvailable() {
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(false);
        when(registrationRepo.countByEventId(1L)).thenReturn(1L);
        when(registrationRepo.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        RegistrationResponse res = service.register(1L, 10L);

        assertEquals(10L, res.attendeeId());
        verify(registrationRepo).save(any(Registration.class));
    }

    @Test
    void register_throws_whenEventFull() {
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(false);
        when(registrationRepo.countByEventId(1L)).thenReturn(2L);

        assertThrows(CapacityExceededException.class, () -> service.register(1L, 10L));
        verify(registrationRepo, never()).save(any());
    }

    @Test
    void register_throws_whenAlreadyRegistered() {
        when(registrationRepo.existsByEventIdAndAttendeeId(1L, 10L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.register(1L, 10L));
    }

    @Test
    void register_throws_whenEventCancelled() {
        event.setStatus(EventStatus.CANCELLED);
        assertThrows(BusinessRuleException.class, () -> service.register(1L, 10L));
    }
}
