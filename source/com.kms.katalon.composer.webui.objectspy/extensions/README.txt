** Old addon: (run on new browser):
	To pack extensions for web browsers after update the scripts to use with the application, do the following:
	
	* IE:
		- Install Visual Studio 2013
		- Install Installer Project extension at https://visualstudiogallery.msdn.microsoft.com/9abe329c-9bba-44a1-be59-0fbf6151054d)
		- Open Solution
		- Build Solution (Assembly sign key is "kms@2015")
		- The setup files to install IE addon is in "Setup" project output.
		- After build completed, update the setup files in the <object spy project location>\resources\extensions\IE\Object Spy folder.
	
	* Chrome: as for Chrome, update the addon right in the <object spy project location>\resources\extensions\Chrome\Object Spy folder as Chrome support unpacked addon
	
** New addon: Katalon Utility
	* Chrome: 
		- Update the addon and debug by installing in Chrome extension feature (developer mode turned on)
		- Zip the addon with the new version and upload it in https://chrome.google.com/webstore/developer/dashboard?authuser=1
		- Chrome developer account: katalon-service@kms-technology.com/6UlRK0vykEDoXEOZvwwy
		- Wait for Chrome to finished reviewing the addon.
		
	* Firefox:
		- To debug the addon: 
		  + Open Firefox and enter "about:debugging" in url bar
		  + Check "Enable add-on debugging"
		  + Load Katalon Utility addon
		- Build the extension by submiting at https://addons.mozilla.org/en-US/developers/addons with account: katalon-service@kms-technology.com/6UlRK0vykEDoXEOZvwwy
		- After signing completed, downloaded the signed xpi file and replace the xpi file in the <object spy project location>\resources\extensions\Firefox folder.
		
	