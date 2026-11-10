package com.example.eventmgmt.scheduler;

import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** Scheduled task: marks PUBLISHED events whose end time has passed as COMPLETED. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventStatusScheduler {
    private final EventRepository eventRepo;

    @Scheduled(cron = "${app.scheduler.complete-events-cron:0 0 * * * *}")
    @Transactional
    public int completeFinishedEvents() {
        List<Event> finished = eventRepo.findByStatusAndEndTimeBefore(EventStatus.PUBLISHED, LocalDateTime.now());
        finished.forEach(e -> e.setStatus(EventStatus.COMPLETED));
        eventRepo.saveAll(finished);
        log.info("Scheduler: marked {} event(s) as COMPLETED", finished.size());
        return finished.size();
    }
}
