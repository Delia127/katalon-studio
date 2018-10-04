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

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.util.internal.JsonUtil;
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
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = JsonUtil.fromJson(requestHistoryMap,
                mapType);
        return requestHistoriesPerProject;
    }

    public void addRequestHistory(RequestHistoryEntity requestHistory, ProjectEntity project) throws IOException {
        List<RequestHistoryEntity> currentRequestHistories = new ArrayList<>(getHistoryRequestEntities(project));
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = new HashMap<>(getRequestHistoriesIndice());
        RequestHistoryEntity currentHistoryEntity = currentRequestHistories.stream()
                .filter(req -> req.getRequest().equals(requestHistory.getRequest()))
        .findFirst().orElse(null);
        // Remove current history to add new instance
        if (currentHistoryEntity != null) {
            currentRequestHistories.remove(currentHistoryEntity);
        }

        currentRequestHistories.add(requestHistory);
        requestHistoriesPerProject.put(project.getUUID(), currentRequestHistories);
        getStore().setValue("requestHistories", JsonUtil.toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void setRequestHistoryEntities(List<RequestHistoryEntity> requestHistoryEntities, ProjectEntity project)
            throws IOException {
        Map<String, List<RequestHistoryEntity>> requestHistoriesPerProject = new HashMap<>(getRequestHistoriesIndice());
        requestHistoriesPerProject.put(project.getUUID(), requestHistoryEntities);
        getStore().setValue("requestHistories", JsonUtil.toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void saveDraftRequest(DraftWebServiceRequestEntity requestHistory, ProjectEntity project)
            throws IOException {
        List<DraftWebServiceRequestEntity> currentRequestHistories = new ArrayList<>(getDraftRequests(project));
        Map<String, List<DraftWebServiceRequestEntity>> requestHistoriesPerProject = new HashMap<>(
                getDraftRequestIndice());
        currentRequestHistories.add(requestHistory);
        requestHistoriesPerProject.put(project.getUUID(), currentRequestHistories);
        getStore().setValue("draftRequests", JsonUtil.toJson(requestHistoriesPerProject));
        getStore().save();
    }

    public void removeDraftRequest(DraftWebServiceRequestEntity requestHistory, ProjectEntity project)
            throws IOException {
        List<DraftWebServiceRequestEntity> currentDraftRequests = new ArrayList<>(getDraftRequests(project));
        Map<String, List<DraftWebServiceRequestEntity>> draftRequestPerProjects = new HashMap<>(
                getDraftRequestIndice());
        currentDraftRequests.remove(requestHistory);
        draftRequestPerProjects.put(project.getUUID(), currentDraftRequests);
        getStore().setValue("draftRequests", JsonUtil.toJson(draftRequestPerProjects));
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
        Map<String, List<DraftWebServiceRequestEntity>> requestHistoriesPerProject = JsonUtil.fromJson(draftRequestsMap,
                mapType);
        return requestHistoriesPerProject;
    }
}
