#!/usr/bin/env bash
sonarScanner="$1"
sonarScannerPropertiesFile="$2"
katalonDir="$3"
katalonClassDir="$4"
jacocoXmlReports="$5"

echo "sonar.login=eb7853043bc93b04746ceb191f32d410d3670716" >> "$sonarScannerPropertiesFile"
echo "sonar.projectKey=KS" >> "$sonarScannerPropertiesFile"
echo "sonar.projectName=Katalon Studio" >> "$sonarScannerPropertiesFile"
echo "sonar.sources=source" >> "$sonarScannerPropertiesFile"
echo "sonar.inclusions=**/*.java,**/*.groovy" >> "$sonarScannerPropertiesFile"

"$sonarScanner" \
    -Dproject.settings="$sonarScannerPropertiesFile" \
    -Dsonar.java.binaries="$katalonClassDir" \
    -Dsonar.coverage.jacoco.xmlReportPaths="$jacocoXmlReports"
