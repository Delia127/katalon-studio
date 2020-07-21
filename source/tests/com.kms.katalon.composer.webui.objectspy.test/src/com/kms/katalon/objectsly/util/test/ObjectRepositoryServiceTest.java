package com.kms.katalon.objectsly.util.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementXpathEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.dialog.ObjectRepositoryService;
import com.kms.katalon.objectspy.dialog.ObjectRepositoryService.SaveActionResult;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.ConflictOptions;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.ConflictWebElementWrapper;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;

public class ObjectRepositoryServiceTest {
    private ObjectRepositoryService service;

    @Before
    public void prepare() {
        service = new ObjectRepositoryService();
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator
                .getBundleFile(Platform.getBundle("com.kms.katalon.composer.webui.objectspy.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }

    @Test
    public void canMergeChangesToConflictedObjects() throws Exception {
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement expected = new WebElement("test element");
        WebElementPropertyEntity newProperty = new WebElementPropertyEntity("custom property", "custom value");
        expected.setProperties(Arrays.asList(newProperty));
        WebElementXpathEntity newXPath = new WebElementXpathEntity("custom xpath", "custom value");
        expected.setXpaths(Arrays.asList(newXPath));
        expected.setSelectorValue(SelectorMethod.BASIC, "basic/selector");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(expected, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.MERGE_CHANGE_TO_EXISTING_OBJECT, 1);

        service.saveObject(saveToObjRepoResult);

        WebElementEntity actual = ObjectRepositoryController.getInstance()
                .getWebElementByDisplayPk("Test Cases/test page/test element");
        Assert.assertEquals(actual.getName(), expected.getName());
        // Merging should not erase existing properties & XPaths & selector values
        Assert.assertTrue(actual.getWebElementProperties().size() > expected.getProperties().size());
        Assert.assertTrue(actual.getWebElementXpaths().size() > expected.getXpaths().size());
        Assert.assertTrue(
                actual.getSelectorCollection().values().size() > expected.getSelectorCollection().values().size());
        // Merging should add the new property, XPaths, values to existing properties, XPaths, values
        Assert.assertTrue(actual.getWebElementProperties().contains(newProperty));
        Assert.assertTrue(actual.getWebElementXpaths().contains(newXPath));
        Assert.assertTrue(actual.getSelectorCollection().values().contains("basic/selector"));
    }

    @Test
    public void canProduceCorrectSaveActionResultAfterMergingChangesToConflictedObjects() throws Exception {
        String expectedID = getExtensionsDirectory("resources/test_project").getAbsolutePath()
                + "/Test Cases/test page/test element.rs";
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement webElement = new WebElement("test element");
        webElement.setProperties(Arrays.asList(new WebElementPropertyEntity("tag", "a")));
        webElement.setSelectorValue(SelectorMethod.XPATH, "test/xpath");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(webElement, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.MERGE_CHANGE_TO_EXISTING_OBJECT, 1);

        SaveActionResult saveActionResult = service.saveObject(saveToObjRepoResult);

        // save action result must contain two objects: parent's page and element. Parent's element is used in
        // RecordHandler#addRecordedElements
        // to set alias to the elements in test script. These aliases are folders that users choose to save these
        // elements to
        Assert.assertEquals(saveActionResult.getSavedObjectCount(), 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().size(), 1);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0).length, 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0)[0], expectedID);
    }

    @Test
    public void canReplaceExistingConflictedObjects() throws Exception {
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement expected = new WebElement("test element 2");
        expected.setProperties(Arrays.asList(new WebElementPropertyEntity("tag", "a")));
        expected.setSelectorValue(SelectorMethod.XPATH, "test/xpath");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(expected, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.REPLACE_EXISTING_OBJECT, 1);

        service.saveObject(saveToObjRepoResult);

        WebElementEntity actual = ObjectRepositoryController.getInstance()
                .getWebElementByDisplayPk("Test Cases/test page/test element 2");
        Assert.assertEquals(actual.getName(), expected.getName());
        Assert.assertEquals(actual.getWebElementProperties(), expected.getProperties());
        Assert.assertEquals(actual.getWebElementXpaths(), expected.getXpaths());
        Assert.assertEquals(actual.getSelectorMethod().toString(), expected.getSelectorMethod().toString());
        Assert.assertEquals(actual.getSelectorCollection().values(), expected.getSelectorCollection().values());
    }

    @Test
    public void canProduceCorrectSaveActionResultAfterReplacingExistingConflictedObjects() throws Exception {
        String expectedID = getExtensionsDirectory("resources/test_project").getAbsolutePath()
                + "/Test Cases/test page/test element 2.rs";
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement expected = new WebElement("test element 2");
        expected.setProperties(Arrays.asList(new WebElementPropertyEntity("tag", "a")));
        expected.setSelectorValue(SelectorMethod.XPATH, "test/xpath");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(expected, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.REPLACE_EXISTING_OBJECT, 1);

        SaveActionResult saveActionResult = service.saveObject(saveToObjRepoResult);

        // save action result must contain two objects: parent's page and element. Parent's element is used in
        // RecordHandler#addRecordedElements
        // to set alias to the elements in test script. These aliases are folders that users choose to save these
        // elements to
        Assert.assertEquals(saveActionResult.getSavedObjectCount(), 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().size(), 1);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0).length, 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0)[0], expectedID);
    }

    @Test
    public void canCreateNewObjectsForConflictedObjects() throws Exception {
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement expected = new WebElement("test element 3");
        expected.setProperties(Arrays.asList(new WebElementPropertyEntity("tag", "a")));
        expected.setSelectorValue(SelectorMethod.XPATH, "test/xpath");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(expected, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.CREATE_NEW_OBJECT, 1);

        service.saveObject(saveToObjRepoResult);

        WebElementEntity actual = ObjectRepositoryController.getInstance()
                .getWebElementByDisplayPk("Test Cases/test page/test element 3 (1)");
        // Creating a new object in the same place as old object would result in a different name
        Assert.assertEquals(actual.getName(), "test element 3 (1)");
        Assert.assertEquals(actual.getWebElementProperties(), expected.getProperties());
        Assert.assertEquals(actual.getWebElementXpaths(), expected.getXpaths());
        Assert.assertEquals(actual.getSelectorMethod().toString(), expected.getSelectorMethod().toString());
        Assert.assertEquals(actual.getSelectorCollection().values(), expected.getSelectorCollection().values());

        ObjectRepositoryController.getInstance().deleteWebElement(actual);
    }

    @Test
    public void canProduceCorrectSaveActionResultAfterCreatingNewObjectsForConflictedObjects() throws Exception {
        String expectedID = getExtensionsDirectory("resources/test_project").getAbsolutePath()
                + "/Test Cases/test page/test element 3 (1).rs";
        ProjectEntity currentPrj = new ProjectEntity();
        currentPrj.setFolderLocation(getExtensionsDirectory("resources/test_project").getAbsolutePath());
        FolderTreeEntity selectedParentFolder = new FolderTreeEntity(
                FolderController.getInstance().getTestCaseRoot(currentPrj), null);
        DataProviderState.getInstance().setCurrentProject(currentPrj);
        List<ConflictWebElementWrapper> selectedPages = new ArrayList<ConflictWebElementWrapper>();
        WebElement webPage = new WebPage("test page");
        ConflictWebElementWrapper conflictWebPageWrapper = new ConflictWebElementWrapper(webPage, true);
        WebElement expected = new WebElement("test element 3");
        expected.setProperties(Arrays.asList(new WebElementPropertyEntity("tag", "a")));
        expected.setSelectorValue(SelectorMethod.XPATH, "test/xpath");
        ConflictWebElementWrapper conflictWebElementWrapper = new ConflictWebElementWrapper(expected, true);
        conflictWebElementWrapper.setParent(conflictWebPageWrapper);
        conflictWebPageWrapper.setChildren(Arrays.asList(conflictWebElementWrapper));
        selectedPages.add(conflictWebPageWrapper);
        SaveToObjectRepositoryDialogResult saveToObjRepoResult = new SaveToObjectRepositoryDialogResult(true,
                selectedPages, selectedParentFolder, ConflictOptions.CREATE_NEW_OBJECT, 1);

        SaveActionResult saveActionResult = service.saveObject(saveToObjRepoResult);

        // save action result must contain two objects: parent's page and element. Parent's element is used in
        // RecordHandler#addRecordedElements
        // to set alias to the elements in test script. These aliases are folders that users choose to save these
        // elements to
        Assert.assertEquals(saveActionResult.getSavedObjectCount(), 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().size(), 1);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0).length, 2);
        Assert.assertEquals(saveActionResult.getUpdatedTestObjectIds().get(0)[0], expectedID);
        WebElementEntity actual = ObjectRepositoryController.getInstance()
                .getWebElementByDisplayPk("Test Cases/test page/test element 3 (1)");
        ObjectRepositoryController.getInstance().deleteWebElement(actual);
    }

}
