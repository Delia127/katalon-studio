#!/usr/bin/env bash

projectPath="$1"
katalonc="$2"

"$katalonc" \
  -noSplash \
  -runMode=console \
  -projectPath="$projectPath" \
  -retry=0 \
  -testSuitePath="Test Suites/WebUI Builtin Keywords Test" \
  -executionProfile="default" \
  -browserType="Chrome" \
  -apiKey="0d954e41-886c-4532-b12f-b04a06f84ff0" \
  /