package com.kms.katalon.application.preference;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ProjectSettingPreference {
    public static final int NUMBER_OF_RECENT_PROJECTS = 6;

    public static final String RECENT_PROJECTS = "project.recentProjects";

    public static final String IS_MIGRATED = "project.isMigratedFromRecentProjects";

    private IPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ProjectSettingPreference.class);
    }

    public boolean isMigratedFromRecentProjects() {
        IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();
        if (!store.contains(IS_MIGRATED)) {
            return false;
        }
        return store.getBoolean(IS_MIGRATED);
    }

    public void setMigratedFromLegacyCode(boolean isMigrated) throws IOException {
        IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();
        store.setValue(IS_MIGRATED, isMigrated);
        store.save();
    }

    private List<String> getRecentProjectLocations() {
        IPreferenceStore store = getPreferenceStore();
        if (!store.contains(RECENT_PROJECTS)) {
            return Collections.emptyList();
        }
        String recentProjectStrings = store.getString(RECENT_PROJECTS);
        if (StringUtils.isEmpty(recentProjectStrings)) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return JsonUtil.fromJson(recentProjectStrings, listType);
    }

    private void setRecentProjectLocations(List<String> locations) throws IOException {
        if (locations == null || locations.isEmpty()) {
            return;
        }
        List<String> recentProjectLocations = new ArrayList<>(locations);
        if (recentProjectLocations.size() > NUMBER_OF_RECENT_PROJECTS) {
            recentProjectLocations = recentProjectLocations.subList(0, NUMBER_OF_RECENT_PROJECTS - 1);
        }
        IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();

        Type listType = new TypeToken<List<String>>() {}.getType();
        String recentProjectJsonString = JsonUtil.toJson(recentProjectLocations, listType, false);
        store.setValue(RECENT_PROJECTS, recentProjectJsonString);
        store.save();
    }

    public void addRecentProject(ProjectEntity project) throws IOException {
        List<String> recentProjectLocations = new ArrayList<>(getRecentProjectLocations());
        String projectId = project.getId();
        if (recentProjectLocations.contains(projectId)) {
            recentProjectLocations.remove(projectId);
        }
        recentProjectLocations.add(0, projectId);
        if (recentProjectLocations.size() > NUMBER_OF_RECENT_PROJECTS) {
            recentProjectLocations = recentProjectLocations.subList(0, NUMBER_OF_RECENT_PROJECTS - 1);
        }

        IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();

        Type listType = new TypeToken<List<String>>() {}.getType();
        String recentProjectJsonString = JsonUtil.toJson(recentProjectLocations, listType, false);
        store.setValue(RECENT_PROJECTS, recentProjectJsonString);
        store.save();
    }

    public void removeRecentProject(ProjectEntity project) throws IOException {
        String projectId = project.getId();
        List<String> recentProjectLocations = new ArrayList<>(getRecentProjectLocations());
        if (recentProjectLocations.size() > NUMBER_OF_RECENT_PROJECTS) {
            recentProjectLocations = recentProjectLocations.subList(0, NUMBER_OF_RECENT_PROJECTS - 1);
        }
        if (recentProjectLocations.contains(projectId)) {
            recentProjectLocations.remove(projectId);
        }
        IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();

        Type listType = new TypeToken<List<String>>() {}.getType();
        String recentProjectJsonString = JsonUtil.toJson(recentProjectLocations, listType, false);
        store.setValue(RECENT_PROJECTS, recentProjectJsonString);
        store.save();
    }

    public List<ProjectEntity> getRecentProjects() throws IOException {
        checkMigrate();
        return ProjectController.getInstance().validateRecentProjectLocations(getRecentProjectLocations());
    }

    private void checkMigrate() throws IOException {
        if (!isMigratedFromRecentProjects()) {
            List<String> recentProjectLocations = new ArrayList<>();
            List<ProjectEntity> projectEntities = ProjectController.getInstance().getRecentProjects();
            for (ProjectEntity recent : projectEntities) {
                recentProjectLocations.add(recent.getLocation());
            }
            setRecentProjectLocations(recentProjectLocations);
            setMigratedFromLegacyCode(true);
        }
    }
}
