package com.example.eventmgmt.scheduler;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

/** Scheduled task: periodically logs HikariCP connection pool statistics. */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolMonitor {
    private final DataSource dataSource;

    @Scheduled(fixedRateString = "${app.scheduler.pool-monitor-ms:60000}")
    public void logPoolStats() {
        if (dataSource instanceof HikariDataSource hikari) {
            HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
            if (pool != null) {
                log.info("Connection pool [{}] active={} idle={} total={} waiting={}",
                        hikari.getPoolName(), pool.getActiveConnections(), pool.getIdleConnections(),
                        pool.getTotalConnections(), pool.getThreadsAwaitingConnection());
            }
        }
    }
}
