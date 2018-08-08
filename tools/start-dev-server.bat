SET PROJ_HOME=%~dp0..\source
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.repo && mvn -Djetty.port=9999 jetty:run"
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.p2site && mvn -Djetty.port=33333 jetty:run"