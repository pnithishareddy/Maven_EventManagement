package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "organizers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Organizer {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizer_seq")
    @SequenceGenerator(name = "organizer_seq", sequenceName = "organizer_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
}
