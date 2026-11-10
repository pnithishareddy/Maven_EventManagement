@echo off
cd /d "%~dp0.."
call mvn clean test
echo.
echo Screenshot this window: it must end with BUILD SUCCESS and "Tests run: 36".
pause
