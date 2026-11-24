@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------
@Echo Off
SetLocal EnableDelayedExpansion

REM Kiem tra neu da cai mvn tren may thi dung luon, neu chua thi tu dong tai Apache Maven 3.9.6
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn %*
    exit /b !ERRORLEVEL!
)

set "MAVEN_VERSION=3.9.6"
set "WRAPPER_DIR=%~dp0.mvn\wrapper"
set "MAVEN_HOME_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_CMD=%MAVEN_HOME_DIR%\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo [Maven Wrapper] Dang tai Apache Maven %MAVEN_VERSION% ve may...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$ErrorActionPreference = 'Stop'; " ^
        "$zipPath = Join-Path $env:TEMP 'apache-maven.zip'; " ^
        "New-Item -ItemType Directory -Force -Path '%MAVEN_HOME_DIR%' | Out-Null; " ^
        "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $zipPath; " ^
        "Expand-Archive -Path $zipPath -DestinationPath '%MAVEN_HOME_DIR%' -Force; " ^
        "Remove-Item $zipPath -Force"
)

call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
