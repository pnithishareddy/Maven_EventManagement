package com.example.eventmgmt.mapper;

import com.example.eventmgmt.dto.EventRequest;
import com.example.eventmgmt.dto.EventResponse;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Organizer;
import com.example.eventmgmt.entity.Venue;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest {
    private final EventMapper mapper = new EventMapper();
    private final LocalDateTime start = LocalDateTime.of(2030, 1, 1, 9, 0);

    @Test
    void updateEntity_thenToResponse_roundTrips() {
        Organizer o = new Organizer(); o.setId(1L);
        Venue v = new Venue(); v.setId(2L);
        EventRequest req = new EventRequest("Conf", start, start.plusHours(8), 50, 1L, 2L);

        Event e = new Event();
        mapper.updateEntity(e, req, o, v);
        e.setId(9L);
        EventResponse res = mapper.toResponse(e);

        assertEquals(9L, res.id());
        assertEquals("Conf", res.title());
        assertEquals(1L, res.organizerId());
        assertEquals(2L, res.venueId());
        assertFalse(res.hasBrochure());
    }

    @Test
    void toResponse_marksBrochureWhenPresent() {
        Organizer o = new Organizer(); o.setId(1L);
        Venue v = new Venue(); v.setId(2L);
        Event e = new Event();
        e.setOrganizer(o); e.setVenue(v); e.setBrochureFileName("event-1-brochure.pdf");
        assertTrue(mapper.toResponse(e).hasBrochure());
    }
}
