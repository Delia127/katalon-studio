#!/bin/sh
# Remove all not-needed files, see http://www.oracle.com/technetwork/java/javase/jre-8-readme-2095710.html
# Documentation
export JRE_DIR=../macosx-x64/jre/Contents/Home
rm -f ${JRE_DIR}/jre/README.txt
rm -f ${JRE_DIR}/jre/THIRDPARTYLICENSEREADME.txt
rm -f ${JRE_DIR}/jre/THIRDPARTYLICENSEREADME-JAVAFX.txt
rm -f ${JRE_DIR}/jre/Welcome.html
# optional files 
rm -f ${JRE_DIR}/jre/lib/ext/jfxrt.jar
rm -f ${JRE_DIR}/jre/lib/ext/access-bridge.jar
rm -f ${JRE_DIR}/jre/lib/ext/access-bridge-32.jar
rm -f ${JRE_DIR}/jre/bin/rmid.*
rm -f ${JRE_DIR}/jre/bin/rmiregistry.*
rm -f ${JRE_DIR}/jre/bin/tnameserv.*
rm -f ${JRE_DIR}/jre/bin/keytool.*
rm -f ${JRE_DIR}/jre/bin/kinit.*
rm -f ${JRE_DIR}/jre/bin/klist.*
rm -f ${JRE_DIR}/jre/bin/ktab.*
rm -f ${JRE_DIR}/jre/bin/policytool.*
rm -f ${JRE_DIR}/jre/bin/orbd.*
rm -f ${JRE_DIR}/jre/bin/servertool.*
rm -f ${JRE_DIR}/jre/bin/javaws.*
rm -f ${JRE_DIR}/jre/lib/jfr.*
# can be deleted when private application runtime is used (like launch4j)
rm -f ${JRE_DIR}/jre/bin/java.exe
# launch4j uses javaw.exe. So do NOT delete!
rm -f ${JRE_DIR}/jre/bin/javacpl.exe
rm -f ${JRE_DIR}/jre/bin/jabswitch.exe
rm -f ${JRE_DIR}/jre/bin/java_crw_demo.dll
rm -f ${JRE_DIR}/jre/bin/JavaAccessBridge-32.dll
rm -f ${JRE_DIR}/jre/bin/JavaAccessBridge.dll
rm -f ${JRE_DIR}/jre/bin/JAWTAccessBridge-32.dll
rm -f ${JRE_DIR}/jre/bin/JAWTAccessBridge.dll
rm -f ${JRE_DIR}/jre/bin/WindowsAccessBridge-32.dll
rm -f ${JRE_DIR}/jre/bin/WindowsAccessBridge.dll
rm -f ${JRE_DIR}/jre/bin/wsdetect.dll
rm -f ${JRE_DIR}/jre/bin/deploy.dll
rm -f ${JRE_DIR}/jre/bin/javacpl.cpl
rm -f ${JRE_DIR}/jre/lib/deploy.jar
rm -f ${JRE_DIR}/jre/lib/plugin.jar
rm -Rf /s /q ${JRE_DIR}/jre/bin/dtplugin
rm -Rf /s /q ${JRE_DIR}/jre/bin/plugin2
rm -Rf /s /q ${JRE_DIR}/jre/lib/deploy
rm -Rf ${JRE_DIR}/jre/man
# JavaFX related
rm -rf ${JRE_DIR}/jre/lib/ant-javafx.jar
rm -rf ${JRE_DIR}/jre/lib/javafx.properties
rm -rf ${JRE_DIR}/jre/lib/jfxswt.jar
rm -rf ${JRE_DIR}/jre/lib/fxplugins.dylib
rm -rf ${JRE_DIR}/jre/lib/libdecora_sse.so   
rm -rf ${JRE_DIR}/jre/lib/libdecora-sse.dylib
rm -rf ${JRE_DIR}/jre/lib/libfxplugins.so
rm -rf ${JRE_DIR}/jre/lib/libfxplugins.dylib
rm -rf ${JRE_DIR}/jre/lib/libglass.dylib
rm -rf ${JRE_DIR}/jre/lib/libglib-lite.dylib
rm -rf ${JRE_DIR}/jre/lib/libgstreamer-lite.dylib
rm -rf ${JRE_DIR}/jre/lib/libjavafx_font_t2k.dylib   
rm -rf ${JRE_DIR}/jre/lib/libjavafx-font.dylib
rm -rf ${JRE_DIR}/jre/lib/libjavafx_font.dylib
rm -rf ${JRE_DIR}/jre/lib/libjavafx-iio.dylib
rm -rf ${JRE_DIR}/jre/lib/libjavafx_iio.dylib
rm -rf ${JRE_DIR}/jre/lib/libjfxmedia.dylib
rm -rf ${JRE_DIR}/jre/lib/libjfxwebkit.dylib
rm -rf ${JRE_DIR}/jre/lib/libjfxwebkit_avf.dylib
rm -rf ${JRE_DIR}/jre/lib/libprism_common.dylib   
rm -rf ${JRE_DIR}/jre/lib/libprism_sw.dylib  
rm -rf ${JRE_DIR}/jre/lib/libprism-es2.dylib