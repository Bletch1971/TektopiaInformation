@echo off

set WORKDIR=%CD%

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

call gradlew.bat clean build

xcopy "build\libs\*.jar" "..\..\1.0\*.jar" /Y /Q
xcopy "updateforge.json" "..\..\1.0\updateforge.json" /Y /Q

cd /d "%WORKDIR%"

pause