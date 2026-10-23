@echo off
REM Always work from the folder this file is in (fixes "no source files")
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo Java JDK not found. Install JDK 11 or newer and try again.
    pause
    exit /b 1
)

if not exist out mkdir out
javac -encoding UTF-8 -sourcepath . -d out Main.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
java -cp out Main
if errorlevel 1 pause
