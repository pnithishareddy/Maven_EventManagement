# Event Management System (Spring Boot 3.3, Java 17)

Organizers create events at venues; attendees register for events and enroll in sessions. The project also contains an
Employee API. REST API secured with HTTP Basic, documented with Swagger/OpenAPI, monitored with Actuator.

## Features
| Area | Implementation |
|---|---|
| Entities and relationships | Organizer, Venue, Event, Session, Attendee, Registration, Employee (`@OneToMany`, `@ManyToOne`, `@ManyToMany`, unique constraints) |
| DTOs with validation | Request and response records with `@NotBlank`, `@Email`, `@Min`, `@Size`, `@DecimalMin`, `@PastOrPresent` |
| Mapper | `EventMapper`, `EmployeeMapper` |
| Pagination and sorting | `?page=0&size=5&sort=salary,desc` on all list endpoints |
| Repository queries | Derived, JPQL (`@Query`), native SQL, named query (`@NamedQuery`), pessimistic lock |
| Exception handling | `@RestControllerAdvice` returning RFC 7807 `ProblemDetail` (400, 404, 409, 413) |
| ResponseEntity | All controllers (201 + Location header, 204, 200) |
| Transactions | `@Transactional` (readOnly, rollbackFor) and row locking on registration and enrollment |
| Logging | SLF4J (`@Slf4j`) in services, schedulers and aspects |
| AOP | `LoggingAspect` (execution time), `AuditAspect` |
| AOP + SpEL | `@Audited("'Registered attendee ' + #attendeeId ...")` evaluated by `AuditAspect`; SpEL in `@Cacheable` and `@PreAuthorize` |
| Cache | `@Cacheable`, `@CachePut`, `@CacheEvict` on events |
| Scheduler | `EventStatusScheduler` (cron), `ConnectionPoolMonitor` (fixed rate), scheduler thread pool |
| Connection pool | HikariCP (`EventHikariPool`) configured in `application.properties` |
| Profiles | `dev` (H2, default), `oracle`, `prod` (MySQL) |
| File upload / download | Event brochure (PDF/PNG/JPEG, 5MB) |
| Security | HTTP Basic, BCrypt, roles ADMIN and USER, `@PreAuthorize` |
| OpenAPI | Swagger UI and `/v3/api-docs` |
| Actuator | health, info, metrics, caches, scheduledtasks, loggers, mappings |
| Tests | 35+ JUnit 5 / Mockito tests, one test class per service |

## How to run
Full Windows commands for `C:\Maven Projects\Event_Management`: `docs/COMMANDS.md`. One-click scripts: `scripts\*.bat`.
```
cd /d "C:\Maven Projects\Event_Management"
mvn clean test
mvn spring-boot:run                                         (dev profile, H2, port 8081)
mvn spring-boot:run "-Dspring-boot.run.profiles=oracle"     (Oracle, env DB_URL DB_USER DB_PASSWORD)
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"       (MySQL,  env DB_URL DB_USER DB_PASSWORD)
```
Set `app.seed.enabled=true` in `application-dev.properties` to load sample data.

## Users and roles
| User | Password | Role | Can do |
|---|---|---|---|
| admin | admin123 | ADMIN | everything, including actuator endpoints |
| user | user123 | USER | all GET requests, create attendees, register, enroll, download brochure |

## URLs
- Swagger UI: http://localhost:8081/swagger-ui.html
- API docs: http://localhost:8081/v3/api-docs
- Actuator: http://localhost:8081/actuator (health and info are public)
- H2 console (dev): http://localhost:8081/h2-console, JDBC URL `jdbc:h2:mem:eventdb`, user `sa`, blank password

## Main endpoints
| Method | URL | Role |
|---|---|---|
| POST | /api/organizers, /api/venues | ADMIN |
| POST | /api/attendees | any login |
| POST / GET / PUT / DELETE | /api/events, /api/events/{id} | write: ADMIN, read: any |
| GET | /api/events/search, /by-organizer, /large | any login |
| GET | /api/events/{id}/capacity | any login |
| POST / GET | /api/events/{id}/registrations | any login |
| POST / GET | /api/events/{id}/sessions | create: ADMIN |
| POST | /api/sessions/{id}/enroll?attendeeId= | any login |
| POST / GET | /api/events/{id}/brochure | upload: ADMIN |
| POST / GET / PUT / DELETE | /api/employees, /api/employees/{id}, /api/employees/search | write: ADMIN |

A ready-made Postman collection with every request is in `postman/EventManagement.postman_collection.json`.

## Database
SQL scripts: `sql/oracle_schema.sql`, `sql/mysql_schema.sql`, `sql/verify_queries.sql`. Hibernate also creates the tables
automatically (`ddl-auto=update`). ER diagram: `docs/ER_Diagram.md`.

## Project structure
```
src/main/java/com/example/eventmgmt
  config/ controller/ service/ repository/ entity/ dto/ mapper/ aop/ scheduler/ exception/
src/test/java/...       JUnit 5 + Mockito tests
sql/  docs/  postman/  scripts/ (.bat helpers)  evidence/ (generated reports)  submission/ (screenshot checklist)
```
