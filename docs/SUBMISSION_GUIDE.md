# Complete command guide (Windows Command Prompt)

> The full, current version is in `docs/COMMANDS.md`. Screenshot checklist per service: `docs/SCREENSHOT_MAP.md`.
> Because the path `C:\Maven Projects\Event_Management` contains a space, always put it in quotes.

## A. Check tools
```
java -version
mvn -version
git --version
echo %JAVA_HOME%
```

## B. Build, test, run
```
cd /d "C:\Maven Projects\Event_Management"
mvn clean test
mvn spring-boot:run
```
Keep this window open (Ctrl+C stops the app). Wait for `Started EventManagementApplication`.
The in-memory H2 database is wiped on every restart, so after a restart run the Postman folders again from the top.

## C. Import the Postman requests
1. Postman > Import > choose `postman\EventManagement.postman_collection.json`.
2. Run the folders in order: 01 Public, 02 Setup, 03 Registration, 04 Sessions, 05 Events, 06 Employees,
   07 File upload (pick a file in the form-data field first), 08 Security, 09 Actuator, 10 Cleanup.
3. Take a screenshot of each request: URL, body, status code and response visible.
Login is already set at collection level (admin / admin123); the Security folder overrides it per request.

## D. Browser pages (screenshot them)
```
http://localhost:8081/swagger-ui.html
http://localhost:8081/v3/api-docs
http://localhost:8081/actuator/health
http://localhost:8081/h2-console
```
Actuator endpoints other than health and info ask for a login: admin / admin123.

## E. Actuator from the command line (optional)
```
curl http://localhost:8081/actuator/health
curl -u admin:admin123 http://localhost:8081/actuator/metrics
curl -u admin:admin123 http://localhost:8081/actuator/metrics/hikaricp.connections.active
curl -u admin:admin123 http://localhost:8081/actuator/caches
curl -u admin:admin123 http://localhost:8081/actuator/scheduledtasks
```

## F. Oracle
In SQL Developer as SYSTEM:
```
ALTER SESSION SET CONTAINER = XEPDB1;
CREATE USER eventuser IDENTIFIED BY event123;
GRANT CONNECT, RESOURCE TO eventuser;
ALTER USER eventuser QUOTA UNLIMITED ON USERS;
```
Then in Command Prompt:
```
set DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1
set DB_USER=eventuser
set DB_PASSWORD=event123
mvn spring-boot:run "-Dspring-boot.run.profiles=oracle"
```
Run the Postman folders, then in SQL Developer (connected as eventuser) run `sql\verify_queries.sql` and screenshot.

## G. MySQL (optional)
```
set DB_URL=jdbc:mysql://localhost:3306/eventdb
set DB_USER=root
set DB_PASSWORD=your_password
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

## H. GitHub
Create an empty repository on github.com first, then:
```
cd /d "C:\Maven Projects\Event_Management"
git init
git add .
git commit -m "Event management project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/event-management.git
git push -u origin main
```
`.gitignore` already excludes target, uploads and data. GitHub may ask you to sign in or use a personal access token.

## I. Screenshot checklist
Each folder in `submission\` has a `CAPTURE.md` listing exactly what to capture for deliverables 01 to 12.
Save your screenshots into those folders and zip the whole project for submission.
