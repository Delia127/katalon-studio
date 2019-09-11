package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.composer.keyword.dialogs.RenameKeywordDialog;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class PastePackageHandler {
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    Object targetObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (targetObject != null) {
                        ITreeEntity targetTreeEntity = null;
                        IPackageFragment targetPackageFragment = null;
                        if (targetObject instanceof PackageTreeEntity) {
                            targetPackageFragment = (IPackageFragment) ((PackageTreeEntity) targetObject).getObject();
                            targetTreeEntity = (ITreeEntity) targetObject;
                        } else if (targetObject instanceof ITreeEntity
                                && ((ITreeEntity) targetObject).getParent() instanceof PackageTreeEntity) {
                            targetPackageFragment = (IPackageFragment) ((PackageTreeEntity) ((ITreeEntity) targetObject)
                                    .getParent()).getObject();
                            targetTreeEntity = (ITreeEntity) ((ITreeEntity) targetObject).getParent();
                        }
                        if (targetPackageFragment != null) {
                            Clipboard clipboard = new Clipboard(Display.getCurrent());
                            String[] treeEntityPaths = (String[]) clipboard.getContents(FileTransfer.getInstance());
                            if (treeEntityPaths != null) {
                                if (TransferMoveFlag.isMove()) {
                                    move(treeEntityPaths, targetPackageFragment);
                                    GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                                            .refreshLocal(IResource.DEPTH_INFINITE, null);
                                } else {
                                    copy(treeEntityPaths, targetPackageFragment);
                                    GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                                            .refreshLocal(IResource.DEPTH_INFINITE, null);
                                }
                                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity);
                                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetTreeEntity);
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

    private void copy(String[] treeEntityPaths, IPackageFragment targetPackageFragment) throws Exception {
        try {
            IWorkspaceRoot workspaceRoot = targetPackageFragment.getResource().getWorkspace().getRoot();
            for (String treeEntityPath : treeEntityPaths) {
                copyKeyword(getKeywordFile(workspaceRoot, treeEntityPath), targetPackageFragment, null);
            }
        } catch (OperationCanceledException operationCanceledException) {
            return;
        }
    }

    private void move(String[] treeEntityPaths, IPackageFragment targetPackageFragment) throws Exception {
        try {
            IWorkspaceRoot workspaceRoot = targetPackageFragment.getResource().getWorkspace().getRoot();
            for (String treeEntityPath : treeEntityPaths) {
                IFile file = getKeywordFile(workspaceRoot, treeEntityPath);
                moveKeyword(file, targetPackageFragment, null);
            }

            // Refresh Keywords root folder
            FolderEntity kwRootFolder = FolderController.getInstance().getKeywordRoot(
                    ProjectController.getInstance().getCurrentProject());
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, new FolderTreeEntity(kwRootFolder, null));
        } catch (OperationCanceledException operationCanceledException) {
            return;
        }
    }

    private void copyKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws Exception {
        try {
            GroovyUtil.copyKeyword(keywordFile, targetPackageFragment, newName);
            String copiedKeywordFilePath = getPastedFilePath(keywordFile, targetPackageFragment, newName);
            
            KeywordTreeEntity keywordTreeEntity = TreeEntityUtil.getKeywordTreeEntity(
                    copiedKeywordFilePath, ProjectController.getInstance().getCurrentProject());
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity);
            
            eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM, new Object[] {
                    keywordFile.getProjectRelativePath().toString(), copiedKeywordFilePath });
        } catch (JavaModelException javaModelException) {
            if (javaModelException.getJavaModelStatus().getCode() == IJavaModelStatusConstants.NAME_COLLISION) {
                openRenameDialog(keywordFile, targetPackageFragment, false);
            }
        }
    }

    private void moveKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws Exception {
        try {
            GroovyUtil.moveKeyword(keywordFile, targetPackageFragment, newName);
            String cutKeywordFilePath = getPastedFilePath(keywordFile, targetPackageFragment, newName);
            
            KeywordTreeEntity keywordTreeEntity = TreeEntityUtil.getKeywordTreeEntity(
                    cutKeywordFilePath, ProjectController.getInstance().getCurrentProject());
            refactorReferencingTestSuites(ProjectController.getInstance().getCurrentProject(), keywordFile,
                    keywordFile.getLocation().toString(), cutKeywordFilePath);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity);
            
            eventBroker.post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM, new Object[] {
                    keywordFile.getProjectRelativePath().toString(), cutKeywordFilePath });
        } catch (JavaModelException javaModelException) {
            if (javaModelException.getJavaModelStatus().getCode() == IJavaModelStatusConstants.NAME_COLLISION) {
                openRenameDialog(keywordFile, targetPackageFragment, true);
            }
        }
    }

    /**
     * Get new pasted relative Keyword file path to project
     * 
     * @param keywordFile IFile
     * @param targetPackageFragment IPackageFragment
     * @param newName String
     * @return Project relative path for Keyword file (String)
     * */
    private String getPastedFilePath(IFile keywordFile, IPackageFragment targetPackageFragment, String newName) {
        String keywordRootPath = targetPackageFragment.getParent().getElementName() + IPath.SEPARATOR;
        String packageName = targetPackageFragment.getElementName();
        String packagePath = keywordRootPath
                + (packageName.isEmpty() ? packageName : packageName.replaceAll("[.]", String.valueOf(IPath.SEPARATOR))
                        + IPath.SEPARATOR);
        String kwFileName = (newName != null) ? newName + GroovyConstants.GROOVY_FILE_EXTENSION : keywordFile.getName();
        String copiedKeywordFilePath = packagePath + kwFileName;
        return copiedKeywordFilePath;
    }

    /**
     * @param workspaceRoot Workspace Root
     * @param filePath Keyword file path
     * @return IFile Keyword file
     * @throws Exception
     */
    private IFile getKeywordFile(IWorkspaceRoot workspaceRoot, String filePath) throws Exception {
        String fileName = filePath.substring(filePath.lastIndexOf(IPath.SEPARATOR) + 1);
        String parentContainer = filePath.substring(0, filePath.lastIndexOf(IPath.SEPARATOR));
        IResource resource = workspaceRoot.findMember(new Path(parentContainer));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            // Container does not exist
            throw new Exception(StringConstants.HAND_ERROR_MSG_FILE_NOT_EXIST);
        }
        IContainer container = (IContainer) resource;
        return container.getFile(new Path(fileName));
    }

    /**
     * Open Rename dialog to get new name for copying/moving keyword
     * 
     * @param keywordFile Keyword file
     * @param parentPackage Keyword parent package fragment
     * @param isMoving TRUE if Keyword is moving. Otherwise, FALSE for copying
     * @throws Exception
     */
    private void openRenameDialog(IFile keywordFile, IPackageFragment parentPackage, boolean isMoving) throws Exception {
        RenameKeywordDialog dialog = new RenameKeywordDialog(parentShell, parentPackage);
        String kwName = StringUtils.removeEndIgnoreCase(keywordFile.getName(), GroovyConstants.GROOVY_FILE_EXTENSION);
        dialog.setName(kwName);
        dialog.setWindowTitle(StringConstants.HAND_TITLE_NAME_CONFLICT);
        dialog.setDialogMsg(MessageFormat.format(StringConstants.HAND_MSG_KW_NAME_ALREADY_EXISTS, kwName));
        dialog.open();
        if (dialog.getReturnCode() == Dialog.OK) {
            if (isMoving) {
                moveKeyword(keywordFile, parentPackage, dialog.getName());
            } else {
                copyKeyword(keywordFile, parentPackage, dialog.getName());
            }
        }
    }

    private static void refactorReferencingTestSuites(ProjectEntity project, IFile keyword, String oldKeywordLocation,
            String newKeywordLocation) throws Exception {
        // if test case changed its name, update reference Location in test
        // suites that refer to it
        List<TestCaseEntity> lstTestCases = FolderFileServiceManager
                .getDescendantTestCasesOfFolder(FolderFileServiceManager.getTestCaseRoot(project));
        String constant = "keywords";
        String packageName = project.getFolderLocation() + File.separator + constant + File.separator;
        File projectFile = new File(packageName);
        String oldRelativeKwLocation = oldKeywordLocation.substring(projectFile.getAbsolutePath().length() +1 );
        String oldRelativeTcId = FilenameUtils.removeExtension(oldRelativeKwLocation).replace("/", ".");
        String newRelativeKwLocation = newKeywordLocation.substring(constant.length() + 1);
        String newRelativeTcId = FilenameUtils.removeExtension(newRelativeKwLocation).replace("/", ".");

        for (TestCaseEntity testCase : lstTestCases) {
            ICompilationUnit script = GroovyGuiUtil.getOrCreateGroovyScriptForTestCase(testCase);
            String str = "";
            File file = new File(script.getResource().getLocation().toString());
            str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (str.contains("CustomKeywords")) {
                String newString = str.replace(oldRelativeTcId, newRelativeTcId);
                GroovyGuiUtil.addContentToTestCase(testCase, newString);
            }
        }
    }
}
