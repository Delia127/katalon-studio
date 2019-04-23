##  Development environment build
- Check out source code from Git to your local, please make sure you have all Katalon plug-ins (sub projects with prefix names "com.kms.katalon...") under "source" folder

- A pre-configured Eclipse is available at https://drive.google.com/file/d/1lop6gcc3xHEHKJL7oqEaoR6MaNnl4AKH/view?usp=sharing

- Get Eclipse RCP version 4.6.0 at https://www.eclipse.org/downloads/packages/release/neon/3

- Go to Installation Details and *UNINSTALL* EGit 4.6

- Install these plugins:
  + Groovy-Eclipse at `/source/com.kms.katalon.p2site` (Install New Software with `file://<path_to_that_directory>)
  + EGit 4.4 at http://download.eclipse.org/egit/updates-4.4.1/
  + Cucumber-Eclipse at `/source/third-party/cucumber-eclipse/build/repository/`

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


### Katalon Utility Addon
- Katalon Utilities from Recorder, ObjectSpy and Katalon Recorder are now unified into one Katalon Utility, existing separately in those locations.
- Only tested on Chrome Browser.

#### Changes compared to the previous version:
- KS: Katalon Studio.
- KU: Katalon Utility (Addon) - used to refer KU in both KS and KR.

##### KU's location:
- Location
-- Chrome Object Spy location: /../com.kms.katalon.composer.webui.objectspy/resources/extensions/Chrome/Object Spy/KR
-- Chrome Recorder location: /../com.kms.katalon.composer.webui.recorder/resources/extensions/Chrome/Recorder/KR
- Source code to follow-up: [InspectSession](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.objectspy/src/com/kms/katalon/objectspy/core/InspectSession.java) and [RecordSession](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.recorder/src/com/kms/katalon/composer/webui/recorder/core/RecordSession.java)

##### Functionalities:

| Party | ClassName | Before | After |
|-----------|-------------|----------------|--------------------------------------------------------------------------------------------------|
| KS | [HTMLElementCaptureServer.java](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.objectspy/src/com/kms/katalon/objectspy/core/HTMLElementCaptureServer.java) [HTMLElementRecorderServer.java](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.recorder/src/com/kms/katalon/composer/webui/recorder/core/HTMLElementRecorderServer.java) | Start Selenium server | Start Selenium server with a socket endpoint |
| KS | [AddonSocket.java](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.objectspy/src/com/kms/katalon/objectspy/websocket/AddonSocket.java) [RecorderAddonSocket.java](https://github.com/kms-technology/katalon/blob/merge-addon/source/com.kms.katalon.composer.webui.recorder/src/com/kms/katalon/composer/webui/recorder/websocket/RecorderAddonSocket.java) | Send REQUEST_BROWSER_INFO to on connection | Same. But if it receives another message *from KU* specifying that KU is in a WebDriver, then it will automatically send a message back *to KU* to starting recording or spying |
| KU | background.js | Send broswer info, inject content scripts into tabs to avoid reloading | Same. But only inject content scripts if *chrome_init.variables.js* is not overrided ( which means it's in active mode and not in a WebDriver ). If *chrome_init_variables.js* is overrided, then it sends a message back to KS specifying that KU is in a WebDriver. |
| KU | katalon/ku-recorder-handlers.js | Non-existent | Essentially like content/recorder-handlers.js, but sends recorded actions and elements to KS instead of to its own selenium-api |
| KU | katalon/ku-recorders.js | Non-existent | Essentially like content/recorder.js, but with added capabilities from dom_recorder.js |
| KU | katalon/ku-locatorBuilder.js | Non-existent | Essentially content/locatorBuilders.js, but with added neighbor xpaths and can handle multiple locators returned by a *single* builder |
| KU | chrome_variables_init.js | Non-existent | Now exist because we want to load KU in a ChromeDriver and this process requires a  chrome_variables.js to override. In case of active mode, this file will not be overwritten. Therefore this file contains the signature which if disappears (due to being overrided) will tell us that KU is being loaded in a WebDriver. |
| KU | dom_recorder.js | Contains the main logic of how to record elements and actions | Now only attches ku_recorders.js |

##### Request from KU to KS:
*Request Message*

| Name | Type | Description |
|-----------|-------------|------------------------------------------------------------------------------------------------------------------|
| SELENIUM_SOCKET | Constant | The message sent when chrome_init_variables.js is overrided to tell KS that KU is being loaded in a Webdriver |

### Tips and tricks when dealing with Eclipse glitches.
- IF you ever find build errors despite running the local server for dependencies, then try different combinations of the followings:
  + Project -> Clean.
  + File -> Restart.
  + Window -> Show View -> Package Explorer -> Ctrl + A to select everything and hit F5.
