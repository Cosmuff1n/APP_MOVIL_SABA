@echo off
setlocal
cd /d "%~dp0"
echo =========================================
echo SABA - Compilar APK DEBUG (Cognito real)
echo =========================================
if not exist "gradlew.bat" (
    echo ERROR: Este script debe estar en la raiz del repositorio, junto a gradlew.bat
    exit /b 1
)
echo Se utilizaran los valores de .env o variables de entorno.
echo (No se mostraran identificadores ni credenciales.)
call "gradlew.bat" :app:assembleDebug --console=plain
if errorlevel 1 (
    echo.
    echo ERROR: Gradle no pudo generar el APK. Revisa el primer error de la terminal.
    exit /b 1
)
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ERROR: Gradle finalizo, pero el archivo APK no esta en la ruta esperada.
    exit /b 1
)
echo.
echo APK GENERADA CORRECTAMENTE:
echo %CD%\app\build\outputs\apk\debug\app-debug.apk
exit /b 0
