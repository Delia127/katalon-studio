#!/bin/bash

PACKAGE_FOLDER="source/com.kms.katalon.product/target/products"
PRODUCT_NAME="Katalon Studio"

LINUX_64_NAME="${PRODUCT_NAME} Linux 64"
LINUX_64_DIR="${PACKAGE_FOLDER}/${LINUX_64_NAME}"
LINUX_64_FILE="${LINUX_64_DIR}.tar.gz"

MAC_NAME="${PRODUCT_NAME} MacOS"
MAC_DIR="${PACKAGE_FOLDER}/${MAC_NAME}"
MAC_FILE="${MAC_DIR}.tar.gz"
MAC_APP="${MAC_DIR}/${PRODUCT_NAME}.app"
MAC_PACKAGE="${MAC_DIR}/${PRODUCT_NAME}.dmg"

CHROME_DRIVER="${LINUX_64_DIR}/configuration/resources/drivers/chromedriver_linux64/chromedriver"
FF_DRIVER="${LINUX_64_DIR}/configuration/resources/drivers/firefox_linux64/geckodriver"

KATALON_MAC="${MAC_APP}/Contents/MacOS/katalon"
CHROME_DRIVER_MAC="${MAC_APP}/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver"
FF_DRIVER_MAC="${MAC_APP}/Contents/Eclipse/configuration/resources/drivers/firefox_mac/geckodriver"

# Process Linux packages
echo "Process Linux package ..."
mkdir "${LINUX_64_DIR}"
tar -zxf "${LINUX_64_FILE}" -C "${LINUX_64_DIR}"

chmod +x "${CHROME_DRIVER}"
chmod +x "${FF_DRIVER}"
echo "Grant execute permission for browser drivers ... Done"
rm "${LINUX_64_FILE}"
tar -czf "${LINUX_64_FILE}" -C "${LINUX_64_DIR}" .
rm -r "${LINUX_64_DIR}"
echo "Process Linux package ... Done"

# Process MacOS package
echo "Process MacOS package ..."
mkdir "${MAC_DIR}"
tar -zxf "${MAC_FILE}" -C "${MAC_DIR}"

chmod +x "${KATALON_MAC}"
chmod +x "${CHROME_DRIVER_MAC}"
chmod +x "${FF_DRIVER_MAC}"
echo "Grant executed permission for Katalon and browser drivers ... Done"

codesign --verbose --force --deep --sign "FCF6BDE36FE92C01A8DCCADCFA6F3392DDC45D6C" --timestamp=none "${MAC_APP}"
echo "Codesigning ... Done"

dropdmg --config-name "Katalon Studio" "${MAC_APP}"
mv "${MAC_PACKAGE}" "${PACKAGE_FOLDER}"
echo "DMG packaging ... Done"

rm -r "${MAC_DIR}"
echo "Process MacOS package ... Done"
