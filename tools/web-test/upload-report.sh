#!/usr/bin/env bash
set -xe

uploadResultToSonar() {
  echo "UPLOAD RESULT TO SONAR"

  sonarScanner="$tools/sonar-scanner/bin/sonar-scanner"
  sonarScannerPropertiesFile="$katalonRepoDir/sonar-project.properties"
  touch "$sonarScannerPropertiesFile"

  source "$webTestTools/upload-to-sonar.sh" "$sonarScanner" "$sonarScannerPropertiesFile" "$katalonRepoDir" "$katalonBundleClasses" "$jacocoReportXmlFiles"
}

katalonRepoDir="$1"
jacocoReportXmlFiles="$2"

tools="$katalonRepoDir/tools"
webTestTools="$tools/web-test"

katalonBundleClasses="$katalonRepoDir/katalon-bundle-classes"

uploadResultToSonar
