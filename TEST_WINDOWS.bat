@echo off
setlocal
cd /d "%~dp0backend"
call mvnw.cmd test
set "TEST_EXIT=%ERRORLEVEL%"
pause
exit /b %TEST_EXIT%
