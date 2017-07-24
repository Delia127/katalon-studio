** Old addon: (run on new browser):
	To pack extensions for web browsers after update the scripts to use with the application, do the following:
	
	* Firefox:
		- Install jpm (https://developer.mozilla.org/en-US/Add-ons/SDK/Tools/jpm)
		- To debug the addon: 
		  + installed Firefox Nightly here https://nightly.mozilla.org/ 
		  + Run jpm run -b nightly to start nightly Firefox with the addon 
		- Build the extension using jpm xpi
		- Firefox developer account: hieumai@kms-technology.com/Kms@2016
		- Go to Firefox developer api keys website at https://addons.mozilla.org/en-US/developers/addon/api/key/ to get api-key and api-secret
		- Sign the exension xpi file with jpm sign --api-key <api-key> --api-secret <api-secret> --xpi <generated xpi> (https://wiki.mozilla.org/Add-ons/Extension_Signing)
		- Wait for Mozilla to finished reviewing the addon at https://addons.mozilla.org/en-US/developers/addons
		- After build completed, update the xpi file in the <recorder project location>\resources\extensions\Firefox folder.
	
	* IE:
		- Install Visual Studio 2013
		- Install Installer Project extension at https://visualstudiogallery.msdn.microsoft.com/9abe329c-9bba-44a1-be59-0fbf6151054d)
		- Open Solution
		- Build Solution (Assembly sign key is "kms@2015")
		- The setup files to install IE addon is in "Setup" project output.
		- After build completed, update the setup files in the <recorder project location>\resources\extensions\IE\Recorder folder.
	
	* Chrome: as for Chrome, update the addon right in the <recorder project location>\resources\extensions\Chrome\Recorder folder as Chrome support unpacked addon