@echo off
REM Opens every page that needs a screenshot (app must be running).
REM Actuator pages other than health/info ask for a login: admin / admin123
start "" http://localhost:8081/swagger-ui.html
start "" http://localhost:8081/v3/api-docs
start "" http://localhost:8081/actuator
start "" http://localhost:8081/actuator/health
start "" http://localhost:8081/actuator/info
start "" http://localhost:8081/actuator/metrics
start "" http://localhost:8081/actuator/metrics/hikaricp.connections.active
start "" http://localhost:8081/actuator/metrics/http.server.requests
start "" http://localhost:8081/actuator/metrics/jvm.memory.used
start "" http://localhost:8081/actuator/caches
start "" http://localhost:8081/actuator/scheduledtasks
start "" http://localhost:8081/actuator/loggers/com.example.eventmgmt
start "" http://localhost:8081/actuator/mappings
start "" http://localhost:8081/h2-console
