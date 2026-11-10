package com.example.eventmgmt.config;

import com.example.eventmgmt.entity.*;
import com.example.eventmgmt.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/** Inserts sample data in the dev profile when app.seed.enabled=true. */
@Slf4j
@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final OrganizerRepository organizerRepo;
    private final VenueRepository venueRepo;
    private final AttendeeRepository attendeeRepo;
    private final EventRepository eventRepo;

    @Override
    public void run(String... args) {
        if (organizerRepo.count() > 0) return;
        Organizer o = new Organizer(); o.setName("Asha"); o.setEmail("asha@example.com");
        organizerRepo.save(o);
        Venue v = new Venue(); v.setName("Hall A"); v.setAddress("Hyderabad"); v.setCapacity(100);
        venueRepo.save(v);
        for (String n : new String[]{"Ben", "Cara", "Dev"}) {
            Attendee a = new Attendee(); a.setName(n); a.setEmail(n.toLowerCase() + "@example.com");
            attendeeRepo.save(a);
        }
        Event e = new Event(); e.setTitle("Tech Conf");
        e.setStartTime(LocalDateTime.of(2030, 1, 1, 9, 0)); e.setEndTime(LocalDateTime.of(2030, 1, 1, 17, 0));
        e.setMaxCapacity(2); e.setOrganizer(o); e.setVenue(v);
        eventRepo.save(e);
        log.info("Sample data inserted");
    }
}
