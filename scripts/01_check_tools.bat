@echo off
cd /d "%~dp0.."
echo ===== Project folder =====
cd
echo.
echo ===== Java =====
java -version
echo.
echo ===== Maven =====
call mvn -version
echo.
echo ===== Git =====
git --version
echo.
echo ===== curl / node (optional) =====
curl --version | findstr /b curl
node -v
pause
