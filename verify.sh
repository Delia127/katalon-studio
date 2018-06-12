#!/bin/bash

BRANCH="$1"

PACKAGE_FOLDER="source/com.kms.katalon.product/target/products"
PRODUCT_NAME="Katalon Studio"
VOLUME_DIR="/Volumes/${PRODUCT_NAME}"
MAC_DIR="${PACKAGE_FOLDER}"
MAC_DMG="${MAC_DIR}/${PRODUCT_NAME}.dmg"
MAC_APP="${MAC_DIR}/${PRODUCT_NAME}.app"

echo "Installing ${PRODUCT_NAME} ..."
hdiutil unmount "${VOLUME_DIR}"
hdiutil mount "${MAC_DMG}"

rm -rf "/Applications/${MAC_APP}"
cp -R "${VOLUME_DIR}/${MAC_APP}" /Applications
echo "Installing ${PRODUCT_NAME} ... Done"

echo "Verify package ..."
"/Applications/${MAC_APP}/Contents/MacOS/katalon" --args -noSplash  -runMode=console -consoleLog -projectPath="/Users/katalon/Katalon Studio/TEST WEBUI/TEST WEBUI.prj" -retry=0 -testSuitePath="Test Suites/TS_RegressionTest" -executionProfile="default" -browserType="Chrome" --extra -katalon.buildLabel="${BRANCH}"
