#!/usr/bin/env bash
set -xe

extract () {
  jarFile="$1"
  extractDir="$outputDir/${1%.jar}"
  unzip -d "$extractDir" "$jarFile"

  if [ -d "$extractDir"/resources ]; then rm -rf "$extractDir"/resources; fi
  if [ -d "$extractDir"/resource ]; then rm -rf "$extractDir"/resource; fi
}

jarsDir="$1"
outputDir="$2"

rm -rf "$outputDir"/*

cd "$jarsDir"
for file in *katalon*
do
  extract $file
done

cd $outputDir
rm com.kms.katalon.execution.webservice*/com/kms/katalon/execution/webservice/contribution/WebServiceConfigurationContributor.class