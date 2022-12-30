@echo off

set WORKDIR=%CD%

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

call gradlew.bat wrapper --gradle-version=4.10.3

cd /d "%WORKDIR%"

pause