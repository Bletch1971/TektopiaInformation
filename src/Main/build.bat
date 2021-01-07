@echo off

set WORKDIR=%CD%

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

rmdir build\classes /S /Q
rmdir build\sources /S /Q
rmdir build\resources /S /Q

call gradlew.bat build

cd /d "%WORKDIR%"

pause