package com.kms.katalon.composer.webservice.settings;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WebServicePreferenceStore {
    private static ScopedPreferenceStore getStore() {
        return getPreferenceStore(WebServicePreferenceStore.class);
    }

    public List<RequestHistoryEntity> getHistoryRequestEntities(ProjectEntity project) {
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = getRequestHistoriesIndice();
        if (requestHistoriesPerProject.containsKey(project.getUUID())) {
            return requestHistoriesPerProject.get(project.getUUID());
        }
        return Collections.emptyList();
    }

    private Map<String, List<RequestHistoryEntity>> getRequestHistoriesIndice() {
        String requestHistoryMap = getStore().getString("requestHistories");
        if (StringUtils.isEmpty(requestHistoryMap)) {
            return Collections.emptyMap();
        }
        Type mapType = new TypeToken<Map<String, List<RequestHistoryEntity>>>() {}.getType();
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = getGson().fromJson(requestHistoryMap,
                mapType);
        return requestHistoriesPerProject;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes arg0) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> arg0) {
                if (arg0.getCanonicalName().equals("com.kms.katalon.entity.repository.WebElementXpathEntity")) {
                    return true;
                }
                return false;
            }
        });
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public void addRequestHistory(RequestHistoryEntity requestHistory, ProjectEntity project) throws IOException {
        List<RequestHistoryEntity> currentRequestHistories = new ArrayList<>(getHistoryRequestEntities(project));
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = new HashMap<>(getRequestHistoriesIndice());

        currentRequestHistories.add(requestHistory);
        requestHistoriesPerProject.put(project.getUUID(), currentRequestHistories);

        getStore().setValue("requestHistories", getGson().toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void removeRequestHistory(RequestHistoryEntity requestHistory, ProjectEntity project) throws IOException {
        List<RequestHistoryEntity> currentRequestHistories = new ArrayList<>(getHistoryRequestEntities(project));
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = new HashMap<>(getRequestHistoriesIndice());

        currentRequestHistories.remove(requestHistory);
        requestHistoriesPerProject.put(project.getUUID(), currentRequestHistories);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes arg0) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> arg0) {
                if (arg0.getCanonicalName().equals("com.kms.katalon.entity.repository.WebElementXpathEntity")) {
                    return true;
                }
                return false;
            }
        });
        Gson gson = gsonBuilder.create();
        getStore().setValue("requestHistories", gson.toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void setRequestHistoryEntities(List<RequestHistoryEntity> requestHistoryEntities, ProjectEntity project)
            throws IOException {
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = new HashMap<>(getRequestHistoriesIndice());
        requestHistoriesPerProject.put(project.getUUID(), requestHistoryEntities);
        getStore().setValue("requestHistories", getGson().toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void saveDraftRequest(DraftWebServiceRequestEntity requestHistory, ProjectEntity project)
            throws IOException {
        List<DraftWebServiceRequestEntity> currentRequestHistories = new ArrayList<>(getDraftRequests(project));
        Map<String, List<DraftWebServiceRequestEntity>> requestHistoriesPerProject = new HashMap<>(
                getDraftRequestIndice());
        currentRequestHistories.add(requestHistory);
        requestHistoriesPerProject.put(project.getUUID(), currentRequestHistories);
        getStore().setValue("draftRequests", getGson().toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void removeDraftRequest(DraftWebServiceRequestEntity requestHistory, ProjectEntity project)
            throws IOException {
        List<DraftWebServiceRequestEntity> currentDraftRequests = new ArrayList<>(getDraftRequests(project));
        Map<String, List<DraftWebServiceRequestEntity>> draftRequestPerProjects = new HashMap<>(
                getDraftRequestIndice());
        currentDraftRequests.remove(requestHistory);
        draftRequestPerProjects.put(project.getUUID(), currentDraftRequests);
        getStore().setValue("draftRequests", getGson().toJson(draftRequestPerProjects));
        getStore().save();
    }

    public List<DraftWebServiceRequestEntity> getDraftRequests(ProjectEntity project) {
        Map<String, List<DraftWebServiceRequestEntity>> draftRequestsPerProject = getDraftRequestIndice();
        if (draftRequestsPerProject.containsKey(project.getUUID())) {
            return draftRequestsPerProject.get(project.getUUID());
        }
        return Collections.emptyList();
    }

    private Map<String, List<DraftWebServiceRequestEntity>> getDraftRequestIndice() {
        String draftRequestsMap = getStore().getString("draftRequests");
        if (StringUtils.isEmpty(draftRequestsMap)) {
            return Collections.emptyMap();
        }
        Type mapType = new TypeToken<Map<String, List<DraftWebServiceRequestEntity>>>() {}.getType();
        Map<String, List<DraftWebServiceRequestEntity>> requestHistoriesPerProject = getGson().fromJson(draftRequestsMap,
                mapType);
        return requestHistoriesPerProject;
    }
}
