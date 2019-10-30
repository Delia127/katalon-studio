package com.kms.katalon.application.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.google.gson.JsonObject;
import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.constants.UsagePropertyConstant;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.TestOpsFeatureKey;
import com.kms.katalon.license.LicenseService;
import com.kms.katalon.license.models.Feature;
import com.kms.katalon.license.models.License;
import com.kms.katalon.license.models.LicenseResource;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class ActivationInfoCollector {

    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    public static final String DEFAULT_REASON = ApplicationMessageConstants.LICENSE_INVALID;

    private static boolean activated = false;

    private static ScheduledFuture<?> checkLicenseTask;

    private static String apiKey;
    
    private static LicenseType licenseType;
    
    private static String activationCode;

    protected ActivationInfoCollector() {
    }

    public static void setActivated(boolean activated) {
        ActivationInfoCollector.activated = activated;
    }

    public static boolean isActivated() {
        return activated;
    }

    public static boolean checkAndMarkActivatedForGUIMode() {
        return checkAndMarkActivatedForGUIMode(new StringBuilder());
    }

    public static boolean checkAndMarkActivatedForGUIMode(StringBuilder errorMessage) {
        activated = false;
        try {
            License license = getValidLicense();
            boolean isOffline = isOffline(license);

            if (!isOffline) {
                String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

                if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(encryptedPassword)) {
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    String machineId = MachineUtil.getMachineId();
                    LicenseResource licenseResource = activate(email, password, machineId, errorMessage);

                    if (licenseResource != null) {
                        license = licenseResource.getLicense();
                        String message = licenseResource.getMessage();
                        if (!StringUtils.isEmpty(message)) {
                            LogUtil.logError(message);
                        }
                    }
                }
            }

            if (license != null) {
                enableFeatures(license);
                markActivatedLicenseCode(license.getJwtCode());
                saveLicenseType(license.getType());
                saveExpirationDate(license.getExpirationDate());
                activated = true;
            }
        } catch (Exception ex) {
            activated = false;
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_GUI_FAIL);
        }

        return activated;
    }

    private static void saveLicenseType(LicenseType type) {
//        ApplicationInfo.setAppProperty(ApplicationStringConstants.LICENSE_TYPE, type.toString(), true);
        licenseType = type;
    }

    private static void saveExpirationDate(Date date) {
        Format formatter = new SimpleDateFormat("MMMMM dd, yyyy HH:mm");
        String dateWithFormatter = formatter.format(date);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.EXPIRATION_DATE, dateWithFormatter, true);
    }

    public static boolean checkAndMarkActivatedForConsoleMode(String apiKey, StringBuilder errorMessage) {
        activated = false;
        try {
            String machineId = MachineUtil.getMachineId();
            LicenseResource licenseResource = activate(null, apiKey, machineId, errorMessage);

            if (licenseResource != null) {
                License license = licenseResource.getLicense();
                String message = licenseResource.getMessage();

                if (!StringUtils.isEmpty(message)) {
                    LogUtil.logError(message);
                }
                if (license != null) {
                    enableFeatures(license);
//                  markActivatedLicenseCode(license.getJwtCode());
                    activationCode = license.getJwtCode();
                    saveLicenseType(license.getType());
                    activated = true;
                    ActivationInfoCollector.apiKey = apiKey;
                }
             }
        } catch (Exception ex) {
            activated = false;
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_CLI_FAIL);
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
            LicenseResource licenseResource = ActivationInfoCollector.activate(username, password, machineId, errorMessage);
            
            if (licenseResource != null) {
                License license = licenseResource.getLicense();
                String message = licenseResource.getMessage();
                if (!StringUtils.isEmpty(message)) {
                    LogUtil.logError(message);
                }
                return license != null;
            }

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
        traits.addProperty(UsagePropertyConstant.PROPERTY_SESSION_ID, KatalonApplication.USER_SESSION_ID);
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
    
    public static void deactivate(String userName, String password, String machineId) throws Exception {
        String jwsCode = getActivationCode();
        Long orgId = null;
        if (StringUtils.isNotBlank(jwsCode)) {
            License license = parseLicense(jwsCode);
            if (license == null) {
                license = getLastUsedLicense();
            }
            orgId = license.getOrganizationId();
        }
        String serverUrl = ApplicationInfo.getTestOpsServer();
        String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, userName, password);
        KatalonApplicationActivator.getFeatureActivator().deactivate(serverUrl, token, machineId, orgId);
    }

    public static LicenseResource activate(String serverUrl, String userName, String password, String machineId,
            StringBuilder errorMessage) {
        ApplicationInfo.setTestOpsServer(serverUrl);
        return activate(userName, password, machineId, errorMessage);
    }

    public static LicenseResource activate(String userName, String password, String machineId, StringBuilder errorMessage) {
//        try {
//            String userInfo = collectActivationInfo(userName, password);
//            ServerAPICommunicationUtil.post("/segment/identify", userInfo);
//            if (errorMessage != null) {
//                errorMessage.append(ApplicationMessageConstants.ACTIVATE_INFO_INVALID);
//            }
//        } catch (Exception e) {
//            LogUtil.logError(e);
//        }
        License license;
        if (!StringUtils.isBlank(password) && !StringUtils.isBlank(machineId)) {
            try {
                Map<String, String> respond = getLicenseFromTestOps(userName, password, machineId);
                String jwtCode = respond.get("license");
                String message = respond.get("errorMessage");
                license = parseLicense(jwtCode);

                LicenseResource licenseResource = new LicenseResource();
                licenseResource.setLicense(license);
                licenseResource.setMessage(message);

                return licenseResource;
            } catch (Exception ex) {
                LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
                try {
                    String message = KatalonApplicationActivator.getFeatureActivator().getTestOpsMessage(ex.getMessage());
                    errorMessage.append(message);
                } catch (Exception error) {
                    //No message from server
                    errorMessage.delete(0, errorMessage.length());
                    errorMessage.append(ApplicationMessageConstants.ACTIVATION_ONLINE_INVALID);
                }
            }
        }

        return null;
    }

    private static License parseLicense(String jwtCode) throws Exception {
        try {
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = LicenseService.getInstance().parseJws(jwtCode);
                if (isValidLicense(license)) {
                    return license;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.KSE_ACTIVATE_INFOR_INVALID);
            throw ex;
        }
        return null;
    }

    private static Map<String, String> getLicenseFromTestOps(String userName, String password, String machineId) throws Exception {
        String serverUrl = ApplicationInfo.getTestOpsServer();
        String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, userName, password);
        String hostname = getHostname();
        Map<String, String> licenseInfor = KatalonApplicationActivator.getFeatureActivator().getLicense(serverUrl, token, userName,
                KatalonApplication.USER_SESSION_ID, hostname, machineId);
        return licenseInfor;
    }
    
    public static String getOrganization(String userName, String password, long orgId) throws Exception {
        String serverUrl = ApplicationInfo.getTestOpsServer();
        String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, userName, password);
        String org = KatalonApplicationActivator.getFeatureActivator().getOrganization(serverUrl, token, orgId);
        return org;
    }

    public static boolean activateOffline(String activationCode, StringBuilder errorMessage, RunningMode runningMode) {
        try {
            License license = parseLicense(activationCode);
            if (license != null) {
                if (isOffline(license)) {
                    enableFeatures(license);
                    
                    saveLicenseType(license.getType());
                    if (runningMode == RunningMode.GUI) {
                        markActivatedLicenseCode(activationCode);
                        saveExpirationDate(license.getExpirationDate());
        
                        Organization org = new Organization();
                        org.setId(license.getOrganizationId());
                        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, JsonUtil.toJson(org), true);
                    } else {
                        ActivationInfoCollector.activationCode = activationCode;
                    }
    
                    activated = true;
                    return activated;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.ACTIVATION_OFFLINE_FAIL);
        }
        errorMessage.append(ApplicationMessageConstants.KSE_ACTIVATE_INFOR_INVALID);
        activated = false;
        return activated;
    }
    
    public static boolean activateOfflineForEngine(StringBuilder errorMessage) throws Exception {
        try {
            Set<String> validActivationCodes = findValidEngineOfflineLinceseCodes();
            int validOfflineLicenseSessionNumber = validActivationCodes.size();
            int runningSession =  ProcessUtil.countKatalonRunningSession();

            LogUtil.logInfo("The number of valid offline licenses: " + validActivationCodes.size());
            LogUtil.logInfo("The number of Runtime Engine running sessions: " + runningSession);
            if (validOfflineLicenseSessionNumber < runningSession) {
                errorMessage.append("License quota exceeded");
                return false;
            }
            
            String activationCode = validActivationCodes.stream().findFirst().get();
            return activateOffline(activationCode, errorMessage, RunningMode.CONSOLE);
        } catch (Exception e) {
            LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_OFFLINE_FAIL);
            return false;
        }
    }

    private static boolean isValidLicense(License license) {
        boolean isValidMachineId = hasValidMachineId(license);
        boolean isExpired = isExpired(license);
        if (isValidMachineId && !isExpired) {
            RunningMode runMode = ApplicationRunningMode.get();
            if (runMode == RunningMode.CONSOLE && license.isEngineLicense()) { 
                return true;
            }
            if (runMode == RunningMode.GUI && license.isKSELicense()) { 
                return true;
            }
            LogUtil.logError(DEFAULT_REASON);
        } else {
            if (!isValidMachineId) {
                LogUtil.logError(ApplicationMessageConstants.LICENSE_INVALID_MACHINE_ID);
            }

            if (isExpired) {
                LogUtil.logError(ApplicationMessageConstants.LICENSE_EXPIRED);
            }
        }
        return false;
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

    public static boolean isReachRenewTime(License license) {
        Date currentDate = new Date();
        Date renewTime = license.getRenewTime();
        if (renewTime != null) {
            return currentDate.after(renewTime);
        }
        return false;
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
        try {
            String jwsCode = getActivationCode();
            if (StringUtils.isNotBlank(jwsCode)) {
                License license = parseLicense(jwsCode);
                if (license == null) {
                    license = getLastUsedLicense();
                }
                boolean isOffline = isOffline(license);
                if (!isOffline) {
                    String serverUrl = ApplicationInfo.getTestOpsServer();
                    String machineId = MachineUtil.getMachineId();
                    String ksVersion = VersionUtil.getCurrentVersion().getVersion();
                    Long orgId = license.getOrganizationId();
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
                            KatalonApplication.USER_SESSION_ID,
                            orgId,
                            token
                            );
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            KatalonApplication.refreshUserSession();
        }
    }

    public static void markActivated(String userName, String password, String organization, License license)
            throws Exception {
        activated = true;
        enableFeatures(license);
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, userName, true);
        String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, encryptedPassword, true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, organization, true);
        markActivatedLicenseCode(license.getJwtCode());
        saveLicenseType(license.getType());
        saveExpirationDate(license.getExpirationDate());
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

    public static void scheduleCheckLicense(Runnable expiredHandler, Runnable renewHandler) {
        checkLicenseTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                License license = getValidLicense();
                if (license == null) {
                    license = getLastUsedLicense();
                }
                boolean isOffline = isOffline(license);

                if (!isOffline) {
                    if (license != null && isReachRenewTime(license)) {
                        try {
                            renewHandler.run();
                            license = getValidLicense();
                        } catch (Exception e) {
                            LogUtil.logError(e, ApplicationMessageConstants.LICENSE_UNABLE_RENEW);
                        }
                    }
                }

                if (license == null || !isValidLicense(license)) {
                    expiredHandler.run();
                }
            } catch (Exception e) {
                LogUtil.logError(e, ApplicationMessageConstants.LICENSE_ERROR_RENEW);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    public static void postEndSession() {
        if (checkLicenseTask != null) {
            checkLicenseTask.cancel(true);
        }
    }

    public static boolean isOffline(License license) {
        boolean isOffline = false;
        if (license != null) {
            isOffline = license.getFeatures()
                    .stream()
                    .map(Feature::getKey)
                    .anyMatch(TestOpsFeatureKey.OFFLINE::equals);
        }
        return isOffline;
    }

    private static License getValidLicense() throws Exception {
        String jwsCode = getActivationCode();
        License license = ActivationInfoCollector.parseLicense(jwsCode);
        return license;
    }
    
    public static LicenseType getLicenseType() {
        return licenseType;
    }

    private static License getLastUsedLicense() {
        try {
            String jwtCode = getActivationCode();
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = LicenseService.getInstance().parseJws(jwtCode);
                if (hasValidMachineId(license)) {
                    return license;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.KSE_ACTIVATE_INFOR_INVALID);
        }
        return null;
    }
    
    private static String getActivationCode() {
        KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
        if (katalonPackage == KatalonPackage.ENGINE) {
            return activationCode;
        } else {
            return ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE);
        }
    }
    
    public static Set<String> findValidEngineOfflineLinceseCodes() {
        Set<String> validActivationCodes = new HashSet<>();
        try {
            File licenseFolder = new File(ApplicationInfo.userDirLocation(), "license");
            LogUtil.logInfo("Finding valid offline licenses in folder: " + licenseFolder.getAbsolutePath());
            if (licenseFolder.exists() && licenseFolder.isDirectory()) {
                Files.walk(Paths.get(licenseFolder.getAbsolutePath()))
                        .filter(p -> Files.isRegularFile(p)
                                && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals("lic"))
                        .forEach(p -> {
                            try {
                                File licenseFile = p.toFile();
                                String activationCode = FileUtils.readFileToString(licenseFile);
                                License license = parseLicense(activationCode);
                                if (license != null && license.isEngineLicense() && isOffline(license)) {
                                    validActivationCodes.add(activationCode);
                                }
                            } catch (Exception e) {
                                LogUtil.logError(e);
                            }
                        });
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return validActivationCodes;
    }
}
