# Exact commands (Windows Command Prompt)

Project folder: `C:\Maven Projects\Event_Management` (the folder name contains a space, so always keep the quotes).
Open **Command Prompt** (not PowerShell) and run the commands in order.

## 1. Go to the project and check tools
```
cd /d "C:\Maven Projects\Event_Management"
java -version
mvn -version
git --version
echo %JAVA_HOME%
```
Needs Java 17 or newer and Maven 3.9+.

## 2. Run the unit tests (screenshot the end: BUILD SUCCESS, Tests run: 36)
```
cd /d "C:\Maven Projects\Event_Management"
mvn clean test
```

## 3. Start the application (H2, no database install needed)
```
cd /d "C:\Maven Projects\Event_Management"
mvn spring-boot:run
```
Wait for `Started EventManagementApplication`. Leave this window open. Stop with Ctrl+C.
The H2 database lives in memory and is wiped at every restart.

## 4. Browser pages (open after the app has started)
| What | URL |
|---|---|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| OpenAPI JSON | http://localhost:8081/v3/api-docs |
| Actuator index | http://localhost:8081/actuator |
| Health (public) | http://localhost:8081/actuator/health |
| Info (public) | http://localhost:8081/actuator/info |
| Metrics list | http://localhost:8081/actuator/metrics |
| DB pool active | http://localhost:8081/actuator/metrics/hikaricp.connections.active |
| DB pool max | http://localhost:8081/actuator/metrics/hikaricp.connections.max |
| HTTP requests | http://localhost:8081/actuator/metrics/http.server.requests |
| JVM memory | http://localhost:8081/actuator/metrics/jvm.memory.used |
| Caches | http://localhost:8081/actuator/caches |
| Scheduled tasks | http://localhost:8081/actuator/scheduledtasks |
| Loggers | http://localhost:8081/actuator/loggers/com.example.eventmgmt |
| Endpoint mappings | http://localhost:8081/actuator/mappings |
| H2 console | http://localhost:8081/h2-console |

Everything under `/actuator` except health and info asks for a login: **admin / admin123**.
Or open them all at once: `scripts\06_open_browser_pages.bat`.

## 5. Postman
Postman > Import > `C:\Maven Projects\Event_Management\postman\EventManagement.postman_collection.json`.
Run the folders 01 to 10 in order, on a freshly started app. Every request now has an automatic test:
the number in square brackets in its name is the expected status code, and the **Test Results** tab shows PASS.
Screenshot each request with URL, body, status and the response visible. See `docs/SCREENSHOT_MAP.md`.

Folder 07 uses `postman\sample_brochure.pdf` (valid) and `postman\sample_invalid.txt` (rejected with 400).
If Postman cannot find them: in Body > form-data, click the file field and pick them from the postman folder.

### No screenshots by hand? Run the whole collection from the command line
```
winget install OpenJS.NodeJS.LTS
```
(close and reopen Command Prompt), then, with the app running in another window:
```
cd /d "C:\Maven Projects\Event_Management"
npm install -g newman newman-reporter-htmlextra
newman run "postman\EventManagement.postman_collection.json" --working-dir "postman" --reporters cli,htmlextra --reporter-htmlextra-export "evidence\newman_report.html"
start "" "evidence\newman_report.html"
```
The HTML report lists every request, its response and pass/fail. Screenshot it.

## 6. Show the SQL tables
### H2 (default profile)
Open http://localhost:8081/h2-console, JDBC URL `jdbc:h2:mem:eventdb`, user `sa`, blank password, Connect.
Paste `sql\show_tables_h2.sql`, then `sql\verify_queries.sql`, press Run and screenshot.

### MySQL
```
cd /d "C:\Maven Projects\Event_Management"
mysql -u root -p < sql\mysql_schema.sql
set DB_URL=jdbc:mysql://localhost:3306/eventdb
set DB_USER=root
set DB_PASSWORD=your_mysql_password
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```
In a second window, after running Postman:
```
mysql -u root -p eventdb < "C:\Maven Projects\Event_Management\sql\show_tables_mysql.sql"
mysql -u root -p eventdb < "C:\Maven Projects\Event_Management\sql\verify_queries.sql"
```

## 7. Oracle (your machine: Oracle XE 11.2, service name `xe`)
Your `lsnrctl services` shows service `xe`, so `XEPDB1` does not exist (that is XE 18c/21c). Oracle 11g has no pluggable
databases, so skip every `ALTER SESSION SET CONTAINER` / `ALTER PLUGGABLE DATABASE` line below. Create the user once:
```
sqlplus system@localhost:1521/xe
CREATE USER eventuser IDENTIFIED BY event123;
GRANT CONNECT, RESOURCE TO eventuser;
ALTER USER eventuser QUOTA UNLIMITED ON USERS;
EXIT
```
Test the login, then start the app (the script now defaults to `jdbc:oracle:thin:@localhost:1521/xe`):
```
sqlplus eventuser/event123@localhost:1521/xe
cd /d "C:\Maven Projects\Event_Management"
scripts\04_run_oracle.bat
```
Ids now come from sequences, so Oracle 11g works. If an earlier failed start left tables behind, clean them first:
```
sqlplus eventuser/event123@localhost:1521/xe @"C:\Maven Projects\Event_Management\sql\oracle_drop_all.sql"
```
Manual alternative to Hibernate's auto-create: `sql\oracle11g_schema.sql`.

### Older notes (other Oracle versions)
`ORA-12514: Listener refused the connection` means the listener is running but does not know the service name
in the URL (`XEPDB1`), or that database is not open. Find the right name:
```
net start | findstr /i oracle
lsnrctl status
lsnrctl services
```
You need `OracleServiceXE` and `OracleXETNSListener` in the first list. In `lsnrctl services` look for the service name:

| `lsnrctl services` shows | DB_URL to use |
|---|---|
| `XEPDB1` | `jdbc:oracle:thin:@//localhost:1521/XEPDB1` |
| only `XE` (Oracle XE 11g) | `jdbc:oracle:thin:@localhost:1521/XE` |
| a SID instead of a service | `jdbc:oracle:thin:@localhost:1521:XE` |

If XEPDB1 is listed as `status UNKNOWN` or is missing, open SQL*Plus as SYSDBA and run:
```
sqlplus / as sysdba
ALTER PLUGGABLE DATABASE XEPDB1 OPEN;
ALTER PLUGGABLE DATABASE XEPDB1 SAVE STATE;
ALTER SESSION SET CONTAINER = XEPDB1;
CREATE USER eventuser IDENTIFIED BY event123;
GRANT CONNECT, RESOURCE TO eventuser;
ALTER USER eventuser QUOTA UNLIMITED ON USERS;
EXIT
```
Then start the app (this passes the URL for you):
```
cd /d "C:\Maven Projects\Event_Management"
scripts\04_run_oracle.bat
scripts\04_run_oracle.bat "jdbc:oracle:thin:@localhost:1521/XE"
```
or by hand:
```
set DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1
set DB_USER=eventuser
set DB_PASSWORD=event123
mvn spring-boot:run "-Dspring-boot.run.profiles=oracle"
```
Show the tables after running Postman:
```
sqlplus eventuser/event123@//localhost:1521/XEPDB1 @"C:\Maven Projects\Event_Management\sql\show_tables_oracle.sql"
sqlplus eventuser/event123@//localhost:1521/XEPDB1 @"C:\Maven Projects\Event_Management\sql\verify_queries.sql"
```

## 8. Actuator responses saved to files
```
cd /d "C:\Maven Projects\Event_Management"
scripts\08_actuator_to_files.bat
```
Writes `evidence\actuator_*.json` and `evidence\openapi_api-docs.json`.

## 9. GitHub
Create an empty repository on github.com first.
```
cd /d "C:\Maven Projects\Event_Management"
git init
git add .
git commit -m "Event management project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/event-management.git
git push -u origin main
```

## 10. Zip for submission
```
cd /d "C:\Maven Projects\Event_Management"
scripts\09_package_for_submission.bat
```
Creates `C:\Maven Projects\Event_Management_submission.zip` without `target` and `uploads`.
