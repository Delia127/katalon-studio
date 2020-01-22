#!/usr/bin/env bash
sonarScanner="$1"
sonarScannerPropertiesFile="$2"
katalonDir="$3"
katalonClassDir="$4"
jacocoXmlReport="$5"

echo "sonar.login=eb7853043bc93b04746ceb191f32d410d3670716" >> "$sonarScannerPropertiesFile"
echo "sonar.projectKey=KS" >> "$sonarScannerPropertiesFile"
echo "sonar.projectName=Katalon Studio" >> "$sonarScannerPropertiesFile"
echo "sonar.projectBaseDir=$katalonDir" >> "$sonarScannerPropertiesFile"
echo "sonar.sources=source" >> "$sonarScannerPropertiesFile"
echo "sonar.java.binaries=$katalonClassDir" >> "$sonarScannerPropertiesFile"
echo "sonar.coverage.jacoco.xmlReportPaths=$jacocoXmlReport" >> "$sonarScannerPropertiesFile"
echo "sonar.inclusions=**/*.java,**/*.groovy" >> "$sonarScannerPropertiesFile"

"$sonarScanner" -Dproject.settings="$sonarScannerPropertiesFile"