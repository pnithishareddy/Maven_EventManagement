package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // ---- derived queries
    Page<Event> findByVenueId(Long venueId, Pageable pageable);
    boolean existsByVenueIdAndStartTime(Long venueId, LocalDateTime startTime);
    List<Event> findByStatusAndEndTimeBefore(EventStatus status, LocalDateTime time);

    // ---- JPQL query (with pagination + sorting through Pageable)
    @Query("select e from Event e where e.status = :status and e.startTime between :from and :to")
    Page<Event> findByStatusAndDateRange(@Param("status") EventStatus status,
                                         @Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to,
                                         Pageable pageable);

    // ---- native SQL query
    @Query(value = "SELECT * FROM events WHERE max_capacity >= :minCapacity ORDER BY start_time",
           nativeQuery = true)
    List<Event> findLargeEventsNative(@Param("minCapacity") int minCapacity);

    // ---- named query (defined with @NamedQuery on the Event entity)
    List<Event> findEventsByOrganizerName(@Param("name") String name);

    // ---- pessimistic lock: prevents two requests taking the last seat at the same time
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
