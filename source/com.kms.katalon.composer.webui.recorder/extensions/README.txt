To pack extensions for web browsers after update the scripts to use with the application, do the following:

* Firefox:
- Install jpm (https://developer.mozilla.org/en-US/Add-ons/SDK/Tools/jpm)
- Build the extension using jpm xpi
- Sign the exension xpi file with jpm sign (https://wiki.mozilla.org/Add-ons/Extension_Signing)

* IE:
- Open Solution using Visual Studio 2013
- Build solution
- The setup file to install IE addon is in "Setup" project output.
- Assembly sign key is "kms@2015"

After build completed, update the packed extensions in the <recorder project location>\resources\extensions folder.