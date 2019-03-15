package com.kms.katalon.objectspy.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;
import com.kms.katalon.entity.repository.WebElementXpathEntity;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.ConflictOptions;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.ConflictWebElementWrapper;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebElement.WebElementType;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.util.WebElementUtils;

public class ObjectRepositoryService {

    public SaveActionResult saveObject(SaveToObjectRepositoryDialogResult dialogResult) throws Exception {
        List<Object[]> testObjectIds = new ArrayList<>();
        Set<ITreeEntity> newSelectionOnExplorer = new HashSet<>();
        for (ConflictWebElementWrapper webPageWrapper : dialogResult.getAllSelectedPages()) {
            if (!(webPageWrapper.getType() == WebElementType.PAGE)) {
                continue;
            }
            WebPage page = (WebPage) webPageWrapper.getOriginalWebElement();
            for (ConflictWebElementWrapper webElementChildWrapper : webPageWrapper.getChildren()) {
                if (webElementChildWrapper.isConflicted()) {
                    addConflictedWebElement(page, (ConflictWebElementWrapper) webElementChildWrapper, dialogResult, testObjectIds);
                } else {
                    newSelectionOnExplorer = addNonConflictedWebElement(page, ((ConflictWebElementWrapper) webElementChildWrapper).getOriginalWebElement(), dialogResult);
                }
            }
        }
        
        int savedObjectCount = dialogResult.getEntitySavedMap().size();
        if (!dialogResult.isCreateFolderAsPageNameAllowed()) {
            savedObjectCount -= dialogResult.getAllSelectedPages().size();
        }
        
        return new SaveActionResult(testObjectIds, newSelectionOnExplorer, savedObjectCount);
    }

    private Set<ITreeEntity> addNonConflictedWebElement(WebPage pageElement, WebElement selectedWebElement, SaveToObjectRepositoryDialogResult dialogResult) throws Exception {
        Set<ITreeEntity> newSelectionOnExplorer = new HashSet<>();
        
        boolean createFolderAsPageNameAllowed = dialogResult.isCreateFolderAsPageNameAllowed();
        FolderTreeEntity selectedParentFolder = dialogResult.getSelectedParentFolder();
        Map<WebElement, FileEntity> entitySavedMap = dialogResult.getEntitySavedMap();

        // Check page folder already exist or not.
        FolderEntity pageFolder = getPageFolder(pageElement, selectedParentFolder);
        FolderTreeEntity webPageDestinationFolder = selectedParentFolder;
        if (createFolderAsPageNameAllowed) {
            webPageDestinationFolder = ((pageFolder == null))
                    ? createTreeFolderForPageElement(pageElement, selectedParentFolder, createFolderAsPageNameAllowed)
                    : new FolderTreeEntity(pageFolder, selectedParentFolder);
        }

        entitySavedMap.put(pageElement, webPageDestinationFolder.getObject());
        newSelectionOnExplorer = createNewObject(webPageDestinationFolder, selectedWebElement, entitySavedMap);

        return newSelectionOnExplorer;
    }

    private FolderEntity getPageFolder(WebPage pageElement, FolderTreeEntity selectedParentFolder) throws Exception {
        
        String pageFolderAbsolutePath =  selectedParentFolder.getObject().getLocation() + File.separator + StringUtils.trim(pageElement.getName());
        
        FolderEntity pageFolder = FolderController.getInstance().getFolder(pageFolderAbsolutePath);
        return pageFolder;
    }

    private Set<ITreeEntity> createNewObject(FolderTreeEntity webPageFolder, WebElement selectedWebElement,
            Map<WebElement, FileEntity> entitySavedMap) throws Exception {
        Set<ITreeEntity> newSelectionOnExplorer = new HashSet<>();
        newSelectionOnExplorer.add(webPageFolder);
        newSelectionOnExplorer.addAll(createWebElementTree(selectedWebElement, webPageFolder, null, entitySavedMap));
        return newSelectionOnExplorer;
    }

    // For object spy
    private FolderTreeEntity createTreeFolderForPageElement(WebPage pageElement, FolderTreeEntity selectedParentFolder,
            boolean createFolderAsPageNameAllowed) throws Exception {
        if (createFolderAsPageNameAllowed) {
            return new FolderTreeEntity(
                    createFolderForPageElement(pageElement, selectedParentFolder, createFolderAsPageNameAllowed),
                    selectedParentFolder);
        }
        return selectedParentFolder;
    }

    // For using recorder.
    public FolderEntity createFolderForPageElement(WebPage pageElement, FolderTreeEntity selectedParentFolder,
            boolean createFolderAsPageNameAllowed) throws Exception {
        FolderEntity parentFolder = selectedParentFolder.getObject();
        return createFolderAsPageNameAllowed ? newPageWebElementAsFolder(parentFolder, pageElement) : parentFolder;
    }

    private FolderEntity newPageWebElementAsFolder(FolderEntity parentFolder, WebPage pageElement) throws Exception {
        return ObjectRepositoryController.getInstance().importWebElementFolder(
                WebElementUtils.convertWebPageToFolder(pageElement, parentFolder), parentFolder);
    }

    private Collection<ITreeEntity> createWebElementTree(WebElement element, FolderTreeEntity parentTreeFolder,
            WebElementEntity refElement, Map<WebElement, FileEntity> entitySavedMap) throws Exception {

        // Prepare for a new node.
        FolderEntity parentFolderEntity = parentTreeFolder.getObject();
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                WebElementUtils.convertWebElementToTestObject(element, refElement, parentFolderEntity),
                parentFolderEntity);
        WebElementTreeEntity webElementNode = new WebElementTreeEntity(importedElement, parentTreeFolder);
        entitySavedMap.put(element, importedElement);
        // Add to tree
        List<ITreeEntity> newWebElementTree = new ArrayList<>();
        newWebElementTree.add(webElementNode);
        if (element instanceof WebFrame) {
            for (WebElement childElement : ((WebFrame) element).getChildren()) {
                newWebElementTree
                        .addAll(createWebElementTree(childElement, parentTreeFolder, importedElement, entitySavedMap));
            }
        }
        return newWebElementTree;
    }

    private void addConflictedWebElement(WebPage webPage, ConflictWebElementWrapper wrapElement,
            SaveToObjectRepositoryDialogResult dialogResult, List<Object[]> testObjectIds) throws Exception {

        FolderTreeEntity selectedParentFolder = dialogResult.getSelectedParentFolder();
        ConflictOptions selectedConflictOption = dialogResult.getSelectedConflictOption();
        Map<WebElement, FileEntity> entitySavedMap = dialogResult.getEntitySavedMap();
        
        WebElement newWebElement = ((ConflictWebElementWrapper) wrapElement).getOriginalWebElement().clone();

        // Build destination folder path.
        String folderPath = ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator
                + selectedParentFolder.getObject().getRelativePath();

        if (dialogResult.isCreateFolderAsPageNameAllowed()) {
            folderPath += File.separator + StringUtils.trim(webPage.getName());
        }

        FolderEntity conflictedFolderEntity = FolderController.getInstance().getFolder(folderPath);
        
        if (wrapElement.isConflicted()) {

            // Load conflicted old element
            String oldElementAbsolutePath = folderPath + File.separator + StringUtils.trim(newWebElement.getName())
                    + WebElementEntity.getWebElementFileExtension();

            WebElementEntity oldWebElementEntity = ObjectRepositoryController.getInstance()
                    .getWebElement(oldElementAbsolutePath);

            switch (selectedConflictOption) {
                case CREATE_NEW_OBJECT:
                    newWebElement.setName(ObjectRepositoryController.getInstance().getAvailableWebElementName(
                            conflictedFolderEntity, StringUtils.trim(newWebElement.getName())));

                    WebElementEntity importedWebElement = ObjectRepositoryController.getInstance().importWebElement(
                            WebElementUtils.convertWebElementToTestObject(newWebElement, null, conflictedFolderEntity),
                            conflictedFolderEntity);

                    entitySavedMap.put(wrapElement.getOriginalWebElement(), importedWebElement);
                    break;

                case REPLACE_EXISTING_OBJECT:
                    oldWebElementEntity.setWebElementProperties(newWebElement.getProperties());
                    oldWebElementEntity.setWebElementXpaths(newWebElement.getXpaths());
                    // Replace old selector method with new one
                    oldWebElementEntity.setSelectorMethod(WebElementSelectorMethod.valueOf(wrapElement.getOriginalWebElement().getSelectorMethod().toString()));
                    entitySavedMap.put(wrapElement.getOriginalWebElement(), oldWebElementEntity);
                    break;

                case MERGE_CHANGE_TO_EXISTING_OBJECT:
                    Set<WebElementPropertyEntity> mergedProperties = new LinkedHashSet<>();
                    mergedProperties.addAll(newWebElement.getProperties());
                    //uncheck all properties of old web element.
                    for(WebElementPropertyEntity wProperty: oldWebElementEntity.getWebElementProperties()) {
                        wProperty.setIsSelected(false);
                    }
                    mergedProperties.addAll(oldWebElementEntity.getWebElementProperties());
                    oldWebElementEntity.setWebElementProperties(new ArrayList<>(mergedProperties));
                    
                    Set<WebElementXpathEntity> mergedXpaths = new LinkedHashSet<>();
                    mergedXpaths.addAll(newWebElement.getXpaths());
                    //uncheck all xpaths of old web element.
                    for(WebElementXpathEntity wXpath: oldWebElementEntity.getWebElementXpaths()) {
                        wXpath.setIsSelected(false);
                    }
                    mergedXpaths.addAll(oldWebElementEntity.getWebElementXpaths());
                    oldWebElementEntity.setWebElementXpaths(new ArrayList<>(mergedXpaths));
                    
                    // Replace old selector method with new one
                    oldWebElementEntity.setSelectorMethod(WebElementSelectorMethod.valueOf(wrapElement.getOriginalWebElement().getSelectorMethod().toString()));
                    
                    entitySavedMap.put(wrapElement.getOriginalWebElement(), oldWebElementEntity);
                    break;

                default:
                    break;
            }

            // Update old element.
            ObjectRepositoryController.getInstance().updateTestObject(oldWebElementEntity);
            testObjectIds.add(new Object[] { oldWebElementEntity.getId(), oldWebElementEntity });
        } else {

            // Insert new object is not conflicted.
            WebElementEntity newWebElementEntity = ObjectRepositoryController.getInstance().importWebElement(
                    WebElementUtils.convertWebElementToTestObject(newWebElement, null, conflictedFolderEntity),
                    conflictedFolderEntity);
            testObjectIds.add(new Object[] { newWebElementEntity.getId(), conflictedFolderEntity });
        }
        // Update don't change the structure of folder tree.
    }

    public class SaveActionResult {
        List<Object[]> updatedTestObjectIds;

        Set<ITreeEntity> newSelectionOnExplorer;
        
        int savedObjectCount;

        public SaveActionResult(List<Object[]> updatedTestObjectIds,
                Set<ITreeEntity> newSelectionOnExplorer,
                int savedObjectCount) {
            this.updatedTestObjectIds = updatedTestObjectIds;
            this.newSelectionOnExplorer = newSelectionOnExplorer;
            this.savedObjectCount = savedObjectCount;
        }

        public List<Object[]> getUpdatedTestObjectIds() {
            return updatedTestObjectIds;
        }

        public Set<ITreeEntity> getNewSelectionOnExplorer() {
            return newSelectionOnExplorer;
        }
        
        public int getSavedObjectCount() {
            return savedObjectCount;
        }
    }

}
