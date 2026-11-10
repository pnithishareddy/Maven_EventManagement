package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    boolean existsByName(String name);
}
