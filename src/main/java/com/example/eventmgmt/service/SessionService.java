package com.example.eventmgmt.service;

import com.example.eventmgmt.aop.Audited;
import com.example.eventmgmt.dto.PageResponse;
import com.example.eventmgmt.dto.SessionRequest;
import com.example.eventmgmt.dto.SessionResponse;
import com.example.eventmgmt.entity.Attendee;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Session;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.CapacityExceededException;
import com.example.eventmgmt.exception.DuplicateResourceException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.repository.AttendeeRepository;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import com.example.eventmgmt.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepo;
    private final EventRepository eventRepo;
    private final AttendeeRepository attendeeRepo;
    private final RegistrationRepository registrationRepo;

    @Transactional(rollbackFor = Exception.class)
    public SessionResponse create(Long eventId, SessionRequest r) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        if (!r.endTime().isAfter(r.startTime()))
            throw new BusinessRuleException("Session end time must be after start time");
        if (r.startTime().isBefore(event.getStartTime()) || r.endTime().isAfter(event.getEndTime()))
            throw new BusinessRuleException("Session must fall within the event's time window");
        if (r.capacity() > event.getMaxCapacity())
            throw new BusinessRuleException("Session capacity cannot exceed event capacity");

        Session s = new Session();
        s.setTitle(r.title());
        s.setStartTime(r.startTime());
        s.setEndTime(r.endTime());
        s.setCapacity(r.capacity());
        s.setEvent(event);
        Session saved = sessionRepo.save(s);
        log.info("Session created id={} eventId={}", saved.getId(), eventId);
        return SessionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<SessionResponse> listForEvent(Long eventId, Pageable pageable) {
        return PageResponse.from(sessionRepo.findByEventId(eventId, pageable), SessionResponse::from);
    }

    /** Attendee must be registered for the parent event and the session must have room. */
    @Audited("'Enrolled attendee ' + #attendeeId + ' in session ' + #sessionId")
    @Transactional(rollbackFor = Exception.class)
    public SessionResponse enroll(Long sessionId, Long attendeeId) {
        Session session = sessionRepo.findByIdForUpdate(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));
        Attendee attendee = attendeeRepo.findById(attendeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendee", attendeeId));

        Long eventId = session.getEvent().getId();
        if (!registrationRepo.existsByEventIdAndAttendeeId(eventId, attendeeId))
            throw new BusinessRuleException("Attendee must be registered for the event before enrolling in a session");
        if (session.getAttendees().stream().anyMatch(a -> a.getId().equals(attendeeId)))
            throw new DuplicateResourceException("Attendee already enrolled in this session");
        if (session.getAttendees().size() >= session.getCapacity())
            throw new CapacityExceededException("Session is full");

        session.getAttendees().add(attendee);
        log.info("Attendee {} enrolled in session {}", attendeeId, sessionId);
        return SessionResponse.from(sessionRepo.save(session));
    }
}
