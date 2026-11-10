package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    boolean existsByEmail(String email);
}
