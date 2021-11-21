@echo off

set WORKDIR=%CD%

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

call gradlew.bat clean build

cd /d "%WORKDIR%"

pause