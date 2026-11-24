@echo off
cd /d "%~dp0runtime"
java -version
if errorlevel 1 (echo Can cai Java 17 tro len. & pause & exit /b 1)
echo Mo http://localhost:8080 sau khi may chu san sang.
java -jar studyroom.jar
pause
