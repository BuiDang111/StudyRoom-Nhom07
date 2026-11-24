@echo off
cd /d "%~dp0frontend"
call npm ci
if errorlevel 1 goto fail
call npm run build
if errorlevel 1 goto fail
xcopy /E /I /Y dist "..\backend\src\main\resources\static"
cd ..\backend
call mvnw.cmd package
if errorlevel 1 goto fail
copy /Y target\room-booking-1.0.0.jar ..\runtime\studyroom.jar
echo Build thanh cong. Chay START_WINDOWS.bat.
pause
exit /b 0
:fail
echo Build that bai. Xem loi phia tren.
pause
exit /b 1
