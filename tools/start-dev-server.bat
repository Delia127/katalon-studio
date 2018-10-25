SET PROJ_HOME=%~dp0..\source
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.repo && mvn clean p2:site && mvn -Djetty.port=9999 jetty:run"
start cmd /k "cd %PROJ_HOME%\com.kms.katalon.p2site && mvn -Djetty.port=33333 clean jetty:run"