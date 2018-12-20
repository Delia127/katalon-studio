#!/bin/bash
ulimit -c unlimited
KATABUILD=/tmp/katabuild
KATABUILD2=/tmp/katabuild2
PACKAGE_FOLDER="source/com.kms.katalon.product/target/products"
PRODUCT_NAME="Katalon_Studio"
MAC_PRODUCT_NAME="Katalon Studio"
TIMESTAMP=`date +%Y-%m-%d-%H-%M`

WINDOWS_32_FILE="${PACKAGE_FOLDER}/${PRODUCT_NAME}_Windows_32.zip"
WINDOWS_64_FILE="${PACKAGE_FOLDER}/${PRODUCT_NAME}_Windows_64.zip"

LINUX_64_NAME="${PRODUCT_NAME}_Linux_64"
LINUX_64_DIR="${PACKAGE_FOLDER}/${LINUX_64_NAME}"
LINUX_64_FILE="${LINUX_64_DIR}.tar.gz"

MAC_NAME="${PRODUCT_NAME}_MacOS"
MAC_DIR="${PACKAGE_FOLDER}"
MAC_FILE="${MAC_DIR}/${MAC_NAME}.tar.gz"
MAC_APP="${MAC_DIR}/${MAC_PRODUCT_NAME}.app"
MAC_PACKAGE="${MAC_DIR}/${MAC_PRODUCT_NAME}.dmg"

CHROME_DRIVER="${LINUX_64_DIR}/configuration/resources/drivers/chromedriver_linux64/chromedriver"
FF_DRIVER="${LINUX_64_DIR}/configuration/resources/drivers/firefox_linux64/geckodriver"

KATALON_MAC="${MAC_APP}/Contents/MacOS/katalon"
CHROME_DRIVER_MAC="${MAC_APP}/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver"
FF_DRIVER_MAC="${MAC_APP}/Contents/Eclipse/configuration/resources/drivers/firefox_mac/geckodriver"

#Process Linux packages
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

#Process MacOS package
echo "Process MacOS package ..."
sudo tar -zxf "${MAC_FILE}" -C ${MAC_DIR}

sudo chmod +x "${KATALON_MAC}"
sudo chmod +x "${CHROME_DRIVER_MAC}"
sudo chmod +x "${FF_DRIVER_MAC}"
echo "Grant executed permission for Katalon and browser drivers ... Done"

#Create .dmg file
sudo codesign --verbose --force --deep --sign "80166EC5AD274586C44BD6EE7A59F016E1AB00E4" --timestamp=none "${MAC_APP}"
echo "Codesigning ... Done"

TEMP_APP="$(pwd -P)/$MAC_APP"
echo $TEMP_APP
TEMP_PATH="$(pwd -P)/$PACKAGE_FOLDER"
sudo chmod 777 $TEMP_PATH

sudo /usr/local/bin/dropdmg --config-name "Katalon Studio" "${TEMP_APP}"
#sudo /usr/local/bin/create-dmg "${TEMP_APP}"
#cp -Rf "${MAC_APP}" /tmp/
echo "DMG packaging ... Done"
sudo rm -r "${MAC_APP}"
echo "Process MacOS package ... Done"

#Distribute packages to shared folder
#sudo mkdir -p ${HOME}/Public/KatalonStudio/
#DISTRIBUTION_FOLDER="${HOME}/Public/KatalonStudio/"
#BRANCH_FOLDER="${DISTRIBUTION_FOLDER}/${1}/${2}/" # JOB_BASE_NAME

#BRANCH_FOLDER is unique
#sudo mkdir -p $BRANCH_FOLDER

#sudo cp "${LINUX_64_FILE}" "${BRANCH_FOLDER}/"
#sudo cd "${BRANCH_FOLDER}/${LINUX_64_FILE}"
#sudo mv "${PRODUCT_NAME}_Linux_64.tar.gz" "${PRODUCT_NAME}_Linux_64-${3}.tar.gz"

#sudo cp "${MAC_FILE}" "${BRANCH_FOLDER}/"
#sudo cd "${BRANCH_FOLDER}/${MAC_DIR}"
#sudo mv "${MAC_NAME}_MacOS.tar.gz" "${MAC_NAME}_MacOS-${3}.tar.gz"

#sudo mv "$(pwd -P)/$MAC_PACKAGE" "${BRANCH_FOLDER}/"
#sudo cp "${WINDOWS_32_FILE}" "${BRANCH_FOLDER}/"
#sudo cd "${BRANCH_FOLDER}/${WINDOWS_32_FILE}"
#sudo mv "${PRODUCT_NAME}_Windows_32.zip" "${PRODUCT_NAME}_Windows_32-${3}.zip"

#sudo cp "${WINDOWS_64_FILE}" "${BRANCH_FOLDER}/"
#sudo cd "${BRANCH_FOLDER}/${WINDOWS_32_FILE}"
#sudo mv "${PRODUCT_NAME}_Windows_64.zip" "${PRODUCT_NAME}_Windows_64-${3}.zip"
#cp -Rf $CURRENT
#cd $TEMP_PATH
#sudo mv "Katalon_Studio_Linux_64.tar.gz" "Katalon_Studio_Linux_64-$1-$TIMESTAMP.tar.gz"
#sudo mv "Katalon_Studio_MacOS.tar.gz" "Katalon_Studio_MacOS-$1-$TIMESTAMP.tar.gz"
#sudo mv "Katalon_Studio_Windows_32.zip" "Katalon_Studio_Windows_32-$1-$TIMESTAMP.zip"
#sudo mv "Katalon_Studio_Windows_64.zip" "Katalon_Studio_Windows_64-$1-$TIMESTAMP.zip"
#sudo mv "Katalon Studio.dmg" "Katalon_Studio-MacOS-$1-$TIMESTAMP.dmg"
# echo $WORKSPACE
#mkdir -p $KATABUILD
#if [ ! -d "${KATABUILD}" ]; then
#  sudo mkdir -p $KATABUILD
#fi  

#Distribute builds to shared folders on macOS
#sudo mount_smbfs //katabuild:[katalon2018]@192.168.34.7/Katalon/public $KATABUILD
#Distribute builds to shared folders on macOS
#sudo mount_smbfs //katabuild:[katalon2018]@192.168.35.52/share/build $KATABUILD2
#sudo rsync -vaE --progress $DISTRIBUTION_FOLDER/ $KATABUILD/
#sudo cp -Rf $DISTRIBUTION_FOLDER/* $KATABUILD/
#cp -Rf "Katalon_Studio_Linux_64-$1-$TIMESTAMP.tar.gz" $KATABUILD/
#cp -Rf "Katalon_Studio_Windows_32-$1-$TIMESTAMP.zip" $KATABUILD/
#cp -Rf "Katalon_Studio_Windows_64-$1-$TIMESTAMP.zip" $KATABUILD/
#cp -Rf "Katalon_Studio-MacOS-$1-$TIMESTAMP.dmg" $KATABUILD/
#sudo cp -Rf $DISTRIBUTION_FOLDER/* $KATABUILD2/

# Clean up old files from shared public folder
# Get older today file name and assign to array
LST_REMOVEFILE=($(sudo ls -la /tmp/katabuild/ | grep  -v "$(date +"%b %d")" | awk '/^-/{ print $NF }'))
# for loop array then execute command to remove it
for filename in $LST_REMOVEFILE
do
  echo "Begin to clean up $filename\n"
  sudo rm -f $KATABUILD/$filename
done;

sudo umount -f $KATABUILD
echo "Distribute packages on macOS ... Done"
#sudo rsync -vaE --progress $TEMP_PATH $KATABUILD2/
#sudo cp -Rf $DISTRIBUTION_FOLDER/* $KATABUILD/
# sudo umount -f $KATABUILD2
#echo "Distribute packages ... Done"
