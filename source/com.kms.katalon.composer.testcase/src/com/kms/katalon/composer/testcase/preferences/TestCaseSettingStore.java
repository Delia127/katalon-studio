package com.kms.katalon.composer.testcase.preferences;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.EntityTagController;
import com.kms.katalon.core.setting.BundleSettingStore;

public class TestCaseSettingStore extends BundleSettingStore {

    private static final String STORE_ID = "com.kms.katalon.testcase";
    
    private static final String TAG_PROPERTY = "testCaseTag";
    
    public TestCaseSettingStore(String projectDir) {
        super(projectDir, STORE_ID, false);
    }
    
    public Set<String> getTestCaseTags() throws IOException {
        String tagsString = getString(TAG_PROPERTY, StringUtils.EMPTY);
        Set<String> tags = new HashSet<>();
        if (!StringUtils.isBlank(tagsString)) {
            tags = EntityTagController.getInstance().parse(tagsString);
        }
        return tags;
    }
    
    public void saveTestCaseTags(Set<String> tags) throws GeneralSecurityException, IOException {
        String tagsString = EntityTagController.getInstance().joinTags(tags);
        setStringProperty(TAG_PROPERTY, tagsString, false);
    }
}
