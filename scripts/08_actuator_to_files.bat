@echo off
REM Saves every actuator response (admin login) into evidence\ so you can screenshot or attach the files.
cd /d "%~dp0.."
if not exist evidence mkdir evidence
for %%E in (health info metrics caches scheduledtasks mappings beans) do (
  curl -s -u admin:admin123 http://localhost:8081/actuator/%%E -o evidence\actuator_%%E.json
  echo saved evidence\actuator_%%E.json
)
curl -s -u admin:admin123 http://localhost:8081/actuator/metrics/hikaricp.connections.active -o evidence\actuator_hikari_active.json
curl -s -u admin:admin123 http://localhost:8081/actuator/metrics/hikaricp.connections.max    -o evidence\actuator_hikari_max.json
curl -s -u admin:admin123 http://localhost:8081/actuator/metrics/http.server.requests         -o evidence\actuator_http_requests.json
curl -s -u admin:admin123 http://localhost:8081/actuator/metrics/jvm.memory.used              -o evidence\actuator_jvm_memory.json
curl -s -u admin:admin123 http://localhost:8081/actuator/loggers/com.example.eventmgmt        -o evidence\actuator_loggers.json
curl -s http://localhost:8081/v3/api-docs -o evidence\openapi_api-docs.json
echo Done.
pause
