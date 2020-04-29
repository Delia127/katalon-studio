#!/usr/bin/env bash
jacococli="$1"
jacocoExecFile="$2"
classFileDir="$3"
xmlReportFile="$4"

java -jar "$jacococli" report "$jacocoExecFile" --classfiles "$classFileDir" --xml "$xmlReportFile"