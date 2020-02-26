package com.kms.katalon.application.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.util.ConsoleCommandExecutor;

public class ProcessUtil {
    
    private static String[] MAC_COMMAND = new String[] { "/bin/sh", "-c", "pgrep -i -x -u $UID katalonc | wc -l" };
    
    private static String[] LINUX_COMMAND = new String[] { "/bin/sh", "-c", "pgrep -i -x -u $UID katalonc | wc -l" };

    private static String[] WINDOW_COMMAND = new String[] {"cmd", "/c", "tasklist /fi \"imagename eq katalonc.exe\" | find /i \"katalonc.exe\" /c"};
    
    public static int countKatalonRunningSession() throws Exception {
        KatalonPackage katalonPackge = KatalonApplication.getKatalonPackage();
        if (katalonPackge == KatalonPackage.ENGINE) {
            return countKatalonCRunningSession();
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    private static int countKatalonCRunningSession() throws Exception {
        String[] command = null;
        if (SystemUtils.IS_OS_MAC) {
            command = MAC_COMMAND;
        } else if (SystemUtils.IS_OS_LINUX) {
            command = LINUX_COMMAND;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            command = WINDOW_COMMAND;
        }
        
        Map<String, String> envs = new HashMap();
        envs.put("UID", System.getProperty("user.name"));
        String kataloncProcessCount = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(command, envs, true);
        return Integer.valueOf(kataloncProcessCount.trim());
    }

}
