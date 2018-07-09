package com.kms.katalon.composer.integration.jira.preference;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.google.gson.reflect.TypeToken;
import static com.kms.katalon.composer.integration.jira.constant.PreferenceConstants.PREF_LAST_EDITED_JQL;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class JiraPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        getStore().setDefault(PREF_LAST_EDITED_JQL,
                JsonUtil.toJson(Collections.emptyMap(), getTokenTypeForMap(), false));
    }

    private static Type getTokenTypeForMap() {
        return new TypeToken<Map<String, String>>() {}.getType();
    }

    public static ScopedPreferenceStore getStore() {
        return getPreferenceStore(JiraPreferenceInitializer.class);
    }

    public static String getLastEditedJQL(ProjectEntity project) {
        if (project == null) {
            return StringUtils.EMPTY;
        }
        Map<String, String> jqlPerProjects = getJqlPerProjects();
        String key = project.getFolderLocation();
        return jqlPerProjects.containsKey(key) ? jqlPerProjects.get(key) : StringUtils.EMPTY;
    }

    private static Map<String, String> getJqlPerProjects() {
        return JsonUtil.fromJson(getStore().getString(PREF_LAST_EDITED_JQL), getTokenTypeForMap());
    }

    public static void saveLastEditedJQL(String jql, ProjectEntity project) throws IOException {
        if (jql == null || project == null) {
            return;
        }
        Map<String, String> jqlPerProjects = getJqlPerProjects();
        jqlPerProjects.put(project.getFolderLocation(), jql);
        ScopedPreferenceStore store = getStore();
        store.setValue(PREF_LAST_EDITED_JQL, JsonUtil.toJson(jqlPerProjects, getTokenTypeForMap(), false));
        store.save();
    }
}
