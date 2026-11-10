-- Oracle 11g XE schema (no identity columns: ids come from sequences, as the JPA entities now use).
-- Hibernate (ddl-auto=update) creates all of this automatically; use this script only for manual creation.
CREATE SEQUENCE organizer_seq     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE venue_seq         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE attendee_seq      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE employee_seq      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE event_seq         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE session_seq       START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE registration_seq  START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE organizers (
  id NUMBER(19) PRIMARY KEY, name VARCHAR2(255) NOT NULL, email VARCHAR2(255) NOT NULL,
  CONSTRAINT uk_organizers_email UNIQUE (email));
CREATE TABLE venues (
  id NUMBER(19) PRIMARY KEY, name VARCHAR2(255) NOT NULL, address VARCHAR2(255), capacity NUMBER(10) NOT NULL,
  CONSTRAINT uk_venues_name UNIQUE (name));
CREATE TABLE attendees (
  id NUMBER(19) PRIMARY KEY, name VARCHAR2(255) NOT NULL, email VARCHAR2(255) NOT NULL,
  CONSTRAINT uk_attendees_email UNIQUE (email));
CREATE TABLE employees (
  id NUMBER(19) PRIMARY KEY, name VARCHAR2(255) NOT NULL, email VARCHAR2(255) NOT NULL,
  department VARCHAR2(255) NOT NULL, salary NUMBER(12,2) NOT NULL, join_date DATE NOT NULL,
  CONSTRAINT uk_employees_email UNIQUE (email));
CREATE TABLE events (
  id NUMBER(19) PRIMARY KEY, title VARCHAR2(255) NOT NULL,
  start_time TIMESTAMP(6) NOT NULL, end_time TIMESTAMP(6) NOT NULL, max_capacity NUMBER(10) NOT NULL,
  status VARCHAR2(20) NOT NULL, brochure_file_name VARCHAR2(255), brochure_content_type VARCHAR2(255),
  organizer_id NUMBER(19) NOT NULL, venue_id NUMBER(19) NOT NULL,
  CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES organizers (id),
  CONSTRAINT fk_events_venue     FOREIGN KEY (venue_id)     REFERENCES venues (id),
  CONSTRAINT uk_events_venue_start UNIQUE (venue_id, start_time));
CREATE TABLE sessions (
  id NUMBER(19) PRIMARY KEY, title VARCHAR2(255) NOT NULL,
  start_time TIMESTAMP(6), end_time TIMESTAMP(6), capacity NUMBER(10) NOT NULL, event_id NUMBER(19) NOT NULL,
  CONSTRAINT fk_sessions_event FOREIGN KEY (event_id) REFERENCES events (id));
CREATE TABLE registrations (
  id NUMBER(19) PRIMARY KEY, registered_at TIMESTAMP(6),
  event_id NUMBER(19) NOT NULL, attendee_id NUMBER(19) NOT NULL,
  CONSTRAINT fk_reg_event    FOREIGN KEY (event_id)    REFERENCES events (id),
  CONSTRAINT fk_reg_attendee FOREIGN KEY (attendee_id) REFERENCES attendees (id),
  CONSTRAINT uk_reg_event_attendee UNIQUE (event_id, attendee_id));
CREATE TABLE session_enrollment (
  session_id NUMBER(19) NOT NULL, attendee_id NUMBER(19) NOT NULL,
  CONSTRAINT pk_session_enrollment PRIMARY KEY (session_id, attendee_id),
  CONSTRAINT fk_se_session  FOREIGN KEY (session_id)  REFERENCES sessions (id),
  CONSTRAINT fk_se_attendee FOREIGN KEY (attendee_id) REFERENCES attendees (id));
