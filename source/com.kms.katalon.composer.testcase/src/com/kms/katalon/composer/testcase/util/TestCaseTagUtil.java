package com.kms.katalon.composer.testcase.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTagUtil {

    private static final char TAG_SEPARATOR = ',';

    public static Set<String> collectTagsFromAllTestCases(ProjectEntity project) throws Exception {
        Set<String> allTagsInProject = new HashSet<>();
        FolderController folderController = FolderController.getInstance();
        FolderEntity testCaseRoot = folderController.getTestCaseRoot(project);
        List<Object> testCaseDescendants = folderController.getAllDescentdantEntities(testCaseRoot);
        for (Object entity : testCaseDescendants) {
            if (entity instanceof TestCaseEntity) {
                TestCaseEntity testCase = (TestCaseEntity) entity;
                Set<String> testCaseTags = collectTestCaseTags(testCase);
                allTagsInProject.addAll(testCaseTags);
            }
        }
        return allTagsInProject;
    }

    public static Set<String> collectTestCaseTags(TestCaseEntity testCase) {
        String tagString = testCase.getTag();
        return splitTags(tagString);
    }

    public static String joinTags(Set<String> tags) {
        String tagString = StringUtils.join(tags, TAG_SEPARATOR);
        return tagString;
    }

    public static Set<String> splitTags(String tagString) {
        String[] tagArray = StringUtils.split(tagString, TAG_SEPARATOR);
        Set<String> tagSet = new HashSet<>();
        for (String tag : tagArray) {
            if (!StringUtils.isBlank(tag)) {
                tagSet.add(tag.trim());
            }
        }
        return tagSet;
    }
    
    public static char getTagSeparator() {
        return TAG_SEPARATOR;
    }
}
