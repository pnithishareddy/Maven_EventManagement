@echo off
REM Usage:  04_run_oracle.bat                      -> XE 11g (service xe, default)
REM         04_run_oracle.bat "jdbc:oracle:thin:@//localhost:1521/XEPDB1" -> XE 18c/21c
REM         04_run_oracle.bat "jdbc:oracle:thin:@localhost:1521:XE"   -> classic SID XE
cd /d "%~dp0.."
set DB_URL=jdbc:oracle:thin:@localhost:1521/xe
if not "%~1"=="" set DB_URL=%~1
set DB_USER=eventuser
set DB_PASSWORD=event123
echo Using %DB_URL% as %DB_USER%
call mvn spring-boot:run "-Dspring-boot.run.profiles=oracle"
pause
