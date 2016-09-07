package com.kms.katalon.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;

import com.kms.katalon.activation.dialog.ActivationDialog;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;
import com.kms.katalon.composer.intro.FunctionsIntroductionFinishDialog;
import com.kms.katalon.composer.project.constants.CommandId;
import com.kms.katalon.core.application.Application.RunningModeParam;
import com.kms.katalon.logging.LogUtil;

public class ActivationInfoCollector {
    private static final String ACTIVATION_COLLECT_FAIL_MESSAGE = "Activation collection is failed";

    private static final String SEND_SUCCESS_RESPONSE = "OK";

    private static final String ACTIVATED_PROP_NAME = "activated";

    private static final String SEND_ACTIVATION_INFO_FAILED = "Send activation info failed!";

    private static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    private static final String NETWORK_ERROR = "Network error! Cannot execute activation.";

    private static final String ACTIVATE_INFO_INVALID = "Email or Password is invalid!";
    
    private static final String KATALON_NOT_ACTIVATED = "Katalon is NOT activated!";

    private ActivationInfoCollector() {
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL("https://mar-staging.katalon.com/api/segment/identify");
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();

        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setUseCaches(false);
        uc.setDoOutput(true);

        return uc;
    }

    private static String collectActivationInfo(String userName, String pass) throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();
        StringBuilder userInfo = new StringBuilder();
        String os = Platform.getOS();
        String katVersion = ApplicationInfo.versionNo() + " build " + ApplicationInfo.buildNo();
        String hostName = inetAddress.getHostName();

        userInfo.append("{ \"userId\":\"")
                .append(userName)
                .append("\", \"traits\": { ")
                .append("\"password\": \"")
                .append(pass)
                .append("\"")
                .append(", \"host_name\": \"")
                .append(hostName)
                .append("\"")
                .append(", \"os\": \"")
                .append(os)
                .append("\"")
                .append(", \"kat_version\": \"")
                .append(katVersion)
                .append("\" } }");

        return userInfo.toString();
    }

    private static String sendActivationInfo(HttpURLConnection uc, String userInfo) throws Exception {
        StringBuilder response = new StringBuilder();
        String line;
        String result = "";

        try (DataOutputStream wr = new DataOutputStream(uc.getOutputStream())) {
            wr.writeBytes(userInfo);
        } catch (IOException ex) {
            LogUtil.logError(ex, SEND_ACTIVATION_INFO_FAILED);
            throw ex;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            result = response.toString().trim();
        } catch (IOException ex) {
            LogUtil.logError(ex, SEND_ACTIVATION_INFO_FAILED);
            throw ex;
        }

        return result;
    }

    private static void markActivated() throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.setAppProperty(ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal, true);
    }

    public static boolean isActivated() {
        String activatedVal = ApplicationInfo.getAppProperty(ACTIVATED_PROP_NAME);
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
        boolean activated = false;
        
        try {
            urlConnection = createConnection();
            String userInfo = collectActivationInfo(userName, pass);
            String result = sendActivationInfo(urlConnection, userInfo);
            if (result.equals(SEND_SUCCESS_RESPONSE)) {
                markActivated();
                activated = true;
            }
            else {
                errorMessage.append(ACTIVATE_INFO_INVALID);
            }
        } catch (Exception ex) {
            errorMessage.delete(0, errorMessage.length());
            if (ex instanceof IOException) {
                errorMessage.append(NETWORK_ERROR);
            }
            else {
                errorMessage.append(ACTIVATE_INFO_INVALID);
            }

            LogUtil.logError(ex, ACTIVATION_COLLECT_FAIL_MESSAGE);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return activated;
    }

    public static boolean checkActivation(RunningModeParam runningMode) {
        if (isActivated()) {
            return true;
        }
        if (runningMode == RunningModeParam.CONSOLE) {
            System.out.println(KATALON_NOT_ACTIVATED);
            return false;
        }
        int result = new ActivationDialog(null).open();
        if (result == Window.CANCEL) {
            return false;
        }
        showFunctionsIntroductionForTheFirstTime();
        return true;
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
}