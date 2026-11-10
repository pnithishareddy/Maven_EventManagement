-- sqlplus eventuser/event123@//localhost:1521/XEPDB1 @sql\show_tables_oracle.sql   (or SQL Developer)
SELECT table_name FROM user_tables ORDER BY table_name;
SELECT table_name, column_name, data_type, data_length, nullable
  FROM user_tab_columns ORDER BY table_name, column_id;
SELECT table_name, constraint_name, constraint_type
  FROM user_constraints WHERE constraint_type IN ('P','U','R') AND table_name NOT LIKE 'BIN$%' ORDER BY table_name, constraint_type;
