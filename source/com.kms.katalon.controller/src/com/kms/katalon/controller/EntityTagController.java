package com.kms.katalon.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.util.EntityTagUtil;

public class EntityTagController {
    
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
        return EntityTagUtil.parse(tagValues);
    }
}
