package com.kms.katalon.application.helper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.application.utils.LocalStorage;

public class UserProfileHelper {

    private static Map<String, UserProfile> userProfiles;

    public static final String USER_PROFILES_KEY = "userProfiles";

    public static UserProfile getOrCreateProfile(String email) {
        UserProfile userProfile = getProfile(email);
        if (userProfile == null) {
            return createProfile(email);
        }
        return userProfile;
    }

    public static UserProfile createProfile(String email) {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(email);
        userProfile.setDoneFirstTimeUseSurvey(false);
        return userProfile;
    }

    public static UserProfile getProfile(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        Map<String, UserProfile> profiles = getUserProfiles();
        return profiles.get(email);
    }

    public static void saveProfile(UserProfile newUserProfile) {
        if (newUserProfile == null || StringUtils.isBlank(newUserProfile.getEmail())) {
            return;
        }
        Map<String, UserProfile> profiles = getUserProfiles();
        profiles.put(newUserProfile.getEmail(), newUserProfile);
        saveUserProfiles();
    }

    public static Map<String, UserProfile> getUserProfiles() {
        if (userProfiles != null) {
            return userProfiles;
        }

        Type userProfileMapType = new TypeToken<Map<String, UserProfile>>() {}.getType();
        userProfiles = LocalStorage.get(USER_PROFILES_KEY, userProfileMapType);

        if (userProfiles == null) {
            userProfiles = new HashMap<String, UserProfile>();
        }

        return userProfiles;
    }

    public static void saveUserProfiles() {
        LocalStorage.set(USER_PROFILES_KEY, userProfiles);
    }
}
