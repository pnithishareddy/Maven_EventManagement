@echo off
REM Runs the whole Postman collection from the command line and writes an HTML report you can screenshot.
REM Needs Node.js once:  winget install OpenJS.NodeJS.LTS   then reopen this window.
cd /d "%~dp0.."
if not exist evidence mkdir evidence
call npm install -g newman newman-reporter-htmlextra
call newman run "postman\EventManagement.postman_collection.json" --working-dir "postman" --reporters cli,htmlextra --reporter-htmlextra-export "evidence\newman_report.html" --reporter-htmlextra-title "Event Management API"
start "" "evidence\newman_report.html"
pause
