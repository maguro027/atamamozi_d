@echo off
setlocal
chcp 65001 > nul

rem --- Configuration ---
set "TARGET_DIR=E:\OneDrive\Desktop\plugin test - α\plugins"
rem ---------------------

echo [INFO] Starting Build and Deploy...
echo [INFO] Target Directory: %TARGET_DIR%

if not exist "%TARGET_DIR%" (
    echo [WARNING] Target directory "%TARGET_DIR%" does not exist.
    echo [INFO] Creating directory...
    mkdir "%TARGET_DIR%"
    if errorlevel 1 (
        echo [ERROR] Failed to create directory.
        pause
        exit /b 1
    )
)

echo [INFO] Running Maven Clean Package...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [INFO] Copying JAR files...
for %%f in (target\*.jar) do (
    echo [INFO] Deploying %%f ...
    copy /Y "%%f" "%TARGET_DIR%\"
)

echo [SUCCESS] Deployment complete.
pause