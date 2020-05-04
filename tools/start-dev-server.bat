SET TOOLS_DIR=%~dp0
SET PROJ_HOME=%TOOLS_DIR%..\source
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.repo && call %TOOLS_DIR%install_maven_3.5.bat && mvnw clean p2:site && mvnw -Djetty.port=9999 jetty:run"
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.p2site && call %TOOLS_DIR%install_maven_3.5.bat && mvnw -Djetty.port=33333 clean jetty:run"