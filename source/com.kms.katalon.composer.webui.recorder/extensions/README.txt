** Old addon: (run on new browser):
	To pack extensions for web browsers after update the scripts to use with the application, do the following:
	
	* IE:
		- Install Visual Studio 2013
		- Install Installer Project extension at https://visualstudiogallery.msdn.microsoft.com/9abe329c-9bba-44a1-be59-0fbf6151054d)
		- Open Solution
		- Build Solution (Assembly sign key is "kms@2015")
		- The setup files to install IE addon is in "Setup" project output.
		- After build completed, update the setup files in the <recorder project location>\resources\extensions\IE\Recorder folder.
	
	* Chrome: as for Chrome, update the addon right in the <recorder project location>\resources\extensions\Chrome\Recorder folder as Chrome support unpacked addon
	
	* Firefox: (recorder also use <object spy project location>\extensions\Firefox\Katalon Utility for recorder)
		- To debug the addon:
		  + Open Firefox and enter "about:debugging" in url bar
		  + Check "Enable add-on debugging"
		  + Load Katalon Utility addon
		- Build the extension by submiting at https://addons.mozilla.org/en-US/developers/addons with account: katalon-service@kms-technology.com/6UlRK0vykEDoXEOZvwwy
		- After signing completed, downloaded the signed xpi file and replace the xpi file in the <recorder project location>\resources\extensions\Firefox folder.