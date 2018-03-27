##  Development environment build
- Check out source code from Git to your local, please make sure you have all Katalon plug-ins (sub projects with prefix names "com.kms.katalon...") under "source" folder

- Get Eclipse RCP version 4.6.0 at https://www.eclipse.org/downloads/packages/release/neon/3

- Go to Installation Details and uninstall EGit 4.6

- Install these plugins:
  + Groovy-Eclipse at `/source/com.kms.katalon.p2site` (Install New Software with `file://<path_to_that_directory>)
  + EGit 4.4 at http://192.168.35.33:9998/egit/updates-4.4.1/

- Installed Maven found at https://maven.apache.org/download.cgi, update PATH environment variable to include <Maven Home>\bin folder. Make sure you can execute "mvn" command from a console (terminal/cmd) window

- Open a console (terminal for Mac, cmd for Win) Windows.
  Go to Katalon source\com.kms.katalon.repo.
  Type command "mvn p2:site" and wait for maven BUILD SUCCESS. 
  Type command "mvn -Djetty.port=9999 jetty:run" to start maven local repo for Katalon

- Go to `/source/com.kms.katalon.p2site`, execute `mvn -Djetty.port=33333 jetty:run`.

- Open Eclipse with workspace point to folder "source" you've just checked out in first step, import all sub projects inside it: File->Import-General->Existing Projects into Workspace

- Wait for Eclipse build workspace completed. You will see a lot build errors, because we have not included dependencies. 

- Open/expand sub project "com.kms.katalon.target" (it is usually in the bottom of projects list in Package Explorer). 
  Double click to open file "com.kms.katalon.target.target".
  Wait for "Resolving Target Definition" 100% completed.
  On the top right corner of "com.kms.katalon.target.target" tab pane, click on "Set as Target Platform" link.
  Wait for Eclipse updating dependencies and re-build projects completed. Now you should see all build errors disappear.
  
- Open/expand sub project "com.kms.katalon" (it is usually in the top of projects list in Package Explorer).
  Double click to open file "com.kms.katalon.product". 
  On the bottom left corner of "com.kms.katalon.product" tab pane, first click on "Synchronize" to synchronize all the plugins, then click on "Launch an Eclipse application" or "Launch an Eclipse application in Debug mode" link to start Katalon.

## Production build
- Check out source code from Git to your local, please make sure you have all Katalon plug-ins (sub projects with prefix names "com.kms.katalon...") under "source" folder

- Installed Maven found at https://maven.apache.org/download.cgi, update PATH environment variable to include <Maven Home>\bin folder. Make sure you can execute "mvn" command from a console (terminal/cmd) window

- Open a console (terminal for Mac, cmd for Win) Windows.
  Go to Katalon source\com.kms.katalon.repo. 
  Type command "mvn p2:site" and wait for maven BUILD SUCCESS. 
  Type command "mvn -Djetty.port=9999 jetty:run" to start maven local repo for Katalon

- Open another console window. 
  Go to Katalon "source" folder
  Type command "mvn clean verify" and wait for Maven BUILD SUCCESS.
  Katalon builds for typical platforms (Windows, Mac, Linux) will be generated and packaged under com.kms.katalon.product\target\products
  
### Embedded Katalon Object Spy and Recorder extensions (Since Release 5.1.0)
- Location
-- Chrome Object Spy location: /../com.kms.katalon.composer.webui.objectspy/resources/extensions/Chrome/Object Spy/
-- Chrome Recorder location: /../com.kms.katalon.composer.webui.recorder/resources/extensions/Chrome/Recorder/
-- Firefox Object Spy/Recorder location: /../com.kms.katalon.composer.webui.objectspy/resources/extensions/Firefox/objectspy/
-- IE Object Spy location: /../com.kms.katalon.composer.webui.objectspy/resources/extensions/IE/Object Spy
-- IE Recorder location: /../com.kms.katalon.composer.webui.recorder/extensions/IE/RecorderExtension
- Source code to follow-up: [InspectSession](https://github.com/kms-technology/katalon/blob/Release-5.1.0/source/com.kms.katalon.composer.webui.objectspy/src/com/kms/katalon/objectspy/core/InspectSession.java) and [RecordSession](https://github.com/kms-technology/katalon/blob/Release-5.1.0/source/com.kms.katalon.composer.webui.recorder/src/com/kms/katalon/composer/webui/recorder/core/RecordSession.java)

### Katalon Utility Addon Message format (Since Release 5.3.0)
##### Request sends from object spy to Katalon Studio

*When users capture an object*

- Host: http://localhost:50001

- Method: POST

- Body: element=URIEncoder.encode(capturedObject)

- capturedObject: 

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| type | String | HTML tag name. Eg: "div", "a", "input". |
| attribute | Map | All html attributes of the captured object. Key is attribute name (String) and value is attribute value (String) |
| xpath | String | XPath of the captured object. |
| page | page object | Information of the current page that contains the captured object. |

** *page object*

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| url | String | Page url. Eg: "http://www.katalon.com" |
| title | String | Page title. Eg: "Katalon Studio" |

*When users record an object*

- Host: http://localhost:50001

- Method: POST

- Body: element=URIEncoder.encode(recordedAction)

- recordedAction: 

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| type | String | HTML tag name. Eg: "div", "a", "input". |
| attribute | Map | All html attributes of the captured object. Key is attribute name (String) and value is attribute value (String) |
| xpath | String | XPath of the captured object. |
| page | page object | Information of the current page that contains the captured object. |
| action | action object | Description of an action|

** *Action object*

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| actionName | String | Name of the action. Eg: "nagivate", "click", "sendKeys" |
| actionData | String | Data of the action |

##### Request sends from Katalon Studio to Utility Addon

*Request Message*

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| command | enum | Name of the command. Eg: REQUEST_BROWSER_INFO, BROWSER_INFO, START_INSPECT, START_RECORD, HIGHLIGHT_OBJECT |
| data | String | Follow-up data of the command. Maybe null. |
##### Source code to follow-up:
[ObjectSpy](https://github.com/kms-technology/katalon/tree/Release-5.3.0/source/com.kms.katalon.composer.webui.objectspy/src/com/kms/katalon/objectspy) and
[Recorder](https://github.com/kms-technology/katalon/tree/Release-5.3.0/source/com.kms.katalon.composer.webui.recorder/src/com/kms/katalon/composer/webui/recorder)
