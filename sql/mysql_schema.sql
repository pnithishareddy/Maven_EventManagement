-- MySQL 8 schema. Hibernate (ddl-auto=update) creates these automatically; this script is for documentation
-- or manual creation.
CREATE DATABASE IF NOT EXISTS eventdb;
USE eventdb;

CREATE TABLE organizers (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT uk_organizers_email UNIQUE (email)
);
CREATE TABLE venues (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  address  VARCHAR(255),
  capacity INT NOT NULL,
  CONSTRAINT uk_venues_name UNIQUE (name)
);
CREATE TABLE attendees (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT uk_attendees_email UNIQUE (email)
);
CREATE TABLE employees (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  email      VARCHAR(255) NOT NULL,
  department VARCHAR(255) NOT NULL,
  salary     DECIMAL(12,2) NOT NULL,
  join_date  DATE NOT NULL,
  CONSTRAINT uk_employees_email UNIQUE (email)
);
CREATE TABLE events (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  title                 VARCHAR(255) NOT NULL,
  start_time            DATETIME(6) NOT NULL,
  end_time              DATETIME(6) NOT NULL,
  max_capacity          INT NOT NULL,
  status                VARCHAR(20) NOT NULL,
  brochure_file_name    VARCHAR(255),
  brochure_content_type VARCHAR(255),
  organizer_id          BIGINT NOT NULL,
  venue_id              BIGINT NOT NULL,
  CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES organizers (id),
  CONSTRAINT fk_events_venue     FOREIGN KEY (venue_id)     REFERENCES venues (id),
  CONSTRAINT uk_events_venue_start UNIQUE (venue_id, start_time)
);
CREATE TABLE sessions (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(255) NOT NULL,
  start_time DATETIME(6),
  end_time   DATETIME(6),
  capacity   INT NOT NULL,
  event_id   BIGINT NOT NULL,
  CONSTRAINT fk_sessions_event FOREIGN KEY (event_id) REFERENCES events (id)
);
CREATE TABLE registrations (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  registered_at DATETIME(6),
  event_id      BIGINT NOT NULL,
  attendee_id   BIGINT NOT NULL,
  CONSTRAINT fk_reg_event    FOREIGN KEY (event_id)    REFERENCES events (id),
  CONSTRAINT fk_reg_attendee FOREIGN KEY (attendee_id) REFERENCES attendees (id),
  CONSTRAINT uk_reg_event_attendee UNIQUE (event_id, attendee_id)
);
CREATE TABLE session_enrollment (
  session_id  BIGINT NOT NULL,
  attendee_id BIGINT NOT NULL,
  PRIMARY KEY (session_id, attendee_id),
  CONSTRAINT fk_se_session  FOREIGN KEY (session_id)  REFERENCES sessions (id),
  CONSTRAINT fk_se_attendee FOREIGN KEY (attendee_id) REFERENCES attendees (id)
);

-- Entities use sequence ids; MySQL has no sequences, so Hibernate emulates each one with a one-row table.
CREATE TABLE organizer_seq (next_val BIGINT);
INSERT INTO organizer_seq VALUES (1);
CREATE TABLE venue_seq (next_val BIGINT);
INSERT INTO venue_seq VALUES (1);
CREATE TABLE attendee_seq (next_val BIGINT);
INSERT INTO attendee_seq VALUES (1);
CREATE TABLE employee_seq (next_val BIGINT);
INSERT INTO employee_seq VALUES (1);
CREATE TABLE event_seq (next_val BIGINT);
INSERT INTO event_seq VALUES (1);
CREATE TABLE session_seq (next_val BIGINT);
INSERT INTO session_seq VALUES (1);
CREATE TABLE registration_seq (next_val BIGINT);
INSERT INTO registration_seq VALUES (1);
