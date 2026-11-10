package com.example.eventmgmt.service;

import com.example.eventmgmt.aop.Audited;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.dto.RegistrationResponse;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.entity.Registration;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.CapacityExceededException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepo;
    private final EventRepository eventRepo;
    private final AttendeeRepository attendeeRepo;

    /**
     * Create (INSERT) a registration. The event row is locked (SELECT ... FOR UPDATE) for the
     * duration of the transaction so two requests cannot both take the last seat.
     */
    @Audited("'Registered attendee ' + #attendeeId + ' for event ' + #eventId")
    @Transactional(rollbackFor = Exception.class)
    public RegistrationResponse register(Long eventId, Long attendeeId) {
        Event event = eventRepo.findByIdForUpdate(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        Attendee attendee = attendeeRepo.findById(attendeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendee", attendeeId));

        if (event.getStatus() != EventStatus.PUBLISHED)
            throw new BusinessRuleException("Event is not open for registration (" + event.getStatus() + ")");
        if (registrationRepo.existsByEventIdAndAttendeeId(eventId, attendeeId))
            throw new DuplicateResourceException("Attendee already registered for this event");
        if (registrationRepo.countByEventId(eventId) >= event.getMaxCapacity())
            throw new CapacityExceededException("Event is full");

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setAttendee(attendee);
        Registration saved = registrationRepo.save(reg);
        log.info("Registration created eventId={} attendeeId={}", eventId, attendeeId);
        return RegistrationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<RegistrationResponse> list(Long eventId, Pageable pageable) {
        return PageResponse.from(registrationRepo.findByEventId(eventId, pageable), RegistrationResponse::from);
    }

    /** JPQL search by attendee name. */
    @Transactional(readOnly = true)
    public PageResponse<RegistrationResponse> search(Long eventId, String name, Pageable pageable) {
        return PageResponse.from(registrationRepo.searchByAttendeeName(eventId, name, pageable),
                RegistrationResponse::from);
    }
}
