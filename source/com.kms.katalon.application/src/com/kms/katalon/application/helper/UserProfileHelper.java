package com.kms.katalon.application.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;

public class UserProfileHelper {

    public static final String USER_PROFILES_FILE_LOCATION = ApplicationStringConstants.USER_PROFILES_FILE_LOCATION;

    private static Map<String, UserProfile> userProfiles;

    public static final String USER_PROFILES_KEY = "userProfiles";

    public static UserProfile getCurrentProfile() {
        String currentUserEmail = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        if (StringUtils.isBlank(currentUserEmail)) {
            currentUserEmail = MachineUtil.getMachineId();
        }
        return getOrCreateProfile(currentUserEmail);
    }

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
        userProfile.setEnableKURecorderHint(true);
        return userProfile;
    }

    public static UserProfile getProfile(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        Map<String, UserProfile> profiles = getUserProfiles();
        return profiles.getOrDefault(email, null);
    }

    public static void saveProfile(UserProfile userProfile) {
        if (userProfile == null || StringUtils.isBlank(userProfile.getEmail())) {
            return;
        }
        Map<String, UserProfile> profiles = getUserProfiles();
        profiles.put(userProfile.getEmail(), userProfile);
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

            String jsonUserProfiles = FileUtils.readFileToString(userProfilesFile);
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
            FileUtils.writeStringToFile(new File(USER_PROFILES_FILE_LOCATION), jsonUserProfiles);
        } catch (IOException error) {
            LogUtil.logError(error);
        }
    }
}
