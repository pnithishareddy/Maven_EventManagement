-- H2 console (http://localhost:8081/h2-console, JDBC URL jdbc:h2:mem:eventdb, user sa, blank password)
-- Run AFTER the app is started. Select all text and press Run.
SHOW TABLES;
SELECT table_name, column_name, data_type, character_maximum_length, is_nullable
  FROM information_schema.columns WHERE table_schema = 'PUBLIC' ORDER BY table_name, ordinal_position;
SELECT constraint_name, constraint_type, table_name
  FROM information_schema.table_constraints WHERE table_schema = 'PUBLIC' ORDER BY table_name, constraint_type;
