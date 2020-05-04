SET TOOLS_DIR=%~dp0
SET PROJ_HOME=%TOOLS_DIR%..\source
cd %PROJ_HOME% && call %TOOLS_DIR%install_maven_3.5.bat && mvnw clean verify -P prod && cd %CD%