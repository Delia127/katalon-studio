package com.kms.katalon.composer.integration.jira.preference;

import java.net.URI;
import java.util.Arrays;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.kms.katalon.integration.jira.setting.StoredJiraObject;

public class DisplayedComboboxObject<T extends NamedEntity & AddressableEntity> implements PreferredObjectStrategy {

    private StoredJiraObject<T> storedObject;

    private String[] names;

    public DisplayedComboboxObject(StoredJiraObject<T> storedObject) {
        this.storedObject = storedObject;
        buildNameArray();
    }

    public StoredJiraObject<T> getStoredObject() {
        return storedObject;
    }

    public String[] getNames() {
        if (names == null) {
            return new String[0];
        }
        return names;
    }

    private void buildNameArray() {
        T[] jiraObjects = storedObject.getJiraObjects();
        if (jiraObjects == null) {
            return;
        }
        int arrayLength = jiraObjects.length;
        names = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            names[i] = jiraObjects[i].getName();
        }
    }

    public int getDefaultObjectIndex() {
        T defaultJiraProject = storedObject.getDefaultJiraProject();
        if (defaultJiraProject != null) {
            return Arrays.asList(storedObject.getJiraObjects()).indexOf(defaultJiraProject);
        }
        if (storedObject.getJiraObjects() == null) {
            return -1;
        }
        int preferredIndex = getPreferredIndex();
        setDefaultObjectIndex(preferredIndex);
        return preferredIndex;
    }

    public void setDefaultObjectIndex(int index) {
        setDefaultObjectURI(storedObject.getJiraObjects()[index].getSelf());
    }

    private void setDefaultObjectURI(URI uri) {
        storedObject.setDefaultProjectURI(uri);
    }

    public DisplayedComboboxObject<T> updateDefaultURIFrom(DisplayedComboboxObject<T> that) {
        setDefaultObjectURI(that.getStoredObject().getDefaultProjectURI());
        return this;
    }

    @Override
    public int getPreferredIndex() {
        return 0;
    }
}
