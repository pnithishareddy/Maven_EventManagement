@echo off
REM Default profile: in-memory H2 on http://localhost:8081  (data is wiped on every restart)
cd /d "%~dp0.."
call mvn spring-boot:run
pause
