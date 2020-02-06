#!/usr/bin/env bash
set -xe

generateXmlCoverageReport() {
  echo "GENERATE XML COVERAGE REPORT"

  jacococli="$jacocoLibDir/jacococli.jar"
  
  source "$webTestTools/generate-xml-coverage-report.sh" "$jacococli" "$jacocoExecFile" "$katalonBundleClasses" "$jacocoReportXmlFile"
}

katalonRepoDir="$1"
testProjectDir="$2"

tools="$katalonRepoDir/tools"
webTestTools="$tools/web-test"
jacocoLibDir="$tools/jacoco/lib"

jacocoReportDir="$testProjectDir/jacoco-report"
jacocoExecFile="$jacocoReportDir/jacoco.exec"
jacocoReportXmlFile="$jacocoReportDir/jacoco.xml"

katalonBundleClasses="$katalonRepoDir/katalon-bundle-classes"

generateXmlCoverageReport
