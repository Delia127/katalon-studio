[![Katalon Logo](http://katalon.kms-technology.com/assets/images/katalon_logo.png)](http://katalon.kms-technology.com)
##  Development environment build
- Check out source code from Git to your local, please make sure you have all Katalon plug-ins (sub projects with prefix names "com.kms.katalon...") under "source" folder

- Get Eclipse RCP version 4.4.2 or higher

- Get Maven installed, update PATH environment variable to include Maven Home\bin folder. Make sure you can execute "mvn" command from a console (terminal/cmd) window

- Open a console (terminal for Mac, cmd for Win) Windows.
  Go to Katalon source\com.kms.katalon.repo.
  Type command "mvn p2:site" and wait for maven BUILD SUCCESS. 
  Type command "mvn -Djetty.port=9999 jetty:run" to start maven local repo for Katalon

- Open Eclipse with workspace point to folder "source" you've just checked out in first step, import all sub projects inside it: File->Import-General->Existing Projects into Workspace

- Wait for Eclipse build workspace completed. You will see a lot build errors, because we have not included dependencies. 

- Open/expand sub project "com.kms.katalon.target" (it is usually in the bottom of projects list in Package Explorer). 
  Double click to open file "com.kms.katalon.target.target".
  Wait for "Resolving Target Definition" 100% completed.
  On the top right corner of "com.kms.katalon.target.target" tab pane, click on "Set as Target Platform" link.
  Wait for Eclipse updating dependencies and re-build projects completed. Now you should see all build errors disappear.
  
- Open/expand sub project "com.kms.katalon" (it is usually in the top of projects list in Package Explorer).
  Double click to open file "com.kms.katalon.product". 
  On the bottom left corner of "com.kms.katalon.product" tab pane, click on "Launch an Eclipse application" or "Launch an Eclipse application in Debug mode" link to start Katalon.

## Production build
- Check out source code from Git to your local, please make sure you have all Katalon plug-ins (sub projects with prefix names "com.kms.katalon...") under "source" folder

- Get Maven installed, update PATH environment variable to include Maven Home\bin folder. Make sure you can execute "mvn" command from a console (terminal/cmd) window

- Open a console (terminal for Mac, cmd for Win) Windows.
  Go to Katalon source\com.kms.katalon.repo. 
  Type command "mvn p2:site" and wait for maven BUILD SUCCESS. 
  Type command "mvn -Djetty.port=9999 jetty:run" to start maven local repo for Katalon

- Open another console window. 
  Go to Katalon "source" folder
  Type command "mvn clean verify" and wait for Maven BUILD SUCCESS.
  Katalon builds for typical platforms (Windows, Mac, Linux) will be generated and packaged under com.kms.katalon.product\target\products
  