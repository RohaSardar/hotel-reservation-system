@echo off
title Hotel Reservation System runner
echo ====================================================
echo      HOTEL RESERVATION SYSTEM COMPILER & RUNNER     
echo ====================================================
echo.

if not exist bin (
    echo Creating bin/ directory for compiled classes...
    mkdir bin
)

echo Compiling Java source files...
javac -cp "lib/sqlite-jdbc.jar" src/database/*.java src/model/*.java src/gui/*.java src/Main.java -d bin

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please verify JDK is installed and configured in PATH.
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCESS] Compilation completed!
echo Launching Hotel Reservation System...
echo.

java -cp "bin;lib/sqlite-jdbc.jar" Main

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Application terminated unexpectedly.
)
echo.
pause
