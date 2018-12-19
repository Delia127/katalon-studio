package com.kms.katalon.platform.internal.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.katalon.platform.api.model.Folder;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.IntegrationType;
import com.katalon.platform.api.model.Project;
import com.katalon.platform.api.model.TestCase;
import com.katalon.platform.api.service.FolderManager;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class FolderManagerImpl implements FolderManager {
    
    private ProjectController projectController = ProjectController.getInstance();
    
    private FolderController folderController = FolderController.getInstance();

    @Override
    public Folder getTestCaseRoot(Project project) throws Exception {
        String projectFileLocation = project.getFileLocation();
        ProjectEntity projectEntity = projectController.getProject(projectFileLocation); 
        FolderEntity folderEntity = folderController.getTestCaseRoot(projectEntity);
        return new Folder() {

            @Override
            public String getId() {
                return folderEntity.getId();
            }

            @Override
            public String getName() {
                return folderEntity.getName();
            }

            @Override
            public String getFolderLocation() {
                return folderEntity.getLocation();
            }

            @Override
            public String getFileLocation() {
                return folderEntity.getLocation();
            }
        };
    }

    @Override
    public List<TestCase> getChildTestCases(Folder folder) throws Exception {
        String folderLocation = folder.getFolderLocation();
        FolderEntity folderEntity = folderController.getFolder(folderLocation);
        List<TestCaseEntity> childTestCaseEntities = folderController.getTestCaseChildren(folderEntity);
        List<TestCase> childTestCases = childTestCaseEntities.stream()
                .map(testCaseEntity -> {
                    TestCase testCase = new TestCase() {
                        @Override
                        public String getId() {
                            return testCaseEntity.getId();
                        }

                        @Override
                        public String getName() {
                            return testCaseEntity.getName();
                        }

                        @Override
                        public String getFolderLocation() {
                            return testCaseEntity.getParentFolder().getLocation();
                        }

                        @Override
                        public String getFileLocation() {
                            return testCaseEntity.getLocation();
                        }

                        @Override
                        public String getDescription() {
                            return testCaseEntity.getDescription();
                        }

                        @Override
                        public String getComment() {
                            return testCaseEntity.getComment();
                        }

                        @Override
                        public InputStream getScriptContent() {
                            return null;
                        }

                        @Override
                        public List<Integration> getIntegrations() {
                            List<IntegratedEntity> integratedEntities = testCaseEntity.getIntegratedEntities();
                            if (integratedEntities != null) {
                                List<Integration> integrations = integratedEntities.stream()
                                    .map(integratedEntity -> {
                                        Integration integration = new Integration() {

                                            @Override
                                            public String getProductName() {
                                                return integratedEntity.getProductName();
                                            }

                                            @Override
                                            public Map<String, String> getProperties() {
                                                return integratedEntity.getProperties();
                                            }

                                            @Override
                                            public IntegrationType getType() {
                                                return IntegrationType.valueOf(integratedEntity.getType().toString());
                                            }
                                            
                                        };
                                        return integration;
                                    })
                                    .collect(Collectors.toList());
                                return integrations;
                            } else {
                                return new ArrayList<>();
                            }
                        }
                    };
                    return testCase;
                })
            .collect(Collectors.toList());
        return childTestCases;
    }
}
