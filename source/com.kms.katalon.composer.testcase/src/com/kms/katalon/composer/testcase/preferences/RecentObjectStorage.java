package com.kms.katalon.composer.testcase.preferences;

import java.util.LinkedList;
import java.util.List;

import com.kms.katalon.entity.repository.WebElementEntity;

public class RecentObjectStorage {
    private static final int MAX_SIZE = 5;

    private List<String> recentObjectIds;

    private List<String> recentFolderIds;

    public List<String> getRecentObjectIds() {
        if (recentObjectIds == null) {
            recentObjectIds = new LinkedList<>();
        }
        return recentObjectIds;
    }

    public List<String> getRecentFolderIds() {
        if (recentFolderIds == null) {
            recentFolderIds = new LinkedList<>();
        }
        return recentFolderIds;
    }

    public void addRecentTestObject(WebElementEntity testObject) {
        addFirst(getRecentObjectIds(), testObject.getIdForDisplay(), MAX_SIZE);
        if (isNotUnderRoot(testObject)) {
            addFirst(getRecentFolderIds(), testObject.getParentFolder().getIdForDisplay(), MAX_SIZE);
        }
    }

    private void addFirst(List<String> list, String s, int maxSize) {
        if (!list.contains(s)) {
            list.remove(s);
        }
        list.add(0, s);
        list = getNewSize(list, maxSize);
    }

    private List<String> getNewSize(List<String> list, int maxSize) {
        while (list.size() > maxSize) {
            list.remove(list.size() - 1);
        }
        return list;
    }

    private boolean isNotUnderRoot(WebElementEntity testObject) {
        return testObject.getParentFolder().getParentFolder() != null;
    }

    public boolean isEmpty() {
        return getRecentObjectIds().isEmpty() && getRecentFolderIds().isEmpty();
    }
}
