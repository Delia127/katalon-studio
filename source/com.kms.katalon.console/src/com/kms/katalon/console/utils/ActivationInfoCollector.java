package com.kms.katalon.console.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.Platform;

import com.google.gson.JsonObject;
import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.console.constants.ConsoleStringConstants;
import com.kms.katalon.logging.LogUtil;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ActivationInfoCollector {
    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    protected ActivationInfoCollector() {
    }

    public static boolean isActivated() {
        String activatedVal = ApplicationInfo.getAppProperty(ConsoleStringConstants.ACTIVATED_PROP_NAME);
        if (activatedVal == null) {
            return false;
        }
        try {
            String[] activateParts = activatedVal.split("_");
            String oldVersion = new StringBuilder(activateParts[0]).reverse().toString();
            String curVersion = ApplicationInfo.versionNo().replaceAll("\\.", "");
            if (oldVersion.equals(curVersion) == false) {
                return false;
            }
            int activatedHashVal = Integer.parseInt(activateParts[1]);
            return activatedHashVal == getHostNameHashValue();
        } catch (Exception ex) {
            LogUtil.logError(ex);
            return false;
        }
    }

    public static boolean checkConsoleActivation(String[] arguments) {
        if (isActivated()) {
            return true;
        }
        String[] emailPass = getEmailAndPassword(arguments);
        String email = emailPass[0], password = emailPass[1];
        StringBuilder errorMessage = new StringBuilder();
        if (email == null || password == null || !activate(email, password, errorMessage)) {
            System.out.println(email == null || password == null ? ConsoleMessageConstants.KATALON_NOT_ACTIVATED
                    : errorMessage.toString());
            return false;
        }
        return true;
    }

    private static String[] getEmailAndPassword(String[] arguments) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(ConsoleStringConstants.ARG_EMAIL).withRequiredArg().ofType(String.class);
        parser.accepts(ConsoleStringConstants.ARG_PASSWORD).withRequiredArg().ofType(String.class);

        OptionSet argumentSet = parser.parse(arguments);
        String[] emailPass = { null, null };
        if (argumentSet.has(ConsoleStringConstants.ARG_EMAIL)) {
            emailPass[0] = argumentSet.valueOf(ConsoleStringConstants.ARG_EMAIL).toString();
        }
        if (argumentSet.has(ConsoleStringConstants.ARG_PASSWORD)) {
            emailPass[1] = argumentSet.valueOf(ConsoleStringConstants.ARG_PASSWORD).toString();
        }
        return emailPass;
    }

    private static int getHostNameHashValue() throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        if (hostName.equals(ipAddress)) {
            hostName = DEFAULT_HOST_NAME;
        }

        return Objects.hash(hostName);
    }

    private static String collectActivationInfo(String userName, String pass) throws UnknownHostException {
        String katVersion = ApplicationInfo.versionNo() + " build " + ApplicationInfo.buildNo();
        String osType = Platform.getOSArch().contains("64") ? "64" : "32";

        JsonObject traits = new JsonObject();
        // Need to escape Java string for password and single quote character for EcmaScript
        // NOTE that StringEscapeUtils.escapeEcmaScript() will do the same as StringEscapeUtils.escapeJava()
        // and escape single quote (') and slash (/) also. But we do not want to escape slash in Java
        traits.addProperty("password", StringEscapeUtils.escapeJava(pass).replace("'", "\\'"));
        traits.addProperty("host_name", InetAddress.getLocalHost().getHostName());
        traits.addProperty("os", Platform.getOS());
        traits.addProperty("os_type", osType);
        traits.addProperty("kat_version", katVersion);
        traits.addProperty("kat_type", System.getProperty("sun.arch.data.model"));

        JsonObject activationObject = new JsonObject();
        activationObject.addProperty("userId", userName);
        activationObject.add("traits", traits);

        // IMPORTANT: Please do NOT use Gson nor GsonBuilder to build the JSON string
        // They will encode single quote (') character as \u0027
        // The better way is use JsonObject.toString()
        return activationObject.toString();
    }

    public static boolean activate(String userName, String pass, StringBuilder errorMessage) {
        boolean activatedResult = false;

        try {
            String userInfo = collectActivationInfo(userName, pass);
            String result = ServerAPICommunicationUtil.post("/segment/identify", userInfo);
            if (result.equals(ConsoleMessageConstants.SEND_SUCCESS_RESPONSE)) {
                markActivated(userName);
                activatedResult = true;
            } else if (errorMessage != null) {
                errorMessage.append(ConsoleMessageConstants.ACTIVATE_INFO_INVALID);
            }

        } catch (IOException ex) {
            LogUtil.logError(ex, ConsoleMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
            if (errorMessage != null) {
                errorMessage.delete(0, errorMessage.length());
                errorMessage.append(ConsoleMessageConstants.NETWORK_ERROR);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }

        return activatedResult;
    }

    public static boolean activate(String activationCode, StringBuilder errorMessage) {
        try {
            String checkCode = activationCode.substring(0, 2);
            activationCode = new StringBuilder(activationCode.substring(2)).reverse().toString();
            int idx = Integer.parseInt(checkCode.charAt(0) + "");
            if (activationCode.charAt(idx) == checkCode.charAt(1)) {
                markActivated(activationCode);
                return true;
            } else if (errorMessage != null) {
                errorMessage.append(ConsoleMessageConstants.ACTIVATION_CODE_INVALID);
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
            if (errorMessage != null) {
                errorMessage.append(ConsoleMessageConstants.ACTIVATION_CODE_INVALID);
            }
        }

        return false;
    }

    private static void markActivated(String userName) throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.removeAppProperty(ConsoleStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal,
                true);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.ARG_EMAIL, userName, true);
    }
}
