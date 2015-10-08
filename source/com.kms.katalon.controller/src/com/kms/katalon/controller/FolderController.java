package com.kms.katalon.controller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@Creatable
public class FolderController extends EntityController implements Serializable {
    private static EntityController _instance;
    private static final long serialVersionUID = 5447512446375093789L;

    private FolderController() {
        super();
    }

    public static FolderController getInstance() {
        if (_instance == null) {
            _instance = new FolderController();
        }
        return (FolderController) _instance;
    }

    public List<FileEntity> getChildren(FolderEntity folder) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getChildren(folder);
    }

    /**
     * Get only children test case of the given folder
     * 
     * @param parentFolder
     *            : test case folder
     * @return Returns list of test case entity
     */
    public List<TestCaseEntity> getTestCaseChildren(FolderEntity parentFolder) throws Exception {
        List<FileEntity> childrenEntities = dataProviderSetting.getFolderDataProvider().getChildren(parentFolder);
        List<TestCaseEntity> childrentTestCases = new ArrayList<TestCaseEntity>();
        for (FileEntity entity : childrenEntities) {
            if (entity instanceof TestCaseEntity) {
                childrentTestCases.add((TestCaseEntity) entity);
            }
        }
        return childrentTestCases;
    }

    public List<Object> getAllDescentdantEntities(FolderEntity folder) throws Exception {
        List<Object> allDescendant = new ArrayList<>();
        for (Object child : getChildren(folder)) {
            if (child instanceof FolderEntity) {
                allDescendant.addAll(getAllDescentdantEntities((FolderEntity) child));
            }
            allDescendant.add(child);
        }
        return allDescendant;
    }

    public List<FolderEntity> getChildFolders(FolderEntity parentFolder) throws Exception {
        List<FileEntity> childrenEntities = dataProviderSetting.getFolderDataProvider().getChildren(parentFolder);
        List<FolderEntity> childrentFolders = new ArrayList<FolderEntity>();
        for (FileEntity entity : childrenEntities) {
            if (entity instanceof FolderEntity) {
                childrentFolders.add((FolderEntity) entity);
            }
        }
        return childrentFolders;
    }

    public FolderEntity getTestSuiteRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getTestSuiteRoot(project);
    }

    public FolderEntity getTestCaseRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getTestCaseRoot(project);
    }

    public FolderEntity getTestDataRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getTestDataRoot(project);
    }

    public FolderEntity getObjectRepositoryRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getObjectRepositoryRoot(project);
    }

    public FolderEntity getKeywordRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getKeywordRoot(project);
    }

    public FolderEntity getReportRoot(ProjectEntity project) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getReportRoot(project);
    }

    public void deleteFolder(FolderEntity folder) throws Exception {
        dataProviderSetting.getFolderDataProvider().deleteFolder(folder);
    }

    public FolderEntity addNewFolder(FolderEntity parentFolder, String folderName) throws Exception {
        if (parentFolder != null) {
            return dataProviderSetting.getFolderDataProvider().addNewFolder(parentFolder, folderName);
        }
        return null;
    }

    public void updateFolderName(FolderEntity folder, String newName) throws Exception {
        dataProviderSetting.getFolderDataProvider().updateFolderName(folder, newName);
    }

    public FolderEntity copyFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        if (targetFolder != null && folder != null && folder.getFolderType() == targetFolder.getFolderType()) {
            return dataProviderSetting.getFolderDataProvider().copyFolder(folder, targetFolder);
        }
        return null;
    }

    public FolderEntity moveFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        if (targetFolder != null && folder != null && folder.getFolderType() == targetFolder.getFolderType()) {
            return dataProviderSetting.getFolderDataProvider().moveFolder(folder, targetFolder);
        }
        return null;
    }

    public FolderEntity getFolder(String folderValue) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getFolder(folderValue);
    }

    public FolderEntity getFolderByDisplayId(ProjectEntity projectEntity, String folderDisplayId) throws Exception {
        if (folderDisplayId == null || folderDisplayId.isEmpty()) return null;
        String folderId = projectEntity.getFolderLocation() + File.separator
                + folderDisplayId.replace("/", File.separator);
        return dataProviderSetting.getFolderDataProvider().getFolder(folderId);
    }

    public List<String> getSibblingFolderNames(FolderEntity folder) throws Exception {
        List<FolderEntity> sibblingFolders = getChildFolders(folder.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (FolderEntity sibblingFolder : sibblingFolders) {
            if (!dataProviderSetting.getEntityPk(sibblingFolder).equals(dataProviderSetting.getEntityPk(folder))) {
                sibblingName.add(sibblingFolder.getName());
            }
        }
        return sibblingName;
    }

    public void loadAllDescentdantEntities(FolderEntity folder) throws Exception {
        List<FileEntity> childrenEntities = getChildren(folder);
        if (childrenEntities != null) {
            folder.setChildrenEntities(childrenEntities);
            for (Object object : childrenEntities) {
                if (object instanceof FolderEntity) {
                    loadAllDescentdantEntities((FolderEntity) object);
                } else if (object instanceof TestCaseEntity) {
                    GroovyUtil.loadScriptContentIntoTestCase((TestCaseEntity) object);
                }
            }
        }
    }

    public boolean isFolderAncestorOfEntity(FolderEntity folder, IEntity entity) throws Exception {
        return entity.getId().contains(folder.getId() + File.separator);
    }

    public void refreshFolder(FolderEntity folder) throws Exception {
        dataProviderSetting.getFolderDataProvider().refreshFolder(folder);
    }

    public String getIdForDisplay(FolderEntity folder) {
        return folder.getRelativePathForUI().replace(File.separator, "/");
    }

    public void saveFolder(FolderEntity folder) throws Exception {
        dataProviderSetting.getFolderDataProvider().saveFolder(folder);
    }

    public void saveParentFolderRecursively(FolderEntity folder) throws Exception {
        dataProviderSetting.getFolderDataProvider().saveFolder(folder);
        if (folder.getParentFolder() != null) {
            saveParentFolderRecursively(folder.getParentFolder());
        }
    }

    public String getAvailableFolderName(FolderEntity parentFolder, String name) throws Exception {
        return dataProviderSetting.getFolderDataProvider().getAvailableFolderName(parentFolder, name);
    }

}
