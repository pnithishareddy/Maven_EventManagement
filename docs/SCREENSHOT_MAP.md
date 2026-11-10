# Screenshot map: every service class, Swagger and Actuator

Start the app (`mvn spring-boot:run`), import the Postman collection, run the folders top to bottom.
Base URL `http://localhost:8081`. Login admin / admin123 unless a row says otherwise.

## Postman: one row per service class
| Service class | Postman folder | Requests to screenshot (name [expected status]) |
|---|---|---|
| `ReferenceDataService` (organizers, venues, attendees) | 02 Setup data | POST Create organizer [201], POST Create venue [201], POST Create attendee Ben/Cara/Dev [201], POST Duplicate organizer email [409], GET List organizers/venues/attendees [200] |
| `EventService` | 05 Events | POST Create event [201] (folder 02), GET event 1 twice (cache), GET pagination page 0 and 1, GET sort title / startTime, GET filter by venue, GET /search, GET /by-organizer, GET /large, PUT Update event 1 [200], POST Capacity above venue [400], POST Same venue and start [409], POST blank title [400], GET Event not found [404], DELETE event 1 [204] (folder 10) |
| `RegistrationService` | 03 Registration and capacity | POST Register Ben [201], GET Capacity after 1, POST Register Cara [201], POST Event full [409], POST Duplicate registration [409], GET List registrations, GET /search?name=ben |
| `SessionService` | 04 Sessions and enrollment | POST Create session [201], POST outside event window [400], POST Enroll Ben [200], POST Session full [409], POST Not registered [400], GET List sessions |
| `EmployeeService` | 06 Employee API | POST Create Ravi/Sita/Kiran [201], GET employee 1, GET list page 0 size 2, GET sort salary desc, GET filter department, GET /search, PUT Update [200], POST Duplicate email [409], POST bad email and salary [400], DELETE employee 3 [204], GET Not found [404] |
| `FileStorageService` | 07 File upload and download | POST Upload brochure [200], POST wrong type txt [400], GET Download brochure [200] (Send and Download), GET event 1 shows hasBrochure true |
| Security (`SecurityConfig`) | 08 Security | No login [401], USER reads [200], USER creates event [403], USER deletes employee [403], USER registers attendee [201], USER actuator [403], ADMIN actuator [200] |

In every screenshot make sure these are visible: method and URL, Body (for POST/PUT), status code, response body,
and the **Test Results** tab showing PASS.

## Swagger / OpenAPI (browser)
| Page | Link |
|---|---|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| OpenAPI JSON | http://localhost:8081/v3/api-docs |
Click **Authorize**, enter admin / admin123, then expand one endpoint per tag (Events, Employees, Registrations, Sessions,
Organizers/Venues/Attendees), use Try it out > Execute, and scroll to Schemas.

## Actuator (browser, admin / admin123)
http://localhost:8081/actuator/health, /info, /metrics, /metrics/hikaricp.connections.active, /metrics/hikaricp.connections.max,
/metrics/http.server.requests, /metrics/jvm.memory.used, /caches, /scheduledtasks, /loggers/com.example.eventmgmt, /mappings.
(Run some Postman requests first so http.server.requests has data and the events cache exists.)

## Database
`sql\show_tables_h2.sql` / `show_tables_mysql.sql` / `show_tables_oracle.sql`, then `sql\verify_queries.sql`.
Table structure is described in `docs/DATABASE_TABLES.md`.
