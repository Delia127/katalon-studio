package com.kms.katalon.composer.folder.handlers;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import org.eclipse.jdt.internal.ui.refactoring.reorg.NewNameQueries;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.utils.EntityTrackingHelper;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityProcessingUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class PasteFolderHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    MApplication application;

    @Inject
    EModelService modelService;

    @Named(IServiceConstants.ACTIVE_SHELL)
    Shell parentShell;

    /** Used to locate the last pasted tree entity */
    private ITreeEntity lastPastedTreeEntity;

    /** Used to locate the last pasted parent tree entity */
    private ITreeEntity parentPastedTreeEntity;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    Object targetObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (targetObject != null) {
                        ITreeEntity targetTreeEntity = null;
                        FolderEntity targetFolder = null;
                        if (targetObject instanceof FolderTreeEntity) {
                            targetFolder = (FolderEntity) ((FolderTreeEntity) targetObject).getObject();
                            targetTreeEntity = (ITreeEntity) targetObject;
                        } else if (targetObject instanceof ITreeEntity
                                && ((ITreeEntity) targetObject).getParent() instanceof FolderTreeEntity) {
                            targetFolder = (FolderEntity) ((FolderTreeEntity) ((ITreeEntity) targetObject).getParent())
                                    .getObject();
                            targetTreeEntity = (ITreeEntity) ((ITreeEntity) targetObject).getParent();
                        }
                        if (targetFolder != null) {
                            Clipboard clipboard = new Clipboard(Display.getCurrent());

                            ITreeEntity[] treeEntities = (ITreeEntity[]) clipboard
                                    .getContents(TreeEntityTransfer.getInstance());
                            if (verifyPaste(treeEntities, targetFolder)) {
                                parentPastedTreeEntity = targetTreeEntity;
                                lastPastedTreeEntity = null;
                                if (TransferMoveFlag.isMove()) {
                                    move(treeEntities, targetFolder);
                                    for (ITreeEntity treeEntity : treeEntities) {
                                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                                                treeEntity.getParent());
                                    }
                                } else {
                                    copy(treeEntities, targetFolder);
                                }
                                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity);
                                eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                                        lastPastedTreeEntity != null ? lastPastedTreeEntity : targetTreeEntity);
                            }
                        }
                    }
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                            StringConstants.HAND_ERROR_MSG_UNABLE_TO_PASTE_DATA);
                }
            }
        });
    }

    private void copy(ITreeEntity[] treeEntities, FolderEntity targetFolder) throws Exception {
        try {
            for (ITreeEntity treeEntity : treeEntities) {
                if (treeEntity instanceof TestCaseTreeEntity && targetFolder.getFolderType() == FolderType.TESTCASE) {
                    copyTestCase((TestCaseEntity) ((TestCaseTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof FolderTreeEntity
                        && targetFolder.getFolderType() == ((FolderEntity) ((FolderTreeEntity) treeEntity).getObject())
                                .getFolderType()) {
                    copyFolder((FolderEntity) ((FolderTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof TestSuiteTreeEntity
                        && targetFolder.getFolderType() == FolderType.TESTSUITE) {
                    copyTestSuite((TestSuiteEntity) ((TestSuiteTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof TestDataTreeEntity
                        && targetFolder.getFolderType() == FolderType.DATAFILE) {
                    copyTestData((DataFileEntity) ((TestDataTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof WebElementTreeEntity
                        && targetFolder.getFolderType() == FolderType.WEBELEMENT) {
                    copyTestObject((WebElementEntity) ((WebElementTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof PackageTreeEntity
                        && targetFolder.getFolderType() == FolderType.KEYWORD) {
                    copyKeywordPackage((IPackageFragment) ((PackageTreeEntity) treeEntity).getObject(), targetFolder,
                            null);
                } else if (treeEntity instanceof TestSuiteCollectionTreeEntity
                        && targetFolder.getFolderType() == FolderType.TESTSUITE) {
                    copyTestSuiteCollection(
                            (TestSuiteCollectionEntity) ((TestSuiteCollectionTreeEntity) treeEntity).getObject(),
                            targetFolder);
                } else if (treeEntity instanceof CheckpointTreeEntity
                        && targetFolder.getFolderType() == FolderType.CHECKPOINT) {
                    copyCheckpoint(((CheckpointTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof ProfileTreeEntity
                        && targetFolder.getFolderType() == FolderType.PROFILE) {
                    copyExecutionProfile((ProfileTreeEntity) treeEntity, targetFolder);
                } else if (treeEntity instanceof SystemFileTreeEntity
                        && targetFolder.getFolderType() == FolderType.INCLUDE) {
                    copySystemFileEntity((SystemFileTreeEntity) treeEntity, targetFolder);
                } else if (treeEntity instanceof WindowsElementTreeEntity
                        && targetFolder.getFolderType() == FolderType.WEBELEMENT) {
                    copyWindowsObject(((WindowsElementTreeEntity) treeEntity).getObject(), targetFolder);
                } 
                GroovyUtil.getGroovyProject(targetFolder.getProject()).refreshLocal(IResource.DEPTH_INFINITE, null);
            }
        } catch (OperationCanceledException operationCanceledException) {
            return;
        }
    }

    private void move(ITreeEntity[] treeEntities, FolderEntity targetFolder) throws Exception {
        try {
            for (ITreeEntity treeEntity : treeEntities) {
                if (!treeEntity.isRemoveable()) {
                    continue;
                }
                if (treeEntity instanceof TestCaseTreeEntity) {
                    moveTestCase((TestCaseEntity) ((TestCaseTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof FolderTreeEntity) {
                    moveFolder((FolderEntity) ((FolderTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof TestSuiteTreeEntity) {
                    moveTestSuite((TestSuiteEntity) ((TestSuiteTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof TestDataTreeEntity) {
                    moveTestData((DataFileEntity) ((TestDataTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof WebElementTreeEntity) {
                    moveTestObject((WebElementEntity) ((WebElementTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof PackageTreeEntity) {
                    moveKeywordPackage((IPackageFragment) ((PackageTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof TestSuiteCollectionTreeEntity) {
                    moveTestSuiteCollection(
                            (TestSuiteCollectionEntity) ((TestSuiteCollectionTreeEntity) treeEntity).getObject(),
                            targetFolder);
                } else if (treeEntity instanceof CheckpointTreeEntity) {
                    moveCheckpoint(((CheckpointTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof SystemFileTreeEntity) {
                    moveSystemFile(((SystemFileTreeEntity) treeEntity).getObject(), targetFolder);
                } else if (treeEntity instanceof WindowsElementTreeEntity) {
                    moveWindowsObject(((WindowsElementTreeEntity) treeEntity).getObject(), targetFolder);
                }
            }
            GroovyUtil.getGroovyProject(targetFolder.getProject()).refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (OperationCanceledException operationCanceledException) {
            return;
        }
    }

    private boolean verifyPaste(ITreeEntity[] treeEntities, FolderEntity targetFolder) throws Exception {
        if (treeEntities == null)
            return false;
        for (ITreeEntity treeEntity : treeEntities) {
            if (treeEntity instanceof FolderTreeEntity && treeEntity.getObject() instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) treeEntity.getObject();
                if (folder.equals(targetFolder)) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                            MessageFormat.format(StringConstants.HAND_ERROR_MSG_UNABLE_TO_PASTE_SAME_SRC_DEST,
                                    folder.getName()));
                    return false;
                }
            }
            // Do not allow pasting across file type areas
            if (!treeEntity.getCopyTag().equals(targetFolder.getFolderType().toString())) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                        MessageFormat.format(StringConstants.HAND_ERROR_MSG_CANNOT_PASTE_INTO_DIFF_REGION,
                                treeEntity.getCopyTag(), targetFolder.getFolderType().toString()));
                return false;
            }
        }
        return true;
    }

    private void copyTestCase(TestCaseEntity testCase, FolderEntity targetFolder) throws Exception {
        if (testCase != null) {
            TestCaseEntity copiedTestCase = TestCaseController.getInstance().copyTestCase(testCase, targetFolder);
            if (copiedTestCase != null) {
                eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                        new Object[] { testCase.getIdForDisplay(), copiedTestCase.getIdForDisplay() });
                lastPastedTreeEntity = new TestCaseTreeEntity(copiedTestCase, parentPastedTreeEntity);
                EntityTrackingHelper.trackTestCaseCreated();
            }
        }
    }

    private void copyFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        FolderEntity copiedFolder = FolderController.getInstance().copyFolder(folder, targetFolder);
        if (copiedFolder != null) {
            eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                    new Object[] { folder.getRelativePathForUI().replace('\\', IPath.SEPARATOR) + IPath.SEPARATOR,
                            copiedFolder.getRelativePathForUI().replace('\\', IPath.SEPARATOR) + IPath.SEPARATOR });
            lastPastedTreeEntity = new FolderTreeEntity(copiedFolder, parentPastedTreeEntity);
        }
    }

    private void copyTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        if (testSuite != null) {
            TestSuiteEntity copiedTestSuite = TestSuiteController.getInstance().copyTestSuite(testSuite, targetFolder);
            if (copiedTestSuite != null) {
                eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                        new Object[] { testSuite.getIdForDisplay(), copiedTestSuite.getIdForDisplay() });
                lastPastedTreeEntity = new TestSuiteTreeEntity(copiedTestSuite, parentPastedTreeEntity);
            }
        }
    }

    private void copyTestData(DataFileEntity dataFile, FolderEntity targetFolder) throws Exception {
        if (dataFile != null) {
            DataFileEntity copiedDataFile = TestDataController.getInstance().copyDataFile(dataFile, targetFolder);
            if (copiedDataFile != null) {
                eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                        new Object[] { dataFile.getIdForDisplay(), copiedDataFile.getIdForDisplay() });
                lastPastedTreeEntity = new TestDataTreeEntity(copiedDataFile, parentPastedTreeEntity);
            }
        }
    }

    private void copyTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection, FolderEntity targetFolder)
            throws Exception {
        if (testSuiteCollection == null) {
            return;
        }
        TestSuiteCollectionEntity copiedTestSuiteCollection = TestSuiteCollectionController.getInstance()
                .copyTestSuiteCollection(testSuiteCollection, targetFolder);
        if (copiedTestSuiteCollection != null) {
            eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM, new Object[] {
                    testSuiteCollection.getIdForDisplay(), copiedTestSuiteCollection.getIdForDisplay() });
            lastPastedTreeEntity = new TestSuiteCollectionTreeEntity(copiedTestSuiteCollection,
                    (FolderTreeEntity) parentPastedTreeEntity);
        }
    }

    private void copyTestObject(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        if (webElement != null) {
            WebElementEntity copiedWebElement = ObjectRepositoryController.getInstance().copyWebElement(webElement,
                    targetFolder);
            if (copiedWebElement != null) {
                eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                        new Object[] { webElement.getIdForDisplay(), copiedWebElement.getIdForDisplay() });
                lastPastedTreeEntity = new WebElementTreeEntity(copiedWebElement, parentPastedTreeEntity);
            }
        }
    }

    private void copyKeywordPackage(IPackageFragment packageFragment, FolderEntity targetFolder, String newPackageName)
            throws Exception {
        try {
            String parentPath = packageFragment.getParent().getElementName() + IPath.SEPARATOR;
            String packageName = packageFragment.getElementName();
            GroovyUtil.copyPackage(packageFragment, targetFolder, newPackageName);
            if (newPackageName == null) {
                newPackageName = packageName;
            }
            eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                    new Object[] {
                            parentPath + (packageName.isEmpty() ? StringConstants.DEFAULT_PACKAGE_NAME : packageName),
                            parentPath + newPackageName });
        } catch (JavaModelException javaModelException) {
            if (javaModelException.getJavaModelStatus().getCode() == IJavaModelStatusConstants.NAME_COLLISION) {
                NewNameQueries newNameQueries = new NewNameQueries(parentShell);
                INewNameQuery newNameQuery = newNameQueries.createNewPackageNameQuery(packageFragment,
                        packageFragment.getElementName());
                copyKeywordPackage(packageFragment, targetFolder, newNameQuery.getNewName());
            }
        }
    }

    private void copyCheckpoint(CheckpointEntity checkpoint, FolderEntity targetFolder) throws DALException {
        if (checkpoint == null) {
            return;
        }
        CheckpointEntity copiedCheckpoint = CheckpointController.getInstance().copy(checkpoint, targetFolder);
        if (copiedCheckpoint != null) {
            eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM,
                    new Object[] { checkpoint.getIdForDisplay(), copiedCheckpoint.getIdForDisplay() });
            lastPastedTreeEntity = new CheckpointTreeEntity(copiedCheckpoint, parentPastedTreeEntity);
        }
    }

    private void copyExecutionProfile(ProfileTreeEntity profileTree, FolderEntity targetFolder) throws Exception {
        ExecutionProfileEntity sourceProfile = profileTree.getObject();
        ExecutionProfileEntity coppiedProfile = GlobalVariableController.getInstance().copyProfile(sourceProfile);

        eventBroker.post(EventConstants.EXECUTION_PROFILE_CREATED, coppiedProfile.getName());

        lastPastedTreeEntity = new ProfileTreeEntity(coppiedProfile, profileTree.getParent());
    }

    private void copySystemFileEntity(SystemFileTreeEntity treeEntity, FolderEntity targetFolder) throws Exception {
        SystemFileEntity coppiedSystemFile = SystemFileController.getInstance().copySystemFile(treeEntity.getObject(),
                targetFolder);

        lastPastedTreeEntity = new SystemFileTreeEntity(coppiedSystemFile, (FolderTreeEntity) parentPastedTreeEntity);
    }

    private void moveTestCase(TestCaseEntity testCase, FolderEntity targetFolder) throws Exception {
        TestCaseEntity movedTestCase = EntityProcessingUtil.moveTestCase(testCase, targetFolder);
        if (movedTestCase != null) {
            lastPastedTreeEntity = new TestCaseTreeEntity(movedTestCase, parentPastedTreeEntity);
        }
    }

    private void moveFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        FolderEntity movedFolder = EntityProcessingUtil.moveFolder(folder, targetFolder);
        if (movedFolder != null) {
            lastPastedTreeEntity = new FolderTreeEntity(movedFolder, parentPastedTreeEntity);
        }
    }

    private void moveTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        TestSuiteEntity movedTestSuite = EntityProcessingUtil.moveTestSuite(testSuite, targetFolder);
        if (movedTestSuite != null) {
            lastPastedTreeEntity = new TestSuiteTreeEntity(movedTestSuite, parentPastedTreeEntity);
        }
    }

    private void moveTestData(DataFileEntity dataFile, FolderEntity targetFolder) throws Exception {
        DataFileEntity movedTestData = EntityProcessingUtil.moveTestData(dataFile, targetFolder);
        if (movedTestData != null) {
            lastPastedTreeEntity = new TestDataTreeEntity(movedTestData, parentPastedTreeEntity);
        }
    }

    private void moveTestObject(WebElementEntity webElement, FolderEntity targetFolder) throws Exception {
        WebElementEntity movedTestObject = EntityProcessingUtil.moveTestObject(webElement, targetFolder);
        if (movedTestObject != null) {
            lastPastedTreeEntity = new WebElementTreeEntity(movedTestObject, parentPastedTreeEntity);
        }
    }

    private void moveKeywordPackage(IPackageFragment packageFragment, FolderEntity targetFolder) {
        // not allow moving package
    }

    private void moveTestSuiteCollection(TestSuiteCollectionEntity dataFile, FolderEntity targetFolder)
            throws Exception {
        TestSuiteCollectionEntity movedTestSuiteCollection = EntityProcessingUtil.moveTestSuiteCollection(dataFile,
                targetFolder);
        if (movedTestSuiteCollection != null) {
            lastPastedTreeEntity = new TestSuiteCollectionTreeEntity(movedTestSuiteCollection,
                    (FolderTreeEntity) parentPastedTreeEntity);
        }
    }

    private void moveCheckpoint(CheckpointEntity checkpoint, FolderEntity targetFolder) throws Exception {
        CheckpointEntity movedCheckpoint = EntityProcessingUtil.moveCheckpoint(checkpoint, targetFolder);
        if (movedCheckpoint != null) {
            lastPastedTreeEntity = new CheckpointTreeEntity(movedCheckpoint, parentPastedTreeEntity);
        }
    }

    private void moveSystemFile(SystemFileEntity systemFile, FolderEntity targetFolder) throws DALException {
        SystemFileEntity newSystemFile = SystemFileController.getInstance().moveSystemFile(systemFile, targetFolder);
        if (newSystemFile != null) {
            lastPastedTreeEntity = new SystemFileTreeEntity(newSystemFile, (FolderTreeEntity) parentPastedTreeEntity);
        }
    }

    private void moveWindowsObject(WindowsElementEntity windowsElement, FolderEntity targetFolder) throws ControllerException {
        String oldId = windowsElement.getIdForDisplay();
        WindowsElementEntity newsWindowsElement = WindowsElementController.getInstance()
                .moveWindowsElementEntity(windowsElement, targetFolder);
        if (newsWindowsElement != null) {
            lastPastedTreeEntity = new WindowsElementTreeEntity(newsWindowsElement,
                    (FolderTreeEntity) parentPastedTreeEntity);

            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                    new Object[] { oldId, newsWindowsElement.getIdForDisplay() });
        }
    }

    private void copyWindowsObject(WindowsElementEntity object, FolderEntity targetFolder) throws ControllerException {
        WindowsElementEntity coppiedSystemFile = WindowsElementController.getInstance().copyWindowsElementEntity(object,
                targetFolder);

        lastPastedTreeEntity = new WindowsElementTreeEntity(coppiedSystemFile,
                (FolderTreeEntity) parentPastedTreeEntity);
    }

}
