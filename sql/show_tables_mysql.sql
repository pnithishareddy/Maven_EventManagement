-- mysql -u root -p eventdb < sql\show_tables_mysql.sql      (or paste in MySQL Workbench)
SHOW TABLES;
DESCRIBE organizers;
DESCRIBE venues;
DESCRIBE events;
DESCRIBE sessions;
DESCRIBE attendees;
DESCRIBE registrations;
DESCRIBE session_enrollment;
DESCRIBE employees;
SELECT table_name, constraint_name, constraint_type
  FROM information_schema.table_constraints WHERE table_schema = 'eventdb' ORDER BY table_name, constraint_type;
