-- Run as eventuser to wipe leftovers from failed starts, then start the app again.
-- Errors like ORA-00942 / ORA-02289 for objects that do not exist are harmless.
DROP TABLE session_enrollment CASCADE CONSTRAINTS;
DROP TABLE registrations CASCADE CONSTRAINTS;
DROP TABLE sessions CASCADE CONSTRAINTS;
DROP TABLE events CASCADE CONSTRAINTS;
DROP TABLE employees CASCADE CONSTRAINTS;
DROP TABLE attendees CASCADE CONSTRAINTS;
DROP TABLE venues CASCADE CONSTRAINTS;
DROP TABLE organizers CASCADE CONSTRAINTS;
DROP SEQUENCE organizer_seq;
DROP SEQUENCE venue_seq;
DROP SEQUENCE attendee_seq;
DROP SEQUENCE employee_seq;
DROP SEQUENCE event_seq;
DROP SEQUENCE session_seq;
DROP SEQUENCE registration_seq;
