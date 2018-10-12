#!/bin/bash
ulimit 99999999999
KATABUILD=/tmp/katabuild
PACKAGE_FOLDER="source/com.kms.katalon.product/target/products"
PRODUCT_NAME="Katalon_Studio"
MAC_PRODUCT_NAME="Katalon Studio"

WINDOWS_32_FILE="${PACKAGE_FOLDER}/${PRODUCT_NAME}_Windows_32.zip"
WINDOWS_64_FILE="${PACKAGE_FOLDER}/${PRODUCT_NAME}_Windows_64.zip"

LINUX_64_NAME="${PRODUCT_NAME}_Linux_64"
LINUX_64_DIR="${PACKAGE_FOLDER}/${LINUX_64_NAME}"
LINUX_64_FILE="${LINUX_64_DIR}.tar.gz"

MAC_NAME="${PRODUCT_NAME}_MacOS"
MAC_DIR="${PACKAGE_FOLDER}"
MAC_FILE="${MAC_DIR}/${MAC_NAME}.tar.gz"
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
sudo tar -zxf "${MAC_FILE}" -C ${MAC_DIR}

sudo chmod +x "${KATALON_MAC}"
sudo chmod +x "${CHROME_DRIVER_MAC}"
sudo chmod +x "${FF_DRIVER_MAC}"
echo "Grant executed permission for Katalon and browser drivers ... Done"

sudo codesign --verbose --force --deep --sign "80166EC5AD274586C44BD6EE7A59F016E1AB00E4" --timestamp=none "${MAC_APP}"
echo "Codesigning ... Done"

TEMP_APP="$(pwd -P)/$MAC_APP"
echo $TEMP_APP
TEMP_PATH="$(pwd -P)/$PACKAGE_FOLDER"
chmod 777 $TEMP_PATH
/usr/local/bin/dropdmg --config-name "Katalon Studio" "$TEMP_APP"
echo "DMG packaging ... Done"
sudo rm -r "${MAC_APP}"
echo "Process MacOS package ... Done"

# Distribute packages to shared folder
sudo mkdir -p ${HOME}/Public/KatalonStudio/
DISTRIBUTION_FOLDER="${HOME}/Public/KatalonStudio/"
BRANCH_FOLDER="${DISTRIBUTION_FOLDER}/${JOB_BASE_NAME}/${BUILD_ID}" # JOB_BASE_NAME

if [ ! -d "${BRANCH_FOLDER}" ]; then
  sudo mkdir -p $BRANCH_FOLDER
fi

sudo cp "${LINUX_64_FILE}" "${BRANCH_FOLDER}/"
sudo mv "$(pwd -P)/$MAC_PACKAGE" "${BRANCH_FOLDER}/"
sudo cp "${WINDOWS_32_FILE}" "${BRANCH_FOLDER}/"
sudo cp "${WINDOWS_64_FILE}" "${BRANCH_FOLDER}/"
sudo mkdir -p $KATABUILD
sudo mount_smbfs //katabuild:[katalon2018]@192.168.34.7/Katalon/public $KATABUILD
sudo cp -Rf $DISTRIBUTION_FOLDER/* $KATABUILD/
sudo umount -f /private/tmp/katabuild
sudo umount -f $KATABUILD
echo "Distribute packages ... Done"
