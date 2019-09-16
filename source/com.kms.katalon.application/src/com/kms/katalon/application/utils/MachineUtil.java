package com.kms.katalon.application.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.logging.LogUtil;

public class MachineUtil {

    private static final String MAC_GET_MACHINE_ID_FIELD = "IOPlatformUUID";

    private static final String MAC_GET_MACHINE_ID_DELIMITER = "=";

    private static final String[] MAC_GET_MACHINE_ID_COMMAND = new String[] { "ioreg", "-rd1", "-c",
            "IOPlatformExpertDevice", "|", "grep", MAC_GET_MACHINE_ID_FIELD };

    private static final String[] LINUX_GET_MACHINE_ID_COMMAND_2 = new String[] { "cat", "/etc/machine-id" };

    private static final String[] LINUX_GET_MACHINE_ID_COMMAND_1 = new String[] { "cat", "/var/lib/dbus/machine-id" };

    private static final String WINDOWS_GET_MACHINE_ID_DELIMITER = "REG_SZ";

    private static final String[] WINDOWS_GET_MACHINE_ID_COMMAND = new String[] { "reg", "query",
            "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid" };

    private static final String UNAVAILABLE = "N/A";

    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    private static String machineId = StringUtils.EMPTY;

    public static String getMachineId() {
        // Only load machine id if not loaded or previous attempt failed
        if (!machineId.equals(StringUtils.EMPTY) && !machineId.equals(hash(UNAVAILABLE))) {
            return machineId;
        }

        if (SystemUtils.IS_OS_MAC) {
            machineId = parseMachineIdForMac();
            machineId = hash(machineId.matches(UUID_REGEX) ? appendMacAddress(machineId) : UNAVAILABLE);
        } else if (SystemUtils.IS_OS_LINUX) {
            machineId = parseMachineIdForLinux();
            // machine id on a linux is not a UUID
            machineId = hash(machineId.length() != 32 ? UNAVAILABLE : appendMacAddress(machineId));
        } else if (SystemUtils.IS_OS_WINDOWS) {
            machineId = parseMachineIdForWindows();
            machineId = hash(machineId.matches(UUID_REGEX) ? appendMacAddress(machineId) : UNAVAILABLE);
        }
        return machineId;
    }

    private static String appendMacAddress(String str) {
        return str + "_" + KatalonApplication.getMacAddress().toLowerCase();
    }

    private static String hash(String str) {
        MessageDigest md1;
        try {
            md1 = MessageDigest.getInstance("MD5");
            md1.update(str.getBytes());
            byte[] bd1 = md1.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bd1.length; i++) {
                String hex = Integer.toHexString(0xff & bd1[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 32);
        } catch (Exception e) {
            LogUtil.logError(" Cannot hash the Machine ID because: " + e.getMessage());
        }
        return str;
    }

    private static String parseMachineIdForWindows() {
        try {
            List<String> commandLineResults = ConsoleCommandExecutor
                    .runConsoleCommandAndCollectResults(WINDOWS_GET_MACHINE_ID_COMMAND);

            // Example: MachineGuid REG_SZ efc790ec-91b4-4e8d-aaa1-5c3e815669bc
            String commandLineResult = commandLineResults.stream().filter(item -> {
                return item.indexOf(WINDOWS_GET_MACHINE_ID_DELIMITER) > 0;
            }).findFirst().orElse(UNAVAILABLE);
            String parsedResult = Arrays.asList(commandLineResult.split(WINDOWS_GET_MACHINE_ID_DELIMITER))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.toLowerCase())
                    .filter(result -> result.matches(UUID_REGEX))
                    .findAny()
                    .orElse(UNAVAILABLE);

            return parsedResult;
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }

        return UNAVAILABLE;
    }

    private static String parseMachineIdForLinux() {
        try {
            String commandLineResult = ConsoleCommandExecutor
                    .runConsoleCommandAndCollectFirstResult(LINUX_GET_MACHINE_ID_COMMAND_1);
            if (commandLineResult.length() != 32) {
                commandLineResult = ConsoleCommandExecutor
                        .runConsoleCommandAndCollectFirstResult(LINUX_GET_MACHINE_ID_COMMAND_2);
            }
            return commandLineResult;
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }
        return UNAVAILABLE;
    }

    private static String parseMachineIdForMac() {
        try {
            // Returns a list of information about the device
            List<String> commandLineResult = ConsoleCommandExecutor
                    .runConsoleCommandAndCollectResults(MAC_GET_MACHINE_ID_COMMAND);

            // Extract the field of interest
            String parsedResult = commandLineResult.stream()
                    .filter(result -> result.contains(MAC_GET_MACHINE_ID_FIELD))
                    .findAny()
                    .orElse(UNAVAILABLE);

            // Example: "IOPlatformUUID" = "D84B28E0-B054-5C67-8A5D-8F6FF1639F0B"
            parsedResult = Arrays.asList(parsedResult.split(MAC_GET_MACHINE_ID_DELIMITER))
                    .stream()
                    .map(result -> result.trim())
                    .map(result -> result.replace("\"", ""))
                    .map(result -> result.toLowerCase())
                    .filter(result -> result.matches(UUID_REGEX))
                    .findAny()
                    .orElse(UNAVAILABLE);

            return parsedResult;
        } catch (IOException | InterruptedException e) {
            LogUtil.logError(e);
        }
        return UNAVAILABLE;
    }

}
