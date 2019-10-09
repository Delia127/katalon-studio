# Smart Wait Web Extension

This is the repository for Smart Wait web extension used in Katalon Studio (from version 7.0.0). It is responsible for providing the [Smart Wait Function](https://docs.katalon.com/katalon-studio/docs/webui-smartwait.html).

## Description

* ```temp-wait.js``` will contain the primary logic for waiting, namely:
  * Wait for DOM insertion/removal/modification to be done.
  * Wait for AJAX calls to be finished.
We do this by checking every 0.5 second to see if any of the above condition is achieved. The default timeout is 30s.

* ```content/wait.js``` contains the obfuscated version of ```temp-wait.js```.
* ```content/wait-injector.js``` will inject ```content/wait.js``` into the content page.

## Development

To debug:
* Modifiy the field ```web_accessible_resources```  in ```manifest.json``` to inject ```wait-temp.js```.
* Modify element ```elementForInjectingScript.src``` to get script from ```content/wait.js``` to ```wait-temp.js```.

## Build

Prerequisites:
* Have [Javascript Obfuscator npm](https://www.npmjs.com/package/javascript-obfuscator) installed globally.
* The version in ```manifest.json``` is already lifted, the build script won't lift it for you.

There is a file ```build.sh``` which was developed and tested on MacOS to automate the following step:
* Copy the files (except build folder) into build folder, specifically to chrome and firefox.
* In each folder chrome and firefix, obfuscate the ```wait-temp.js``` into ```content/wait.js``` and delete ```wait-temp.js```.
* For firefox folder, also sign the extension and download the ```.xpi``` file.

To build:
* Run ```sh build.sh``` in Mac terminal
