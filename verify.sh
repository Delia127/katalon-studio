#!/bin/bash

BRANCH=${JOB_BASE_NAME}

PRODUCT_NAME="Katalon\ Studio"
VOLUME_DIR="/Volumes/${PRODUCT_NAME}"
PACKAGE_DIR="${HOME}/Public/KatalonStudio/${BRANCH}"
MAC_DMG="${PRODUCT_NAME}.dmg"
MAC_APP="${PRODUCT_NAME}.app"


#mkdir -p $PACKAGE_DIR
# "Installing ${PRODUCT_NAME} ..."
#hdiutil unmount "${VOLUME_DIR}"
#hdiutil mount "${PACKAGE_DIR}/${MAC_DMG}"

#rm -rf "/Applications/${MAC_APP}"
#cp -R "${VOLUME_DIR}/${MAC_APP}" /Applications
#echo "Installing ${PRODUCT_NAME} ... Done"

# echo "Verify package ..."
# "/Applications/${MAC_APP}/Contents/MacOS/katalon" --args -noSplash  -runMode=console -consoleLog -projectPath="${HOME}/KatalonStudio/TEST WEBUI/TEST WEBUI.prj" -retry=0 -testSuitePath="Test Suites/TS_RegressionTest" -executionProfile="default" -browserType="Chrome" --extra -katalon.buildLabel="${BRANCH}"
