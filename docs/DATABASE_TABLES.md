# Database tables (8 tables)

Created automatically by Hibernate (`ddl-auto=update`) or manually with `sql/mysql_schema.sql` / `sql/oracle_schema.sql`.
Display them with `sql/show_tables_h2.sql`, `sql/show_tables_mysql.sql` or `sql/show_tables_oracle.sql`.

| # | Table | Columns | Keys and constraints |
|---|---|---|---|
| 1 | `organizers` | id, name, email | PK id; UNIQUE email |
| 2 | `venues` | id, name, address, capacity | PK id; UNIQUE name |
| 3 | `attendees` | id, name, email | PK id; UNIQUE email |
| 4 | `employees` | id, name, email, department, salary (12,2), join_date | PK id; UNIQUE email |
| 5 | `events` | id, title, start_time, end_time, max_capacity, status, brochure_file_name, brochure_content_type, organizer_id, venue_id | PK id; FK organizer_id -> organizers; FK venue_id -> venues; UNIQUE (venue_id, start_time) |
| 6 | `sessions` | id, title, start_time, end_time, capacity, event_id | PK id; FK event_id -> events |
| 7 | `registrations` | id, registered_at, event_id, attendee_id | PK id; FK event_id -> events; FK attendee_id -> attendees; UNIQUE (event_id, attendee_id) |
| 8 | `session_enrollment` | session_id, attendee_id | PK (session_id, attendee_id); FK session_id -> sessions; FK attendee_id -> attendees |

Relationships: an organizer and a venue each have many events; an event has many sessions and many registrations;
an attendee has many registrations; sessions and attendees are many-to-many through `session_enrollment`.
ER diagram (rendered on GitHub): `docs/ER_Diagram.md`.

H2 stores table names in upper case (`EVENTS`), Oracle too; MySQL keeps lower case.

Ids: each entity uses a sequence (`organizer_seq`, `venue_seq`, `attendee_seq`, `employee_seq`, `event_seq`, `session_seq`, `registration_seq`).
Oracle and H2 create real sequences; MySQL gets one-row `*_seq` tables. Oracle 11g script: `sql/oracle11g_schema.sql`.
