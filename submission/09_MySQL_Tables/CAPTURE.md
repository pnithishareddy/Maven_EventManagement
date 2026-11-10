# 09 Database tables - screenshots
(MySQL Workbench, Oracle SQL Developer, or the H2 console - use whichever database you ran.)
1. List of tables: Oracle `SELECT table_name FROM user_tables;`  MySQL `SHOW TABLES;`
2. SELECT * FROM events; organizers; venues; attendees; registrations; sessions; session_enrollment; employees (with rows).
3. Table structure / constraints: MySQL `DESCRIBE events;`  Oracle user_constraints query from sql/verify_queries.sql.
4. The unique constraint working: duplicate email request returning 409 in Postman.
Scripts: sql/oracle_schema.sql, sql/mysql_schema.sql, sql/verify_queries.sql
