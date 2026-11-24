@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
    echo Can cai Java 17 tro len.
    pause
    exit /b 1
)
if not exist "runtime\studyroom.jar" (
    echo Chua co ban build. Hay chay BUILD_WINDOWS.bat truoc.
    pause
    exit /b 1
)
for %%F in (students rooms bookings) do (
    if not exist "runtime\data\%%F.json" (
        echo Thieu runtime\data\%%F.json. Hay chay BUILD_WINDOWS.bat truoc.
        pause
        exit /b 1
    )
)
cd runtime
echo Mo http://localhost:8080 sau khi thay Started RoomBookingApplication.
java -jar studyroom.jar
set "APP_EXIT=%ERRORLEVEL%"
pause
exit /b %APP_EXIT%
