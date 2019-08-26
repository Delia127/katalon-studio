package com.kms.katalon.application.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.logging.LogUtil;

public class MachineUtil {

    private static final String UNAVAILABLE = "N/A";

    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    private String machineId = StringUtils.EMPTY;

    public String getMachineId() throws IOException, InterruptedException {

        if (!machineId.equals(StringUtils.EMPTY)) {
            return machineId;
        }

        if (SystemUtils.IS_OS_MAC) {
            machineId = parseMachineIdForMac();
        } else if (SystemUtils.IS_OS_LINUX) {
            machineId = parseMachineIdForLinux();
        } else if (SystemUtils.IS_OS_WINDOWS) {
            machineId = parseMachineIdForWindows();
        } else {
            machineId = UNAVAILABLE;
        }

        return machineId;
    }

    private String parseMachineIdForWindows() {
        try {
            String commandLineResult = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(
                    new String[] { "reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography /v MachineGuid" });
            // Example: MachineGuid REG_SZ efc790ec-91b4-4e8d-aaa1-5c3e815669bc
            String parsedResult = Arrays.asList(commandLineResult.split("REG_SZ"))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.toLowerCase())
                    .collect(Collectors.toList())
                    .get(1);
            return (parsedResult.matches(UUID_REGEX) ? parsedResult : UNAVAILABLE);
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }

        return UNAVAILABLE;
    }

    private String parseMachineIdForLinux() {
        try {
            String commandLineResult = ConsoleCommandExecutor
                    .runConsoleCommandAndCollectFirstResult(new String[] { "cat /var/lib/dbus/machine-id" });
            ;
            return (commandLineResult.matches(UUID_REGEX) ? commandLineResult : UNAVAILABLE);
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }
        return UNAVAILABLE;
    }

    private String parseMachineIdForMac() {
        String commandLineResult;
        try {
            commandLineResult = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(
                    new String[] { "ioreg -rd1 -c IOPlatformExpertDevice | grep IOPlatformUUID" });
            // Example: "IOPlatformUUID" = "D84B28E0-B054-5C67-8A5D-8F6FF1639F0B"
            String parsedResult = Arrays.asList(commandLineResult.split("="))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.toLowerCase())
                    .map(result -> result.replace("\"", ""))
                    .collect(Collectors.toList())
                    .get(2);
            return (parsedResult.matches(UUID_REGEX) ? parsedResult : UNAVAILABLE);
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }
        return UNAVAILABLE;
    }

}
