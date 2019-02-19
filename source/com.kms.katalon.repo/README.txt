How to use repo to include libraries from maven : 
- Edit pom.xml file to include needed maven dependencies.
- Open console command in the com.kms.katalon.repo folder and type "mvn p2:site" to generate the p2 site for the pom file.
- Type "mvn -Djetty.port=9999 jetty:run" to run the p2 site on the local machine in the url : "http://localhost:9999/site/".
- Go into project "com.kms.katalon.target", add the local p2 site in to the Locations panel.
- Go into Eclipse and Window\Preferences\Plug-in Developmenet\Target Platform, and choose com.kms.katalon.target as the active target platform.
- Now the added dependencies can be referenced like all the libraries from eclipse.


To include local libs instead of maven: 
- Use command "mvn install:install-file -Dfile=? -DgroupId=? -DartifactId=? -Dversion=? -Dpackaging=jar -DlocalRepositoryPath=?" to install into local repo path
	+ -Dfile : location of local jar file.
	+ -DgroupId : group id of the jar.
	+ -DartifactId : artifact id of the jar
	+ -Dversion : version of the jar
	+ -DlocalRepositoryPath : local repo path ("{Source path}\com.kms.katalon.repo\repo");
