package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByEventIdAndAttendeeId(Long eventId, Long attendeeId);
    long countByEventId(Long eventId);
    Page<Registration> findByEventId(Long eventId, Pageable pageable);

    @Query("select r from Registration r where r.event.id = :eventId "
         + "and lower(r.attendee.name) like lower(concat('%', :name, '%'))")
    Page<Registration> searchByAttendeeName(@Param("eventId") Long eventId,
                                            @Param("name") String name,
                                            Pageable pageable);
}
