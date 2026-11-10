package com.example.eventmgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "venues", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "venue_seq")
    @SequenceGenerator(name = "venue_seq", sequenceName = "venue_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String address;
    @Column(nullable = false)
    private int capacity;
}
