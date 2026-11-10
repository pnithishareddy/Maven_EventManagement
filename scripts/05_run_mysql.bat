@echo off
REM Usage: 05_run_mysql.bat  YourRootPassword
cd /d "%~dp0.."
set DB_URL=jdbc:mysql://localhost:3306/eventdb
set DB_USER=root
set DB_PASSWORD=%~1
call mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
pause
