#!/usr/bin/env bash
set -xe

prepare() {
  echo "PREPARING"

  mkdir "$jacocoReportDir"
  touch "$jacocoExecFile"
  touch "$jacocoReportXmlFile"
}

extractKatalonBundleClasses() {
  echo "EXTRACT KATALON BUNDLE CLASSES"

  if [ -d "$katalonBundleClasses" ]; then return; fi
  
  mkdir "$katalonBundleClasses"

  jarDir="$kreDir/plugins"
  outputDir=$katalonBundleClasses

  source "$webTestTools/extract-katalon-bundles.sh" "$jarDir" "$outputDir"
}

injectJacocoAgentForExecution() {
  echo "INJECT JACOCO AGENT FOR EXECUTION"

  jacocoAgent="$jacocoLibDir/jacocoagent.jar"

  source "$webTestTools/inject-jacocoagent.sh" "$testProjectDir" "$jacocoAgent" "$jacocoExecFile"
}

katalonRepoDir="$1"
testProjectDir="$2"
kreDir="$3"

tools="$katalonRepoDir/tools"
webTestTools="$tools/web-test"
jacocoLibDir="$tools/jacoco/lib"

jacocoReportDir="$testProjectDir/jacoco-report"
jacocoExecFile="$jacocoReportDir/jacoco.exec"
jacocoReportXmlFile="$jacocoReportDir/jacoco.xml"

katalonBundleClasses="$katalonRepoDir/katalon-bundle-classes"

prepare
extractKatalonBundleClasses
injectJacocoAgentForExecution
