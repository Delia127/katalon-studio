package com.kms.katalon.plugin.util;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreProduct;

public class KStoreUrls {

    private static final String STORE_URL_PROPERTY_KEY = "storeUrl";

    private static final String DEVELOPMENT_URL = "https://store-staging.katalon.com";

    private static final String PRODUCTION_URL = "https://store.katalon.com";

    public static String getSearchPluginPageUrl(String token) {
        return getKatalonStoreUrl() + "?token=" + token;
    }

    public static String getManagePluginPageUrl(String token) {
        return getKatalonStoreUrl() + "/manage/products?token=" + token;
    }

    public static String getManageApiKeysPageUrl(String token) {
        return getKatalonStoreUrl() + "/settings?token=" + token;
    }

    public static String getProductReviewPageUrl(KStoreProduct product, String token) {
        return getProductPageUrl(product, token) + "#rating-content";
    }

    public static String getProductPricingPageUrl(KStoreProduct product, String token) {
        return getProductPageUrl(product, token) + "#pricing-content";
    }

    public static String getProductPageUrl(KStoreProduct product, String token) {
        return getKatalonStoreUrl() + product.getUrl() + "?token=" + token;
    }

    public static String getAuthenticateAPIUrl() {
        return getKatalonStoreAPIUrl() + "/authenticate";
    }

    public static String getPluginsAPIUrl(String appVersion) {
        return getKatalonStoreAPIUrl() + "/products/ks?appVersion=" + appVersion;
    }

    public static String getPluginDownloadAPIUrl(KStorePlugin plugin) {
        return plugin.getDownloadUrl();
    }

    public static String getRecommendedPluginsAPIUrl() {
    	return getKatalonStoreAPIUrl() + "/products/ks/recommended";
    }
    
    public static String getInstallRecommendedPluginsAPIUrl() {
    	return getKatalonStoreAPIUrl() + "/products/ks/recommended-plugins/install";
    }
    public static String getKatalonStoreAPIUrl() {
        return getKatalonStoreUrl() + "/api";
    }

    public static String getKatalonStoreUrl() {
        String storeUrlArgument = getStoreUrlArgument();
        if (!StringUtils.isBlank(storeUrlArgument)) {
            return storeUrlArgument;
        } else if (VersionUtil.isStagingBuild() || VersionUtil.isDevelopmentBuild()) {
            return DEVELOPMENT_URL;
        } else {
            return PRODUCTION_URL;
        }
    }

    private static String getStoreUrlArgument() {
        return System.getProperty(STORE_URL_PROPERTY_KEY);
    }
}
