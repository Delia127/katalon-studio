package com.kms.katalon.integration.analytics.providers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.feature.TestOpsFeatureActivator;
import com.kms.katalon.integration.analytics.entity.AnalyticsFeature;
import com.kms.katalon.integration.analytics.entity.AnalyticsLicenseKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.TestOpsMessage;
import com.kms.katalon.logging.LogUtil;

public class TestOpsFeatureActivatorImpl implements TestOpsFeatureActivator {

    @Override
    public Set<String> getFeatures(String serverUrl, String email, String password, long organizationId, String ksVersion) {
        try {
            AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            List<AnalyticsFeature> features = AnalyticsApiProvider.getFeatures(serverUrl, token.getAccess_token(), organizationId, ksVersion);
            Set<String> featureKeys = features.stream().map(AnalyticsFeature::getKey).collect(Collectors.toSet());
            return featureKeys;
        } catch (Exception ex) {
            LogUtil.logError(ex);
            return new HashSet<String>();
        }
    }

    @Override
    public String connect(String serverUrl, String email, String password) throws Exception {
        try {
            AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            return token.getAccess_token();
        } catch (Exception ex) {
            LogUtil.logError(ex);
            throw ex;
        }
    }

    @Override
    public String getLicense(String serverUrl, String token, String username, String sessionId, String hostname,
            String machineId) throws Exception {
        AnalyticsLicenseKey analyticsLicenseKey = AnalyticsApiProvider.getLicenseKey(serverUrl, username, sessionId,
                hostname, machineId, token);
        if (analyticsLicenseKey.getValue() != null) {
            return analyticsLicenseKey.getValue();
        }
        return "";
    }

    @Override
    public void releaseLicense(String serverUrl, String machineId, String ksVersion, String sessionId,
            Long orgId, String token) throws Exception {
        try {
            AnalyticsApiProvider.releaseLicense(serverUrl, machineId, ksVersion, sessionId, orgId, token);
        } catch (Exception ex) {
            LogUtil.logError(ex);
            throw ex;
        }
    }

    @Override
    public String getOrganization(String serverUrl, String token, long orgId) throws Exception {
        AnalyticsOrganization organization = AnalyticsApiProvider.getOrganization(serverUrl, token, orgId);
        return JsonUtil.toJson(organization);
    }

    @Override
    public String getTestOpsMessage(String message) {
        Gson gson = new GsonBuilder().create();
        TestOpsMessage testOpsMessage = gson.fromJson(message, TestOpsMessage.class);
        
        String reponseMessage = "";
        if (!StringUtils.isEmpty(testOpsMessage.getMessage())) {
            reponseMessage = testOpsMessage.getMessage();
        } else if (!StringUtils.isEmpty(testOpsMessage.getError_description())) {
            reponseMessage = testOpsMessage.getError_description();
        } else {
            reponseMessage = testOpsMessage.getError();
        }
        return reponseMessage;
    }

    @Override
    public void deactivate(String serverUrl, String token, String machineId, Long orgId) throws Exception {
        try {
            AnalyticsApiProvider.deactivate(serverUrl, token, machineId, orgId);
        } catch (Exception ex) {
            LogUtil.logError(ex);
            throw ex;
        }
    }
}
