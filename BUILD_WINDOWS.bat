@echo off
setlocal
cd /d "%~dp0"
echo Can JDK 17 tro len va Node.js/npm. Lan dau can Internet.
where java >nul 2>nul
if errorlevel 1 goto fail
where javac >nul 2>nul
if errorlevel 1 goto fail
where npm >nul 2>nul
if errorlevel 1 goto fail
pushd frontend
call npm ci
if errorlevel 1 goto frontend_fail
call npm run build
if errorlevel 1 goto frontend_fail
xcopy /E /I /Y "dist\*" "..\backend\src\main\resources\static\"
if errorlevel 1 goto frontend_fail
popd
pushd backend
call mvnw.cmd package
if errorlevel 1 goto backend_fail
popd
if not exist "runtime\data" mkdir "runtime\data"
if not exist "runtime\data" goto fail
copy /Y "backend\target\room-booking-1.0.0.jar" "runtime\studyroom.jar" >nul
if errorlevel 1 goto fail
for %%F in (students rooms bookings) do (
    if not exist "runtime\data\%%F.json" (
        copy "backend\data\%%F.json" "runtime\data\%%F.json" >nul
        if errorlevel 1 goto fail
    )
)
echo Build thanh cong. Chay START_WINDOWS.bat. Du lieu runtime cu duoc giu nguyen.
pause
exit /b 0
:frontend_fail
popd
goto fail
:backend_fail
popd
:fail
echo Build that bai. Xem loi phia tren. Kiem tra JDK 17+, npm va ket noi mang.
pause
exit /b 1
