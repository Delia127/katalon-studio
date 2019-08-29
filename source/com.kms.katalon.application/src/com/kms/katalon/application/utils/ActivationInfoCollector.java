package com.kms.katalon.application.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.google.gson.JsonObject;
import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.constants.UsagePropertyConstant;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class ActivationInfoCollector {

    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    protected ActivationInfoCollector() {
    }

    public static boolean isActivated() {
        String activatedVal = ApplicationInfo.getAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME);
        if (activatedVal == null) {
            return false;
        }
        try {
            String updatedVersion = ApplicationInfo
                    .getAppProperty(ApplicationStringConstants.UPDATED_VERSION_PROP_NAME);
            if (ApplicationInfo.versionNo().equals(getVersionNo(updatedVersion))) {
                setActivatedVal();
                return true;
            }

            String[] activateParts = activatedVal.split("_");
            String oldVersion = new StringBuilder(activateParts[0]).reverse().toString();
            String curVersion = ApplicationInfo.versionNo().replaceAll("\\.", "");
            if (oldVersion.equals(curVersion) == false) {
                return false;
            }

            int activatedHashVal = Integer.parseInt(activateParts[1]);
            boolean isActivated = activatedHashVal == getHostNameHashValue();
            
            if (isActivated) {
                if (KatalonApplicationActivator.getFeatureActivator() != null) {
                    String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                    String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    String orgId = ApplicationInfo.getAppProperty(ApplicationStringConstants.KA_ORGANIZATION);
                    String serverUrl = ApplicationInfo.getTestOpsServer();
                    String ksVersion = VersionUtil.getCurrentVersion().getVersion();
                    activateFeatures(serverUrl, email, password, Long.valueOf(orgId), ksVersion);
                }
            }
            return isActivated;
        } catch (Exception ex) {
            LogUtil.logError(ex);
            return false;
        }
    }
    
    public static void activateFeatures(String serverUrl, String email, String password, long orgId, String ksVersion) {
        Set<String> featureKeys = KatalonApplicationActivator.getFeatureActivator().getFeatures(serverUrl, email, password, 
                Long.valueOf(orgId), ksVersion);
        IFeatureService instance = FeatureServiceConsumer.getServiceInstance();
        for (String featureKey : featureKeys) {
            instance.enable(featureKey);
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

    private static String collectActivationInfo(String userName, String pass) {
        JsonObject traits = traitsWithAppInfo();
        traits.addProperty("password", pass);

        JsonObject activationObject = new JsonObject();
        activationObject.addProperty("userId", userName);
        activationObject.add("traits", traits);

        return activationObject.toString();
    }

    public static JsonObject traitsWithAppInfo() {
        JsonObject traits = new JsonObject();
        String katVersion = ApplicationInfo.versionNo() + " build " + ApplicationInfo.buildNo();
        String osType = Platform.getOSArch().contains("64") ? "64" : "32";
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            host = "unknown";
        }

        traits.addProperty("host_name", host);
        traits.addProperty("os", Platform.getOS());
        traits.addProperty("os_type", osType);
        traits.addProperty("kat_version", katVersion);
        traits.addProperty("kat_type", System.getProperty("sun.arch.data.model"));
        traits.addProperty(UsagePropertyConstant.PROPERTY_SESSION_ID, KatalonApplication.SESSION_ID);
        traits.addProperty(UsagePropertyConstant.PROPERTY_USER_KEY, KatalonApplication.USER_KEY);
        return traits;
    }

    public static boolean activate(String userName, String pass, StringBuilder errorMessage) {
        boolean activatedResult = false;
        try {
            String userInfo = collectActivationInfo(userName, pass);
            String result = ServerAPICommunicationUtil.post("/segment/identify", userInfo);
            if (result.equals(ApplicationMessageConstants.SEND_SUCCESS_RESPONSE)) {
                activatedResult = true;
            } else if (errorMessage != null) {
                errorMessage.append(ApplicationMessageConstants.ACTIVATE_INFO_INVALID);
            }

        } catch (IOException ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
            if (errorMessage != null) {
                errorMessage.delete(0, errorMessage.length());
                errorMessage.append(ApplicationMessageConstants.NETWORK_ERROR);
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
                errorMessage.append(ApplicationMessageConstants.ACTIVATION_CODE_INVALID);
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
            if (errorMessage != null) {
                errorMessage.append(ApplicationMessageConstants.ACTIVATION_CODE_INVALID);
            }
        }

        return false;
    }

    public static void markActivated(String userName, String password) throws Exception {
        setActivatedVal();
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, userName, true);
        String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, encryptedPassword, true);
    }
    
    private static void markActivated(String activationCode) throws Exception {
        setActivatedVal();
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE, activationCode, true);
    }

    private static void setActivatedVal() throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal,
                true);
    }

    public static void markActivatedViaUpgradation(String versionNumber) {
        ApplicationInfo.setAppProperty(ApplicationStringConstants.UPDATED_VERSION_PROP_NAME, 
                getVersionNo(versionNumber), true);
    }
    
    private static String getVersionNo(String versionNumber) {
        if (versionNumber == null) {
            return versionNumber;
        }
        String[] numbers = versionNumber.split("\\.");
        while (numbers.length < 3) {
            numbers = ArrayUtils.add(numbers, "0");
        }
        return StringUtils.join(ArrayUtils.subarray(numbers, 0, 3), ".");
    }
}
