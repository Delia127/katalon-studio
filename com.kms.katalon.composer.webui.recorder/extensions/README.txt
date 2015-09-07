To pack extensions for web browsers after update the scripts to use with the application, do the following:

* Firefox:
- Install Firefox Addon SDK
- Run a console and activate the console using the firefox addon SDK
- cd into the Firefox\Recorder and run the command "cfx xpi"

* IE:
- Open Solution using Visual Studio 2013
- Build solution
- The setup file to install IE addon is in "Setup" project output.

After build completed, update the packed extensions in the <recorder project location>\resources\extensions folder.