package com.kms.katalon.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class EntityTagController {

    private static final char TAG_SEPARATOR = ',';
    
    private static EntityTagController instance;
    
    public static EntityTagController getInstance() {
        if (instance == null) {
            instance = new EntityTagController();
        }
        return instance;
    }

    public Set<String> collectTagsFromAllTestCases(ProjectEntity project) throws Exception {
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

    public Set<String> collectTestCaseTags(TestCaseEntity testCase) {
        String tagValues = testCase.getTag();
        return parse(tagValues);
    }

    public String joinTags(Set<String> tags) {
        String tagValues = StringUtils.join(tags, TAG_SEPARATOR);
        return tagValues;
    }

    public Set<String> parse(String tagValues) {
        Set<String> parseResult = new HashSet<>();
        String[] tagArray = StringUtils.split(tagValues, TAG_SEPARATOR);
        if (tagArray != null) {
            for (String tag : tagArray) {
                if (!StringUtils.isBlank(tag)) {
                    parseResult.add(tag.trim());
                }
            }
        }
        return parseResult;
    }
    
    public char getTagSeparator() {
        return TAG_SEPARATOR;
    }
}
