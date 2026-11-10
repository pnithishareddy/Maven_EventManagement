# 12 Console logs - screenshots
1. `mvn test` ending with BUILD SUCCESS and the test count.
2. App startup: 'Started EventManagementApplication in ... seconds', active profile 'dev' (or 'oracle').
3. Hibernate SQL lines (create table, insert, select).
4. AUDIT lines produced by the AOP + SpEL aspect.
5. Scheduler lines: 'Scheduler: marked N event(s) as COMPLETED' and 'Connection pool [EventHikariPool] active=..'.
6. Service timing lines from LoggingAspect (DEBUG 'finished in N ms') and a WARN line for a failed request.
7. Cache evidence: second GET /api/events/1 prints no SQL select for that event.
