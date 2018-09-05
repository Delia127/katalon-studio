#!/bin/sh
# Remove all not-needed files, see http://www.oracle.com/technetwork/java/javase/jre-8-readme-2095710.html
# Documentation
export JRE_DIR=../windows-x64
rm -f {JRE_DIR}/jre/README.txt
rm -f {JRE_DIR}/jre/THIRDPARTYLICENSEREADME.txt
rm -f {JRE_DIR}/jre/THIRDPARTYLICENSEREADME-JAVAFX.txt
rm -f {JRE_DIR}/jre/Welcome.html
# optional files
rm -f {JRE_DIR}/jre/lib/ext/jfxrt.jar
rm -f {JRE_DIR}/jre/lib/ext/access-bridge.jar
rm -f {JRE_DIR}/jre/lib/ext/access-bridge-32.jar
rm -f {JRE_DIR}/jre/bin/rmid.*
rm -f {JRE_DIR}/jre/bin/rmiregistry.*
rm -f {JRE_DIR}/jre/bin/tnameserv.*
rm -f {JRE_DIR}/jre/bin/keytool.*
rm -f {JRE_DIR}/jre/bin/kinit.*
rm -f {JRE_DIR}/jre/bin/klist.*
rm -f {JRE_DIR}/jre/bin/ktab.*
rm -f {JRE_DIR}/jre/bin/policytool.*
rm -f {JRE_DIR}/jre/bin/orbd.*
rm -f {JRE_DIR}/jre/bin/servertool.*
rm -f {JRE_DIR}/jre/bin/javaws.*
rm -f {JRE_DIR}/jre/lib/jfr.*
rm -f {JRE_DIR}/jre/bin/javacpl.exe
rm -f {JRE_DIR}/jre/bin/jabswitch.exe
rm -f {JRE_DIR}/jre/bin/java_crw_demo.dll
rm -f {JRE_DIR}/jre/bin/JavaAccessBridge-32.dll
rm -f {JRE_DIR}/jre/bin/JavaAccessBridge.dll
rm -f {JRE_DIR}/jre/bin/JAWTAccessBridge-32.dll
rm -f {JRE_DIR}/jre/bin/JAWTAccessBridge.dll
rm -f {JRE_DIR}/jre/bin/WindowsAccessBridge-32.dll
rm -f {JRE_DIR}/jre/bin/WindowsAccessBridge.dll
rm -f {JRE_DIR}/jre/bin/wsdetect.dll
rm -f {JRE_DIR}/jre/bin/deploy.dll
rm -f {JRE_DIR}/jre/bin/javacpl.cpl
rm -f {JRE_DIR}/jre/lib/deploy.jar
rm -f {JRE_DIR}/jre/lib/plugin.jar
rm -Rf /s /q {JRE_DIR}/jre/bin/dtplugin
rm -Rf /s /q {JRE_DIR}/jre/bin/plugin2
rm -Rf /s /q {JRE_DIR}/jre/lib/deploy
# JavaFX related
rm -rf ${JRE_DIR}/jre/lib/ant-javafx.jar
rm -rf ${JRE_DIR}/jre/lib/javafx.properties
rm -rf ${JRE_DIR}/jre/lib/jfxswt.jar
rm -rf {JRE_DIR}/jre/bin/decora-sse.dll
rm -rf {JRE_DIR}/jre/bin/fxplugins.dll
rm -rf {JRE_DIR}/jre/bin/glass.dll
rm -rf {JRE_DIR}/jre/bin/glib-lite.dll
rm -rf {JRE_DIR}/jre/bin/gstreamer-lite.dll
rm -rf {JRE_DIR}/jre/bin/javafx-font.dll
rm -rf {JRE_DIR}/jre/bin/javafx_font_t2k.dll
rm -rf {JRE_DIR}/jre/bin/javafx-iio.dll
rm -rf {JRE_DIR}/jre/bin/jfxmedia.dll
rm -rf {JRE_DIR}/jre/bin/jfxwebkit.dll
rm -rf {JRE_DIR}/jre/bin/prism_common.dll
rm -rf {JRE_DIR}/jre/bin/prism-d3d.dll
rm -rf {JRE_DIR}/jre/bin/prism_es2.dll
rm -rf {JRE_DIR}/jre/bin/prism_sw.dll