package com.kms.katalon.application.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;

public class UserProfileHelper {

    public static final String USER_PROFILES_FILE_LOCATION = ApplicationStringConstants.USER_PROFILES_FILE_LOCATION;

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
        return profiles.getOrDefault(email, null);
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

        userProfiles = loadUserProfiles();
        if (userProfiles == null) {
            userProfiles = new HashMap<String, UserProfile>();
        }

        return userProfiles;
    }

    private static Map<String, UserProfile> loadUserProfiles() {
        try {
            File userProfilesFile = new File(USER_PROFILES_FILE_LOCATION);
            if (!userProfilesFile.exists()) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader(USER_PROFILES_FILE_LOCATION));
            String jsonUserProfiles = reader.readLine();
            reader.close();
            
            if (StringUtils.isBlank(jsonUserProfiles)) {
                return null;
            }

            Type userProfileMapType = new TypeToken<Map<String, UserProfile>>() {}.getType();
            return JsonUtil.fromJson(jsonUserProfiles, userProfileMapType);
        } catch (IOException error) {
            LogUtil.logError(error);
        }
        return null;
    }

    public static void saveUserProfiles() {
        Map<String, UserProfile> profiles = getUserProfiles();
        if (profiles == null) {
            return;
        }
        try {
            String jsonUserProfiles = JsonUtil.toJson(profiles, false);
            BufferedWriter writer = new BufferedWriter(new FileWriter(USER_PROFILES_FILE_LOCATION));
            writer.write(jsonUserProfiles);
            writer.close();
        } catch (IOException error) {
            LogUtil.logError(error);
        }
    }
}
