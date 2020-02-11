package com.kms.katalon.application.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Link;

import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.JsonObject;
import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.preference.ProxyPreferences;
import com.kms.katalon.constants.UsagePropertyConstant;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.TestOpsFeatureKey;
import com.kms.katalon.license.LicenseService;
import com.kms.katalon.license.models.AwsKatalonAmi;
import com.kms.katalon.license.models.Feature;
import com.kms.katalon.license.models.License;
import com.kms.katalon.license.models.LicenseResource;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class ActivationInfoCollector {

    private static final String URL_KATALON_AMI_ID = "https://katalon-ami.s3.amazonaws.com/ami-id.json";
    
    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";

    public static final String DEFAULT_REASON = ApplicationMessageConstants.LICENSE_INVALID;

    private static final String DEFAULT_LICENSE_FOLDER = "license";

    private static final String DEFAULT_LICENSE_EXTENSION = "lic";
    
    private static final String ENV_KATALON_AMI = "KATALON_AMI";
    
    private static final String MACHINE_ID_KATALON_AMI = "katalon-ami";
    
    private static final String DEFAULT_KATALON_AMI = "true";

    private static boolean activated = false;

    private static ScheduledFuture<?> checkLicenseTask;

    private static String apiKey;

    private static LicenseType licenseType;

    private static boolean isLicenseOffline;

    private static Organization organization;
    
    private static String activationCode;

    private static String expirationDate;
    
    private static String publicKey;
    
    private static String amiLicense;

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
            License license = null;
            publicKey = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PUBLIC_KEY);

            if (isRunOnAmiMachine()) {
                if (ActivationInfoCollector.getAndCheckAmiMachine()) {
                    license = getValidLicense();
                    isLicenseOffline = true; //by default AMI LICENSE is offline license
                } else {
                    activated = false;
                    return activated;
                }
            } else {
                Organization org = ApplicationInfo.getOrganization();
                if (org.getId() == null) {
                    activated = false;
                    return activated;
                }
                license = getValidLicense();
                boolean isOffline = isOffline(license);
                isLicenseOffline = isOffline;
                if (!isOffline) {
                    String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                    String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

                    if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(encryptedPassword)) {
                        String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                        String machineId = MachineUtil.getMachineId();
                        LicenseResource licenseResource = activate(email, password, machineId, errorMessage);

                        if (licenseResource != null) {
                            license = licenseResource.getLicense();
                            if (license != null) {
                                Long orgId = license.getOrganizationId();
                                if (orgId != null && !orgId.equals(org.getId())) {
                                    String organization = getOrganization(email, password, orgId);
                                    saveOrganization(organization);
                                }
                            }
                            String message = licenseResource.getMessage();
                            if (!StringUtils.isEmpty(message)) {
                                LogUtil.logError(message);
                            }
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

    public static boolean isLicenseOffline() {
        return isLicenseOffline;
    }

    private static void saveExpirationDate(Date date) {
        Format formatter = new SimpleDateFormat("MMMMM dd, yyyy HH:mm");
        expirationDate = formatter.format(date);
        // ApplicationInfo.setAppProperty(ApplicationStringConstants.EXPIRATION_DATE, dateWithFormatter, true);
    }

    public static String getExpirationDate() {
        return expirationDate;
    }

    private static void saveOrganization(String org) {
        organization = JsonUtil.fromJson(org, Organization.class);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, org, true);
    }

    private static void saveOrganization(Organization org) {
        organization = org;
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, JsonUtil.toJson(org), true);
    }
    
    private static void savePublicKey(String publicKey) {
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PUBLIC_KEY, publicKey, true);
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

    private static String collectActivationInfo(String userName, String machineId, boolean isActivateSuccess, StringBuilder errorMessage) {
        JsonObject traits = traitsWithAppInfo();
        traits.addProperty("activated", isActivateSuccess);
        traits.addProperty("machineId", machineId);

        JsonObject activationObject = new JsonObject();

        KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
        activationObject.addProperty("katalonPackage", katalonPackage.getPackageName());
        traits.addProperty("katPackage", katalonPackage.getPackageName());

        if (isActivateSuccess) {
            LicenseType licenseType = ActivationInfoCollector.getLicenseType();
            activationObject.addProperty("licenseType", licenseType.name());
            traits.addProperty("license", licenseType.name());

            Organization organization = ActivationInfoCollector.getOrganzation();
            traits.addProperty("orgId", organization.getId());
        }
        activationObject.addProperty("errorMessage", errorMessage.toString());

        activationObject.addProperty("userId", userName);
        activationObject.add("traits", traits);

        return activationObject.toString();
    }

    public static JsonObject traitsWithAppInfo() {
        JsonObject traits = new JsonObject();
        String katVersion = ApplicationInfo.versionNo() + " build " + VersionUtil.getCurrentVersion().getBuildNumber();
        String osType = Platform.getOSArch().contains("64") ? "64" : "32";
        String host = "";
        host = getHostname();

        traits.addProperty("hostName", host);
        traits.addProperty("os", Platform.getOS());
        traits.addProperty("osType", osType);
        traits.addProperty("katVersion", katVersion);
        traits.addProperty("katType", System.getProperty("sun.arch.data.model"));
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

    public static void sendTrackingForActivate(String userName, String machineId, boolean isActivatedSuccess, StringBuilder errorMessage) {
        Thread sendTracking = new Thread(() -> {
            try {
                String userInfo = collectActivationInfo(userName, machineId, isActivatedSuccess, errorMessage);
                ServerAPICommunicationUtil.post("/activation-tracking", userInfo);
            } catch (IOException | GeneralSecurityException e) {
                //ignore
            }
        });
        sendTracking.start();
    }

    public static LicenseResource activate(String userName, String password, String machineId, StringBuilder errorMessage) {
        License license;
        ActivationInfoCollector.publicKey = null;
        if (!StringUtils.isBlank(password) && !StringUtils.isBlank(machineId)) {
            try {
                Map<String, String> respond = getLicenseFromTestOps(userName, password, machineId);
                String jwtCode = respond.get("license");
                String message = respond.get("errorMessage");
                String publicKey = respond.get("publicKey");
                if (StringUtils.isNotBlank(publicKey)) {
                    ActivationInfoCollector.publicKey = publicKey;
                    if (KatalonApplication.getKatalonPackage() == KatalonPackage.KSE) {
                        savePublicKey(publicKey);
                    }
                }
                    
                license = parseLicense(jwtCode);

                LicenseResource licenseResource = new LicenseResource();
                licenseResource.setLicense(license);
                licenseResource.setMessage(message);
                isLicenseOffline = false;
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

    public static boolean checkTestingLicense(String activationCode, StringBuilder errorMessage) throws Exception {
        License testingLicense = parseTestingLicense(activationCode);
        if (testingLicense != null) {
            if (testingLicense.isTesting()) {
                boolean isValidMachineId = hasValidMachineId(testingLicense);
                if (isValidMachineId) {
                    RunningMode runMode = ApplicationRunningMode.get();
                    if (runMode == RunningMode.CONSOLE && testingLicense.isEngineLicense()) { 
                        errorMessage.append(ApplicationMessageConstants.TESTING_LICENSE_MACHINE_ID_CORRECT);
                        return true;
                    }
                    if (runMode == RunningMode.GUI && testingLicense.isKSELicense()) { 
                        errorMessage.append(ApplicationMessageConstants.TESTING_LICENSE_MACHINE_ID_CORRECT);
                        return true;
                    }
                } else {
                    errorMessage.append(ApplicationMessageConstants.TESTING_LICENSE_MACHINE_ID_INCORRECT);
                    return true;
                }
                errorMessage.append(ApplicationMessageConstants.LICENSE_INVALID);
                return true;
            }
        }
        return false;
    }

    private static License parseTestingLicense(String jwtCode) throws Exception {
        try {
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = LicenseService.getInstance().parseJws(jwtCode);
                if (license.isTesting()) {
                    return license;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.KSE_ACTIVATE_INFOR_INVALID);
            throw ex;
        }
        return null;
    }

    private static License parseLicense(String jwtCode, String licenseFileName) throws Exception {
        try {
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = LicenseService.getInstance().parseJws(jwtCode);
                if (isValidLicense(license, licenseFileName)) {
                    return license;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex, ApplicationMessageConstants.KSE_ACTIVATE_INFOR_INVALID);
            throw ex;
        }
        return null;
    }
    
    private static License parseLicense(String jwtCode) throws Exception {
        try {
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = null;
                if (StringUtils.isNotBlank(publicKey)) {
                    license = LicenseService.getInstance().parseJws(jwtCode, publicKey);
                } else {
                    license = LicenseService.getInstance().parseJws(jwtCode);
                }
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
            ActivationInfoCollector.publicKey = null;
            License license = parseLicense(activationCode);
            if (license != null) {
                boolean isOffline = isOffline(license);
                isLicenseOffline = isOffline;
                if (isOffline) {
                    enableFeatures(license);
                    
                    saveLicenseType(license.getType());
                    if (runningMode == RunningMode.GUI) {
                        markActivatedLicenseCode(activationCode);
                        saveExpirationDate(license.getExpirationDate());

                        Organization org = new Organization();
                        org.setId(license.getOrganizationId());
                        saveOrganization(org);
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
            LogUtil.logInfo("The number of valid offline licenses: " + validActivationCodes.size());
            
            int validOfflineLicenseSessionNumber = validActivationCodes.size();
            
            if (validOfflineLicenseSessionNumber == 0) {
                return false;
            }
            
            int runningSession =  ProcessUtil.countKatalonRunningSession();
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
    
    public static boolean activateOfflineForEngineAmiMachine(StringBuilder errorMessage) {
        if (isRunOnAmiMachine()) {
            if (getAndCheckAmiMachine()) {
                return activateOffline(amiLicense, errorMessage, RunningMode.CONSOLE);
            }
        }
        return false;
    }
    
    private static boolean isValidLicense(License license, String licenseFileName) {
        boolean isValidMachineId = hasValidMachineId(license);
        boolean isExpired = isExpired(license);

        RunningMode runMode = ApplicationRunningMode.get();

        if (license.isTesting()) {
            if (isValidMachineId) {
                if (runMode == RunningMode.CONSOLE && license.isEngineLicense()) {
                    LogUtil.logInfo(MessageFormat.format(ApplicationMessageConstants.TESTING_LICENSE_WITH_FILENAME_CORRECT, licenseFileName));
                } else  if (runMode == RunningMode.GUI && license.isKSELicense()) {
                    LogUtil.logInfo(MessageFormat.format(ApplicationMessageConstants.TESTING_LICENSE_WITH_FILENAME_CORRECT, licenseFileName));
                }
            } else {
                LogUtil.logError(MessageFormat.format(ApplicationMessageConstants.TESTING_LICENSE_WITH_FILENAME_INCORRECT, licenseFileName));
            }
        } else {
            if (isValidMachineId && !isExpired && !license.isTesting()) {
                if (runMode == RunningMode.CONSOLE && license.isEngineLicense()) { 
                    return true;
                }
                if (runMode == RunningMode.GUI && license.isKSELicense()) { 
                    return true;
                }
                if (runMode == RunningMode.CONSOLE) {
                    LogUtil.logError(MessageFormat.format(ApplicationMessageConstants.LICENSE_INVALID_KSE_USE_TO_KRE, licenseFileName));
                } else if (runMode == RunningMode.GUI) {
                    LogUtil.logError(MessageFormat.format(ApplicationMessageConstants.LICENSE_INVALID_KRE_USE_TO_KSE, licenseFileName));
                } else {
                    LogUtil.logError(DEFAULT_REASON);
                }
            } else {
                if (!isValidMachineId) {
                    LogUtil.logError(MessageFormat.format(ApplicationMessageConstants.LICENSE_INCORRECT_MACHINE_ID, licenseFileName));
                }

                if (isExpired) {
                    LogUtil.logError(MessageFormat.format(ApplicationMessageConstants.LICENSE_EXPIRED_WITH_FILE_NAME, licenseFileName));
                }
            }
        }
        return false;
    }

    private static boolean isValidLicense(License license) {
        boolean isValidMachineId = false;
        if (isValidAmiMachineId(license)) {
            isValidMachineId = true;
        } else {
            isValidMachineId = hasValidMachineId(license);
        }
        boolean isExpired = isExpired(license);

        if (isValidMachineId && !isExpired && !license.isTesting()) {
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

    private static boolean isValidAmiMachineId(License license) {
        return MACHINE_ID_KATALON_AMI.contains(license.getMachineId());
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
            LogUtil.logInfo("Start release license task");
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
                    LogUtil.logInfo("License released");
                }
            }
            LogUtil.logInfo("End release license task");
        } catch (Exception ex) {
            LogUtil.printAndLogError(ex, "Error when release license");
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

        Organization org = JsonUtil.fromJson(organization, Organization.class);
        saveOrganization(org);

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
        LogUtil.logInfo("Start check license task");
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
                            LogUtil.printAndLogError(e, ApplicationMessageConstants.LICENSE_UNABLE_RENEW);
                        }
                    }
                }

                if (license == null || !isValidLicense(license)) {
                    expiredHandler.run();
                }

            } catch (Exception e) {
                LogUtil.printAndLogError(e, ApplicationMessageConstants.LICENSE_ERROR_RENEW);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    public static void postEndSession() {
        LogUtil.logInfo("Start clean up session");
        if (checkLicenseTask != null) {
            checkLicenseTask.cancel(true);
            LogUtil.logInfo("End check license task");
        }
        LogUtil.logInfo("End clean up session");
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
        if (isRunOnAmiMachine()) {
            License license = LicenseService.getInstance().parseJws(amiLicense);
            if (MACHINE_ID_KATALON_AMI.contains(license.getMachineId())) {
                return license;
            } else {
                return null;
            }
        }
        String jwsCode = getActivationCode();
        License license = ActivationInfoCollector.parseLicense(jwsCode);
        return license;
    }
    
    public static LicenseType getLicenseType() {
        return licenseType;
    }

    public static Organization getOrganzation() {
        return organization;
    }

    private static License getLastUsedLicense() {
        try {
            String jwtCode = getActivationCode();
            if (jwtCode != null && !jwtCode.isEmpty()) {
                License license = null;
                if (StringUtils.isNotBlank(publicKey)) {
                    license = LicenseService.getInstance().parseJws(jwtCode, publicKey);
                } else {
                    license = LicenseService.getInstance().parseJws(jwtCode);
                }
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
            File licenseFolder = new File(ApplicationInfo.userDirLocation(), DEFAULT_LICENSE_FOLDER);
            LogUtil.logInfo(MessageFormat.format(ApplicationMessageConstants.RE_FIND_VAILD_OFFLINE_LICENSE_IN_FOLDER, licenseFolder.getAbsolutePath()));
            if (licenseFolder.exists() && licenseFolder.isDirectory()) {
                Files.walk(Paths.get(licenseFolder.getAbsolutePath()))
                        .filter(p -> Files.isRegularFile(p)
                                && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals(DEFAULT_LICENSE_EXTENSION))
                        .forEach(p -> {
                            try {
                                File licenseFile = p.toFile();
                                LogUtil.logInfo(MessageFormat.format(ApplicationMessageConstants.RE_START_CHECK_LICENSE, licenseFile.getName()));
                                String activationCode = FileUtils.readFileToString(licenseFile);
                                License license = parseLicense(activationCode, licenseFile.getName());
                                if (license != null && license.isEngineLicense() && isOffline(license)) {
                                    LogUtil.logInfo(MessageFormat.format(ApplicationMessageConstants.RE_LICENSE_FILE_VAILD, licenseFile.getName()));
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

    public static boolean isRunOnAmiMachine() {
        try {
            String hasKatalonAmi = System.getenv(ENV_KATALON_AMI);
            return DEFAULT_KATALON_AMI.equalsIgnoreCase(hasKatalonAmi);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return false;
    }

    public static boolean getAndCheckAmiMachine() {
        try {
            String amiID = EC2MetadataUtils.getAmiId();
            if (StringUtils.isEmpty(amiID)) {
                return false;
            }

            String userName = System.getProperty("user.name");
            String machineIdOfAmi = amiID + "-" + userName;

            if (StringUtils.isNotEmpty(machineIdOfAmi)) {
                URL url = new URL(URL_KATALON_AMI_ID);
                InputStream is = null;
                is = url.openConnection(ProxyUtil.getProxy(ProxyPreferences.getProxyInformation())).getInputStream();
                String responseBody = IOUtils.toString(is);
                AwsKatalonAmi awsKatalonAmi = JsonUtil.fromJson(responseBody, AwsKatalonAmi.class);

                if (awsKatalonAmi.getAmiIds().contains(machineIdOfAmi)) {
                    RunningMode runMode = ApplicationRunningMode.get();
                    if (runMode == RunningMode.GUI) {
                        amiLicense = awsKatalonAmi.getKseLicense();
                    } else {
                        amiLicense = awsKatalonAmi.getReLicense();
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return false;
    }
    
    public static String getAmiLicense() {
        return amiLicense;
    }
}
