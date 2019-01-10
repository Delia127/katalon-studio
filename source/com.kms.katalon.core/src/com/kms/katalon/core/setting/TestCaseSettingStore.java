package com.kms.katalon.core.setting;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class TestCaseSettingStore extends BundleSettingStore {

    private static final String STORE_ID = "com.kms.katalon.testcase";
    
    private static final String TAG_PROPERTY = "testCaseTag";
    
    private static final char TAGS_DELIMITER = ',';
    
    private static final String HAS_COLLECTED_TAGS_FROM_ALL_TEST_CASES = "hasCollectedTagsFromAllTestCases";
    
    public TestCaseSettingStore(String projectDir) {
        super(projectDir, STORE_ID, false);
    }
    
    public Set<String> getTestCaseTags() throws IOException {
        String tagsString = getString(TAG_PROPERTY, StringUtils.EMPTY);
        Set<String> tags = new HashSet<>();
        if (!StringUtils.isBlank(tagsString)) {
            tags = parseTags(tagsString);
        }
        return tags;
    }
    
    public void saveTestCaseTags(Set<String> tags) throws GeneralSecurityException, IOException {
        String tagsString = joinTags(tags);
        setStringProperty(TAG_PROPERTY, tagsString, false);
    }
    
    private Set<String> parseTags(String tagsString) {
        if (StringUtils.isBlank(tagsString)) {
            return new HashSet<>();
        }
        Set<String> tags = new LinkedHashSet<>();
        String[] arrTags = StringUtils.split(tagsString, TAGS_DELIMITER);
        if (arrTags != null) {
            for (String tag : arrTags) {
                tags.add(tag);
            }
        }
        return tags;
    }
    
    private String joinTags(Set<String> tags) {
        if (tags == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(tags, TAGS_DELIMITER);
    }

    public void markCollectTagsFromAllTestCases() throws IOException {
        setProperty(HAS_COLLECTED_TAGS_FROM_ALL_TEST_CASES, true);
    }
    
    public boolean hasCollectedTagsFromAllTestCases() throws IOException {
        return getBoolean(HAS_COLLECTED_TAGS_FROM_ALL_TEST_CASES, false);
    }
}
