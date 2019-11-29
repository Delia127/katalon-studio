package com.kms.katalon.integration.kobiton.preferences;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.integration.kobiton.constants.KobitonPreferenceConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonApiKey;
import com.kms.katalon.integration.kobiton.entity.KobitonLoginInfo;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class KobitonPreferencesProvider {
    private static final String KOBITON_PLUGIN_ID = "com.katalon.katalon-studio-kobiton";
    
    public static ScopedPreferenceStore getPreferencetStore() {
        return PreferenceStoreManager.getPreferenceStore(KobitonPreferenceConstants.KOBITON_QUALIFIER);
    }

   /*
    * Return true if Kobiton plugin is installed <b>and</b> Kobiton integration is enabled
    */
    public static boolean isKobitonIntegrationAvailable() {
        return isKobitonIntegrationEnabled() && isKobitonPluginInstalled();
    }
    
    public static boolean isKobitonIntegrationEnabled() {
        return getPreferencetStore().getBoolean(KobitonPreferenceConstants.KOBITON_INTEGRATION_ENABLE);
    }
    
    public static boolean isKobitonPluginInstalled() {
        Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(KOBITON_PLUGIN_ID);
        return plugin != null;
    }

    public static String getKobitonUserName() {
        return getPreferencetStore().getString(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_USERNAME);
    }
    
    public static void saveKobitonUserName(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return;
        }
        getPreferencetStore().setValue(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_USERNAME, userName);
    }

    public static String getKobitonPassword() {
        return getPreferencetStore().getString(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_PASSWORD);
    }
    
    public static void saveKobitonPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return;
        }
        getPreferencetStore().setValue(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_PASSWORD, password);
    }
    
    public static String getKobitonToken() {
        String kobitonToken = getPreferencetStore().getString(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN);
        try {
            // Console mode: we just passed username and password. So, we need to generate token automatically.
            if (StringUtils.isEmpty(kobitonToken)) {
                KobitonLoginInfo loginInfo;

                loginInfo = KobitonApiProvider.login(getKobitonUserName(), getKobitonPassword());
                kobitonToken = loginInfo.getToken();
                KobitonPreferencesProvider.saveKobitonToken(loginInfo.getToken());
                List<KobitonApiKey> apiKeys = KobitonApiProvider.getApiKeyList(loginInfo.getToken());
                if (!apiKeys.isEmpty()) {
                    KobitonPreferencesProvider.saveKobitonApiKey(apiKeys.get(0).getKey());
                }

            } else {
                List<KobitonApiKey> apiKeys = KobitonApiProvider.getApiKeyList(kobitonToken);
                if (!apiKeys.isEmpty()) {
                    KobitonPreferencesProvider.saveKobitonApiKey(apiKeys.get(0).getKey());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Authentication kobiton system failed !", e);
        }
        return kobitonToken;
    }
    
    public static void saveKobitonToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        getPreferencetStore().setValue(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN, token);
    }

    public static String getKobitonApiKey() {
        return getPreferencetStore().getString(KobitonPreferenceConstants.KOBITON_API_KEY);
    }
    
    public static void saveKobitonApiKey(String apiKey) {
        if (StringUtils.isEmpty(apiKey)) {
            return;
        }
        getPreferencetStore().setValue(KobitonPreferenceConstants.KOBITON_API_KEY, apiKey);
    }
}
