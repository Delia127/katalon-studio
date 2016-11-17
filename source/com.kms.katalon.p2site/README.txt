How to start p2site project to host local p2 site: 
- Type "mvn -Djetty.port=33333 jetty:run" to run the p2 site on the local machine in the url : "http://localhost:33333/site/".
- Go into project "com.kms.katalon.target", add the local p2 site in to the Locations panel.
- Go into Eclipse and Window\Preferences\Plug-in Developmenet\Target Platform, and choose com.kms.katalon.target as the active target platform.
- Now the added dependencies can be referenced like all the libraries from eclipse.