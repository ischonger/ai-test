@REM ----------------------------------------------------------------------------
@REM Maven Wrapper (script-only, simplified)
@REM Bootstraps Apache Maven into %USERPROFILE%\.m2\wrapper\dists on first use.
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "MAVEN_VERSION=3.9.9"
set "WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin"
set "MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%"
set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
set "DIST_ZIP=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Apache Maven %MAVEN_VERSION%...
    if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%DIST_ZIP%'; Expand-Archive -Path '%DIST_ZIP%' -DestinationPath '%WRAPPER_DIR%' -Force"
    if errorlevel 1 (
        echo Failed to download or extract Maven.
        exit /b 1
    )
)

call "%MAVEN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
