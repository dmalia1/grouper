@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start script for the Signet demo system
rem
rem $Id: shutdown.bat,v 1.6 2006-02-06 06:40:00 acohen Exp $
rem ---------------------------------------------------------------------------

if exist "%JAVA_HOME%\bin\java.exe" goto okJavaHome
echo This script requires that the JAVA_HOME environment variable be properly
echo set. That means that it must name a directory which contains
echo "bin\java.exe".
goto end

:okJavaHome
set TOMCAT_DIR=jakarta-tomcat-5.0.28
set HSQLDB_DIR=hsqldb

if exist %TOMCAT_DIR% goto okTomcatHome
echo This script must be run from the signet_demo home directory. That's the
echo directory that contains the %TOMCAT_DIR% and %HSQLDB_DIR% directories.
goto end

:okTomcatHome
if exist %HSQLDB_DIR% goto okHome
echo This script must be run from the signet_demo home directory. That's the
echo directory that contains the "%TOMCAT_DIR%" and "%HSQLDB_DIR%" directories.
goto end

:okHome
set TOMCAT_EXECUTABLE_DIR=%TOMCAT_DIR%\bin
set TOMCAT_EXECUTABLE=shutdown.bat
set HSQLDB_EXECUTABLE_DIR=%HSQLDB_DIR%\lib
set HSQLDB_EXECUTABLE=hsqldb.jar

rem Check that target executables exist

if exist %TOMCAT_EXECUTABLE_DIR%\%TOMCAT_EXECUTABLE% goto okTomcatExec
echo Cannot find %TOMCAT_EXECUTABLE_DIR%\%TOMCAT_EXECUTABLE%
echo This file is needed to run this program
goto end

:okTomcatExec
if exist %HSQLDB_EXECUTABLE_DIR%\%HSQLDB_EXECUTABLE% goto okExec
echo Cannot find %HSQLDB_EXECUTABLE_DIR%\%HSQLDB_EXECUTABLE%
echo This file is needed to run this program
goto end

:okExec

set WORKING_DIR=%cd%
cd %TOMCAT_EXECUTABLE_DIR%
call %TOMCAT_EXECUTABLE%

cd "%WORKING_DIR%\%HSQLDB_EXECUTABLE_DIR%"
"%JAVA_HOME%"\bin\java.exe -jar hsqldb.jar --rcfile ..\..\config\sqltool.rc --sql "SHUTDOWN" localhost-sa


:end
