package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Venue -> Events; Organizer -> Events; Event -> Sessions; Event -> Registrations. */
@Entity
@Getter @Setter
@NamedQuery(name = "Event.findEventsByOrganizerName",
            query = "select e from Event e where e.organizer.name = :name order by e.startTime")
@Table(name = "events",
       uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id", "start_time"}))
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private int maxCapacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PUBLISHED;

    /** Uploaded brochure (file upload / download feature). */
    private String brochureFileName;
    private String brochureContentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<Registration> registrations = new ArrayList<>();
}
