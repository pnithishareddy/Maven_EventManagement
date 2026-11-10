package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "attendees", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Attendee {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendee_seq")
    @SequenceGenerator(name = "attendee_seq", sequenceName = "attendee_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
}
