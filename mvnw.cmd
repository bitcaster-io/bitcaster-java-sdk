@echo off
setlocal
set "BASE_DIR=%~dp0"
set "WRAPPER_DIR=%BASE_DIR%.mvn\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
set "PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties"

if exist "%WRAPPER_JAR%" goto run
for /f "tokens=1,* delims==" %%A in (%PROPERTIES%) do if "%%A"=="wrapperUrl" set "WRAPPER_URL=%%B"
if not defined WRAPPER_URL (
    echo Could not find wrapperUrl in %PROPERTIES% 1>&2
    exit /b 1
)
if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
powershell -NoProfile -ExecutionPolicy Bypass -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
if errorlevel 1 exit /b 1

:run
if not defined JAVA_HOME (
    set "JAVA_CMD=java"
) else (
    set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)
"%JAVA_CMD%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
exit /b %ERRORLEVEL%