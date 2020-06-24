package com.katalon.plugin.smart_xpath.controller.test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.EventHandler;

import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.katalon.plugin.smart_xpath.controller.AutoHealingController;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;

public class AutoHealingControllerTest {

    private ProjectEntity testProject;

    private FolderEntity folderEntity;

    private String location;

    private BrokenTestObject brokenTestObject;

    private WebElementEntity testObject;

    @Inject
    private IEventBroker eventBroker = new EventBrokerMock();

    public class EventBrokerMock implements IEventBroker {

        @Override
        public boolean send(String topic, Object data) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean post(String topic, Object data) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean subscribe(String topic, EventHandler eventHandler) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean unsubscribe(EventHandler eventHandler) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    @Before
    public void setUp() throws Exception {
        File testProjectFolder = Files.createTempDirectory("kat-test").toFile();
        location = testProjectFolder.getAbsolutePath();
        testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProjectForUI(testProject.getId(), false, new NullProgressMonitor());

        DataProviderState.getInstance().setCurrentProject(testProject);

        folderEntity = new FolderEntity();
        folderEntity.setName(GlobalMessageConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY);
        folderEntity.setProject(testProject);

        EventBrokerSingleton.getInstance().setEventBroker(eventBroker);
        IEclipseContext context = EclipseContextFactory.create();
        context.set(IEventBroker.class, eventBroker);

        brokenTestObject = new BrokenTestObject();
        String testObjectId = GlobalMessageConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY + "/" + "TestObject";
        brokenTestObject.setTestObjectId(testObjectId);
        brokenTestObject.setProposedLocatorMethod(SelectorMethod.XPATH);
        brokenTestObject.setProposedLocator("//a[@id='btn-make-appointment']");

        testObject = new WebElementEntity();
        testObject.setProject(testProject);
        testObject.setParentFolder(folderEntity);
        testObject.setName("TestObject");

        testObject = ObjectRepositoryController.getInstance().saveNewTestObject(testObject);

    }

    @Test
    public void healBrokenTestObjectTest() throws Exception {
        // When
        AutoHealingController.healBrokenTestObject(brokenTestObject);

        // Then
        testObject = ObjectRepositoryController.getInstance()
                .getWebElementByDisplayPk(brokenTestObject.getTestObjectId());
        /// - Update Selector Method
        WebElementSelectorMethod actualSelectorMethod = testObject.getSelectorMethod();
        WebElementSelectorMethod expectedSelectorMethod = WebElementSelectorMethod
                .valueOf(brokenTestObject.getProposedLocatorMethod().name());
        Assert.assertEquals("Test object should be updated Selector Method", actualSelectorMethod,
                expectedSelectorMethod);

        /// - Update Selector Value
        Map<WebElementSelectorMethod, String> actualSelectorCollection = testObject.getSelectorCollection();
        String actualSelectorValue = actualSelectorCollection.get(expectedSelectorMethod);
        String expectedSelectorValue = brokenTestObject.getProposedLocator();

        Assert.assertEquals("Test object should be updated Selector Value", actualSelectorValue, expectedSelectorValue);
    }

    @Test
    public void readUnapprovedBrokenTestObjectsTest() {
        // Given
        String projectDir = testProject.getFolderLocation();
        String rawSelfHealingDir = FilenameUtils.concat(projectDir, SmartXPathConstants.SELF_HEALING_FOLDER_PATH);
        String selfHealingDir = FilenameUtils.separatorsToSystem(rawSelfHealingDir);
        File selfHealingDirectory = new File(selfHealingDir);

        String autoHealingFileDir = AutoHealingController.getDataFilePath(projectDir);
        File autoHealingFile = new File(autoHealingFileDir);

        // When
        Set<BrokenTestObject> actualBrokenTestObjectCollection = AutoHealingController
                .readUnapprovedBrokenTestObjects(testProject);

        // Then
        Assert.assertEquals("Saved broken Test Objects should be as before saved", new HashSet<>(),
                actualBrokenTestObjectCollection);
        Assert.assertTrue(selfHealingDirectory.exists());
        Assert.assertTrue(autoHealingFile.exists());
    }
}
