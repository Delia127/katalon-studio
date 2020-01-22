#!/bin/sh
testProjectDir="$1"
jacocoAgent="$2"
reportFile="$3"

projectSettingsDir="$testProjectDir/settings/internal"
cd "$projectSettingsDir"

launchProperty="execution.launch.vmArgs=\"-javaagent\:$jacocoAgent\=destfile\=$reportFile\""
echo $launchProperty > "com.kms.katalon.execution.setting.properties" 
