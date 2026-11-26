@echo off
title Iniciar Backend de Springboot para SABA

echo Iniciando Parking (8081)...
start "SABA Parking" /D "%~dp0springboot\parking" cmd /k "gradlew.bat bootRun"

echo Iniciando Incidents (8082)...
start "SABA Incidents" /D "%~dp0springboot\incidents" cmd /k "gradlew.bat bootRun"

echo Iniciando BFF (8080)...
start "SABA BFF" /D "%~dp0springboot\bff" cmd /k "gradlew.bat bootRun"

echo.
echo Los tres servicios se estan iniciando.
echo Revisa sus ventanas para confirmar que arrancaron.
pause
