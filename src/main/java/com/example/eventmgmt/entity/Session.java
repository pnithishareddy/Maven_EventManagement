package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name = "sessions")
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_seq")
    @SequenceGenerator(name = "session_seq", sequenceName = "session_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    /** Session enrollment: unique (session, attendee) pair enforced by the join table. */
    @ManyToMany
    @JoinTable(name = "session_enrollment",
               joinColumns = @JoinColumn(name = "session_id"),
               inverseJoinColumns = @JoinColumn(name = "attendee_id"),
               uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "attendee_id"}))
    private Set<Attendee> attendees = new HashSet<>();
}
