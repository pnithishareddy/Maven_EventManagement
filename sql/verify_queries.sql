-- Run these after the Postman requests and screenshot the results (works on Oracle, MySQL and H2).
SELECT * FROM organizers;
SELECT * FROM venues;
SELECT * FROM events;
SELECT * FROM attendees;
SELECT * FROM registrations;
SELECT * FROM sessions;
SELECT * FROM session_enrollment;
SELECT * FROM employees;

-- Row count per table in one result
SELECT 'organizers' AS tbl, COUNT(*) AS cnt FROM organizers UNION ALL
SELECT 'venues', COUNT(*) FROM venues UNION ALL
SELECT 'events', COUNT(*) FROM events UNION ALL
SELECT 'attendees', COUNT(*) FROM attendees UNION ALL
SELECT 'registrations', COUNT(*) FROM registrations UNION ALL
SELECT 'sessions', COUNT(*) FROM sessions UNION ALL
SELECT 'session_enrollment', COUNT(*) FROM session_enrollment UNION ALL
SELECT 'employees', COUNT(*) FROM employees;

-- Join: who is registered for which event
SELECT e.title, a.name AS attendee, r.registered_at
  FROM registrations r JOIN events e ON e.id = r.event_id JOIN attendees a ON a.id = r.attendee_id
 ORDER BY e.title, a.name;
