package com.kms.katalon.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;

import com.google.gson.JsonObject;
import com.kms.katalon.activation.dialog.ActivationDialog;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;
import com.kms.katalon.composer.intro.FunctionsIntroductionFinishDialog;
import com.kms.katalon.composer.project.constants.CommandId;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;

public class ActivationInfoCollector {
    private static final long RANDOM_MIN = 78364164096L;

    private static final long RANDOM_MAX = 2821109907455L;

    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";
    
    private ActivationInfoCollector() {
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL("https://mar.katalon.com/api/segment/identify");
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();

        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setUseCaches(false);
        uc.setDoOutput(true);

        return uc;
    }

    private static String collectActivationInfo(String userName, String pass) throws Exception {
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

    private static String sendActivationInfo(HttpURLConnection uc, String userInfo) throws Exception {
        StringBuilder response = new StringBuilder();
        String line;
        String result = "";

        try (DataOutputStream wr = new DataOutputStream(uc.getOutputStream())) {
            wr.writeBytes(userInfo);
        } catch (IOException ex) {
            LogUtil.logError(ex, StringConstants.SEND_ACTIVATION_INFO_FAILED);
            throw ex;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            result = response.toString().trim();
        } catch (IOException ex) {
            LogUtil.logError(ex, StringConstants.SEND_ACTIVATION_INFO_FAILED);
            throw ex;
        }

        return result;
    }

    private static void markActivated() throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.removeAppProperty(StringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(StringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal, true);
    }

    public static boolean isActivated() {
        String activatedVal = ApplicationInfo.getAppProperty(StringConstants.ACTIVATED_PROP_NAME);
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

    private static int getHostNameHashValue() throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        if (hostName.equals(ipAddress)) {
            hostName = DEFAULT_HOST_NAME;
        }

        return Objects.hash(hostName);
    }

    public static boolean activate(String userName, String pass, StringBuilder errorMessage) {
        HttpURLConnection urlConnection = null;
        boolean activatedResult = false;

        try {
            urlConnection = createConnection();
            String userInfo = collectActivationInfo(userName, pass);
            String result = sendActivationInfo(urlConnection, userInfo);
            if (result.equals(StringConstants.SEND_SUCCESS_RESPONSE)) {
                markActivated();
                activatedResult = true;
            } else if (errorMessage != null) {
                errorMessage.append(StringConstants.ACTIVATE_INFO_INVALID);
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, StringConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
            if (errorMessage != null) {
                errorMessage.delete(0, errorMessage.length());
                if (ex instanceof IOException) {
                    errorMessage.append(StringConstants.NETWORK_ERROR);
                } else {
                    errorMessage.append(StringConstants.ACTIVATE_INFO_INVALID);
                }
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return activatedResult;
    }

    public static boolean activate(String activationCode, StringBuilder errorMessage) {
        try {
            String checkCode = activationCode.substring(0, 2);
            activationCode = new StringBuilder(activationCode.substring(2)).reverse().toString();
            int idx = Integer.parseInt(checkCode.charAt(0) + "");
            if (activationCode.charAt(idx) == checkCode.charAt(1)) {
                markActivated();
                return true;
            } else if (errorMessage != null) {
                errorMessage.append(StringConstants.ACTIVATION_CODE_INVALID);
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
            if (errorMessage != null) {
                errorMessage.append(StringConstants.ACTIVATION_CODE_INVALID);
            }
        }

        return false;
    }

    public static boolean checkActivation() {
        if (isActivated()) {
            return true;
        }
        int result = new ActivationDialog(null).open();
        if (result == Window.CANCEL) {
            return false;
        }
        showFunctionsIntroductionForTheFirstTime();
        return true;
    }

    public static boolean checkConsoleActivation(String[] arguments) {
        if (isActivated()) {
            return true;
        }
        String[] emailPass = getEmailAndPassword(arguments);
        String email = emailPass[0], password = emailPass[1];
        StringBuilder errorMessage = new StringBuilder();
        if (email == null || password == null || !activate(email, password, errorMessage)) {
            System.out.println(email == null || password == null ? StringConstants.KATALON_NOT_ACTIVATED
                    : errorMessage.toString());
            return false;
        }
        return true;
    }

    private static String[] getEmailAndPassword(String[] arguments) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(StringConstants.ARG_EMAIL).withRequiredArg().ofType(String.class);
        parser.accepts(StringConstants.ARG_PASSWORD).withRequiredArg().ofType(String.class);

        OptionSet argumentSet = parser.parse(arguments);
        String[] emailPass = { null, null };
        if (argumentSet.has(StringConstants.ARG_EMAIL)) {
            emailPass[0] = argumentSet.valueOf(StringConstants.ARG_EMAIL).toString();
        }
        if (argumentSet.has(StringConstants.ARG_EMAIL)) {
            emailPass[1] = argumentSet.valueOf(StringConstants.ARG_PASSWORD).toString();
        }
        return emailPass;
    }

    private static void showFunctionsIntroductionForTheFirstTime() {
        FunctionsIntroductionDialog dialog = new FunctionsIntroductionDialog(null);
        dialog.open();
        FunctionsIntroductionFinishDialog finishDialog = new FunctionsIntroductionFinishDialog(null);
        if (finishDialog.open() == Dialog.OK) {
            try {
                new CommandCaller().call(CommandId.PROJECT_ADD);
            } catch (CommandException e) {
                LogUtil.logError(e);
            }
        }
    }

    public static String genRequestActivationInfo() {
        String requestActivationCode = ApplicationInfo.getAppProperty(StringConstants.REQUEST_CODE_PROP_NAME);

        if (requestActivationCode == null || requestActivationCode.trim().length() < 1) {
            requestActivationCode = genRequestActivateOfflineCode();
            ApplicationInfo.setAppProperty(StringConstants.REQUEST_CODE_PROP_NAME, requestActivationCode, true);
        }
        return requestActivationCode;
    }

    private static String genRequestActivateOfflineCode() {
        Random random = new Random();
        long num = RANDOM_MIN + (long) ((RANDOM_MAX - RANDOM_MIN) * random.nextFloat());
        return Long.toString(num, 36).toUpperCase();
    }
}
