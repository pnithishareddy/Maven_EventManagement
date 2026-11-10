@echo off
REM Creates Event_Management_submission.zip next to the project folder (target and uploads are left out).
cd /d "%~dp0.."
powershell -NoProfile -Command "$p=(Get-Location).Path; $z=Join-Path (Split-Path $p) 'Event_Management_submission.zip'; if(Test-Path $z){Remove-Item $z}; $items=Get-ChildItem -Force | Where-Object { $_.Name -notin 'target','uploads','.idea' }; Compress-Archive -Path $items.FullName -DestinationPath $z; Write-Host ('Created ' + $z)"
pause
