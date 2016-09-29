package com.kms.katalon.composer.testcase.util;

import org.apache.commons.codec.net.URLCodec;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.keyword.BuiltinKeywords;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords;
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

public class KeywordURLUtil {
    private static final String WEB_PLATFORM = "[WebUI] ";

    private static final String MOBILE_PLATFORM = "[Mobile] ";

    private static final String WEB_SERVICE_PLATFORM = "[WS] ";
    
    private static final String BUILTIN_PLATFORM = "";
    
    private static final String KEYWORD_DESC_PATH = "http://docs.katalon.com/display/KD/";

    private static final String WEB_UI_CLASSNAME = WebUiBuiltInKeywords.class.getSimpleName();

    private static final String MOBILE_CLASSNAME = MobileBuiltInKeywords.class.getSimpleName();

    private static final String WEB_SERVICE_CLASSNAME = WSBuiltInKeywords.class.getSimpleName();

    private static final String BUILTIN_CLASSNAME = BuiltinKeywords.class.getSimpleName();
    
    private static final String VERIFY_CHECKPOINT_KEYWORD = "verifyCheckPoint";

    public static String getKeywordDescriptionURI(String key) {
        try {
            String platform = getKeywordPlatform(key);
            if (platform == null) {
                return null;
            }

            return KEYWORD_DESC_PATH + new URLCodec().encode(platform + getKeywordName(key));
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }
    
    public static String getKeywordDescriptionURI(String keywordClass, String keyword) {
        try {
            String platform = getKeywordPlatform(keywordClass);
            if (keyword.equals(VERIFY_CHECKPOINT_KEYWORD)) {
                platform = BUILTIN_PLATFORM;
            }
            return KEYWORD_DESC_PATH + new URLCodec().encode(platform + getKeywordName(keyword).trim());
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }

    private static String getKeywordPlatform(String key) {
        String[] parts = key.split("\\s*;\\s*");
        String className = key;
        
        if (parts.length > 1) {
            className = parts[0].substring(parts[0].lastIndexOf('/') + 1);
        }

        if (className.equals(WEB_UI_CLASSNAME)) {
            return WEB_PLATFORM;
        }
        if (className.equals(MOBILE_CLASSNAME)) {
            return MOBILE_PLATFORM;
        }
        if (className.equals(WEB_SERVICE_CLASSNAME)) {
            return WEB_SERVICE_PLATFORM;
        }
        if (className.equals(BUILTIN_CLASSNAME)) {
            return BUILTIN_PLATFORM;
        }

        return null;
    }

    private static String getKeywordName(String key) {
        String[] parts = key.split("\\s*;\\s*");
        StringBuilder keyword;
        int i = 0;
        if (parts.length > 1) {
            String keywordName = parts[1];
            i = keywordName.indexOf('(');
            keyword = new StringBuilder(i > 0 ? keywordName.substring(1, i) : keywordName.substring(1));
        } else {
            if (key.contains(" ")) {
                key = key.trim().replaceAll("\\s+", "");
            }
            keyword = new StringBuilder(key);
        }
        if (keyword.toString().equals("uncheck")) {
            return "Un-check";
        }
        for (i = 0; i < keyword.length(); ++i) {
            if (Character.isUpperCase(keyword.charAt(i))) {
                keyword.insert(i++, " ");
                if (i < keyword.length() - 1 && keyword.substring(i, i + 2).equals("OS")) {
                    i++;
                } else if (i < keyword.length() - 2 && keyword.substring(i, i + 3).equals("IOS")) {
                    i += 2;
                }
            }
        }

        return Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
    }
}
