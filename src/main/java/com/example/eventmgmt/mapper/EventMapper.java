package com.example.eventmgmt.mapper;

import com.example.eventmgmt.dto.EventRequest;
import com.example.eventmgmt.dto.EventResponse;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import org.springframework.stereotype.Component;

/** Converts between Event entities and request/response DTOs. */
@Component
public class EventMapper {

    public EventResponse toResponse(Event e) {
        return new EventResponse(e.getId(), e.getTitle(), e.getStartTime(), e.getEndTime(),
                e.getMaxCapacity(), e.getStatus(), e.getOrganizer().getId(), e.getVenue().getId(),
                e.getBrochureFileName() != null);
    }

    public void updateEntity(Event e, EventRequest r, Organizer organizer, Venue venue) {
        e.setTitle(r.title());
        e.setStartTime(r.startTime());
        e.setEndTime(r.endTime());
        e.setMaxCapacity(r.maxCapacity());
        e.setOrganizer(organizer);
        e.setVenue(venue);
    }
}
