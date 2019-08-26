package com.kms.katalon.application.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.kms.katalon.core.util.ConsoleCommandExecutor;

public class MachineUtil {

    private static final String UNAVAILABLE = "N/A";

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
            commandLineResult = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(
                    new String[] { "reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography /v MachineGuid" });
        }

        return machineId;
    }

    private String parseMachineIdForLinux() {
        try {
            String commandLineResult = ConsoleCommandExecutor
                    .runConsoleCommandAndCollectFirstResult(new String[] { "cat /var/lib/dbus/machine-id" });
            // Example: MachineGuid REG_SZ efc790ec-91b4-4e8d-aaa1-5c3e815669bc
            return Arrays.asList(commandLineResult.split("REG_SZ"))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.toLowerCase())
                    .collect(Collectors.toList())
                    .get(1);
        } catch (IOException | InterruptedException e) {

        }
        return UNAVAILABLE;
    }

    private String parseMachineIdForMac() {
        String commandLineResult;
        try {
            commandLineResult = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(
                    new String[] { "ioreg -rd1 -c IOPlatformExpertDevice | grep IOPlatformUUID" });
            // Example: "IOPlatformUUID" = "D84B28E0-B054-5C67-8A5D-8F6FF1639F0B"
            return Arrays.asList(commandLineResult.split("="))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.toLowerCase())
                    .map(result -> result.replace("\"", ""))
                    .collect(Collectors.toList())
                    .get(2);
        } catch (IOException | InterruptedException e1) {

        }
        return UNAVAILABLE;
    }

}
