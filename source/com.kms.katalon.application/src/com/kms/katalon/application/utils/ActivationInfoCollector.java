package com.kms.katalon.application.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
import com.kms.katalon.feature.TestOpsFeatureKey;
import com.kms.katalon.license.LicenseService;
import com.kms.katalon.license.models.Feature;
import com.kms.katalon.license.models.License;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class ActivationInfoCollector {

    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    private static boolean activated = false;
    
    private static String apiKey;
    
    private static String sessionId = UUID.randomUUID().toString();

    protected ActivationInfoCollector() {
    }

    public static void setActivated(boolean activated) {
        ActivationInfoCollector.activated = activated;
    }

    public static boolean isActivated() {
        return activated;
    }
    
    public static boolean checkAndMarkActivatedForGUIMode() {
        try {
            String jwsCode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE);
            License license = parseLicense(jwsCode, null);
            
            if (license != null) {
                boolean isOffline = license.getFeatures()
                        .stream().anyMatch(item -> item.getKey().equals(TestOpsFeatureKey.OFFLINE));
                
                if (!isOffline) {
                    String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                    String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    String machineId = MachineUtil.getMachineId();
                    license = activate(email, password, machineId, null);
                }
                
                if (license != null) {
                    enableFeatures(license);
                    markActivatedLicenseCode(license.getJwtCode());
                    activated = true;
                }
            }
        } catch (Exception ex) {
            activated = false;
            LogUtil.logError(ex);
        }
        
        return activated;
    }

    public static boolean checkAndMarkActivatedForConsoleMode(String apiKey) {
        try {
            String machineId = MachineUtil.getMachineId();
            License license = activate(null, apiKey, machineId, null);
            
            if (license != null) {
                enableFeatures(license);
                markActivatedLicenseCode(license.getJwtCode());
                activated = true;
                ActivationInfoCollector.apiKey = apiKey;
            }
        } catch (Exception ex) {
            activated = false;
            LogUtil.logError(ex);
        }
        
        return activated;
    }

    private static boolean isActivatedByApiKey(String apiKey) {
        try {
            String serverUrl = ApplicationInfo.getTestOpsServer();
            if (StringUtils.isEmpty(apiKey)) {
                return false;
            }
            KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, null, apiKey);
            return true;
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
        
        return false;
    }

    private static boolean isActivatedByAccount() {
        String username = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

        StringBuilder errorMessage = new StringBuilder();
        try {
            if (StringUtils.isBlank(username)) {
                return false;
            }

            String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
            String machineId = MachineUtil.getMachineId();
            License license = ActivationInfoCollector.activate(username, password, machineId, errorMessage);
            return license != null;
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
        
        return false;
    }

    private static int getHostNameHashValue() throws Exception {
        String hostName = getHostname();
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
        host = getHostname();

        traits.addProperty("host_name", host);
        traits.addProperty("os", Platform.getOS());
        traits.addProperty("os_type", osType);
        traits.addProperty("kat_version", katVersion);
        traits.addProperty("kat_type", System.getProperty("sun.arch.data.model"));
        traits.addProperty(UsagePropertyConstant.PROPERTY_SESSION_ID, KatalonApplication.SESSION_ID);
        traits.addProperty(UsagePropertyConstant.PROPERTY_USER_KEY, KatalonApplication.USER_KEY);
        return traits;
    }

    private static String getHostname() {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            host = "unknown";
        }
        return host;
    }

    public static License activate(String userName, String password, String machineId, StringBuilder errorMessage) {
        try {
            String userInfo = collectActivationInfo(userName, password);
            ServerAPICommunicationUtil.post("/segment/identify", userInfo);
            if (errorMessage != null) {
                errorMessage.append(ApplicationMessageConstants.ACTIVATE_INFO_INVALID);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        
        
        License license = null;
        try {
            String jwtCode = getLicenseFromTestOps(userName, password, machineId);
            license = parseLicense(jwtCode, errorMessage);
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
            if (errorMessage != null) {
                errorMessage.delete(0, errorMessage.length());
                errorMessage.append(ApplicationMessageConstants.INVALID_ACCOUNT_ERROR);
            }
        }
        return license;
    }
    
    private static License parseLicense(String jwtCode, StringBuilder errorMessage) {
        try {
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = LicenseService.getInstance().parseJws(jwtCode);
                if (isValidLicense(license)) {
                    return license;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATE_INFO_INVALID);
            ex.printStackTrace();
        }
        if (errorMessage != null) {
            errorMessage.append(ApplicationMessageConstants.ACTIVATE_INFO_INVALID);
        }
        return null;
    }
    
    private static String getLicenseFromTestOps(String userName, String password, String machineId) throws Exception {
        String serverUrl = ApplicationInfo.getTestOpsServer();
        String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, userName, password);
        String hostname = getHostname();
        String license = KatalonApplicationActivator.getFeatureActivator().getLicense(serverUrl, token, userName, sessionId,
                hostname, machineId);
        return license;
    }
    
    public static boolean activateOffline(String activationCode, StringBuilder errorMessage) {
        try {
            License license = parseLicense(activationCode, errorMessage);
            if (license != null) {
                markActivatedLicenseCode(activationCode);
                enableFeatures(license);
                activated = true;
                return activated;
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
            if (errorMessage != null) {
                errorMessage.append(ApplicationMessageConstants.ACTIVATION_CODE_INVALID);
            }
        }

        activated = false;
        return activated;
    }
    
    private static boolean isValidLicense(License license) {
        return hasValidMachineId(license) && !isExpired(license);
    }
    
    private static boolean hasValidMachineId(License license) {
        try {
            String machineId = MachineUtil.getMachineId();
            return machineId.equals(license.getMachineId());
        } catch (Exception e) {
            LogUtil.logError(e);
            return false;
        }
    }
    
    private static boolean isExpired(License license) {
        Date currentDate = new Date();
        return currentDate.after(license.getExpirationDate());
    }
    
    private static void enableFeatures(License license) {
        List<Feature> features = license.getFeatures();
        IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
        for (Feature feature : features) {
            featureService.enable(feature.getKey());
        }
    }

    public static void clearFeatures() {
        IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
        featureService.clear();
    }
    
    public static void releaseLicense() throws Exception {
        String jwsCode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE);
        License license = parseLicense(jwsCode, null);
        boolean isOffline = license.getFeatures()
                .stream()
                .anyMatch(item -> item.getKey().equals(TestOpsFeatureKey.OFFLINE));
        if (!isOffline) {
            String serverUrl = ApplicationInfo.getTestOpsServer();
            String machineId = MachineUtil.getMachineId();
            String ksVersion = VersionUtil.getCurrentVersion().getVersion();
            String packageName = KatalonApplication.getKatalonPackage().getPackageName();
            long orgId = license.getOrganizationId();
            String token;
            if (StringUtils.isBlank(apiKey)) {
                String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, email, password);
            } else {
                token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, null, apiKey);
            }
           KatalonApplicationActivator.getFeatureActivator().releaseLicense(
                   serverUrl,
                   machineId,
                   ksVersion,
                   sessionId,
                   packageName,
                   orgId,
                   token
           );
        }
    }

    public static void markActivated(String userName, String password, String organization, License license) throws Exception {
        activated = true;
        enableFeatures(license);
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, userName, true);
        String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, encryptedPassword, true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, organization, true);
        markActivatedLicenseCode(license.getJwtCode());
    }

    private static void markActivatedLicenseCode(String activationCode) throws Exception {
        setActivatedVal();
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
