# Entity Relationship Diagram

GitHub renders this diagram automatically. Take the screenshot from the rendered page.

```mermaid
erDiagram
    ORGANIZERS ||--o{ EVENTS : organizes
    VENUES ||--o{ EVENTS : hosts
    EVENTS ||--o{ SESSIONS : contains
    EVENTS ||--o{ REGISTRATIONS : has
    ATTENDEES ||--o{ REGISTRATIONS : makes
    SESSIONS ||--o{ SESSION_ENROLLMENT : has
    ATTENDEES ||--o{ SESSION_ENROLLMENT : joins

    ORGANIZERS {
        number id PK
        string name
        string email UK
    }
    VENUES {
        number id PK
        string name UK
        string address
        int capacity
    }
    EVENTS {
        number id PK
        string title
        timestamp start_time
        timestamp end_time
        int max_capacity
        string status
        string brochure_file_name
        string brochure_content_type
        number organizer_id FK
        number venue_id FK
    }
    SESSIONS {
        number id PK
        string title
        timestamp start_time
        timestamp end_time
        int capacity
        number event_id FK
    }
    ATTENDEES {
        number id PK
        string name
        string email UK
    }
    REGISTRATIONS {
        number id PK
        timestamp registered_at
        number event_id FK
        number attendee_id FK
    }
    SESSION_ENROLLMENT {
        number session_id PK
        number attendee_id PK
    }
    EMPLOYEES {
        number id PK
        string name
        string email UK
        string department
        decimal salary
        date join_date
    }
```

Unique constraints: organizers.email, attendees.email, employees.email, venues.name,
events(venue_id, start_time), registrations(event_id, attendee_id), session_enrollment(session_id, attendee_id).
