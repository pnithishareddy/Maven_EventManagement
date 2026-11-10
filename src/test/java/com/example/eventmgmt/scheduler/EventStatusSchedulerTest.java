package com.example.eventmgmt.scheduler;

import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStatusSchedulerTest {
    @Mock EventRepository eventRepo;
    @InjectMocks EventStatusScheduler scheduler;

    @Test
    void completeFinishedEvents_marksPastEventsCompleted() {
        Event past = new Event(); past.setStatus(EventStatus.PUBLISHED);
        when(eventRepo.findByStatusAndEndTimeBefore(eq(EventStatus.PUBLISHED), any(LocalDateTime.class)))
                .thenReturn(List.of(past));

        int count = scheduler.completeFinishedEvents();

        assertEquals(1, count);
        assertEquals(EventStatus.COMPLETED, past.getStatus());
        verify(eventRepo).saveAll(List.of(past));
    }
}
