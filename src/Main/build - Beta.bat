@echo off

set WORKDIR=%CD%

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

rmdir build\classes /S /Q
rmdir build\sources /S /Q
rmdir build\resources /S /Q

call gradlew.bat build

xcopy "build\libs\*.jar" "..\..\beta\1.0\*.jar" /Y /Q
xcopy "updateforge.json" "..\..\beta\1.0\updateforge.json" /Y /Q

cd /d "%WORKDIR%"

pause