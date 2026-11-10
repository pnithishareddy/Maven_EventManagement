package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "registrations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "attendee_id"}))
public class Registration {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registration_seq")
    @SequenceGenerator(name = "registration_seq", sequenceName = "registration_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    private LocalDateTime registeredAt = LocalDateTime.now();
}
