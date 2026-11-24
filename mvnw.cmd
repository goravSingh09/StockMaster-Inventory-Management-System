@echo off
set "SCRIPT_DIR=%~dp0"
if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
"%SCRIPT_DIR%maven\bin\mvn.cmd" %*
