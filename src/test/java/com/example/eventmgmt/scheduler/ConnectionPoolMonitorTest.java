package com.example.eventmgmt.scheduler;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ConnectionPoolMonitorTest {

    @Test
    void logPoolStats_readsHikariPoolStatistics() {
        HikariDataSource ds = mock(HikariDataSource.class);
        HikariPoolMXBean pool = mock(HikariPoolMXBean.class);
        when(ds.getHikariPoolMXBean()).thenReturn(pool);
        when(ds.getPoolName()).thenReturn("EventHikariPool");
        when(pool.getActiveConnections()).thenReturn(1);
        when(pool.getIdleConnections()).thenReturn(2);
        when(pool.getTotalConnections()).thenReturn(3);
        when(pool.getThreadsAwaitingConnection()).thenReturn(0);

        new ConnectionPoolMonitor(ds).logPoolStats();

        verify(pool).getActiveConnections();
        verify(pool).getTotalConnections();
    }

    @Test
    void logPoolStats_ignoresNonHikariDataSource() {
        DataSource other = mock(DataSource.class);
        assertDoesNotThrow(() -> new ConnectionPoolMonitor(other).logPoolStats());
    }
}
