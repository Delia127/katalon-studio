package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.ExplorerPreferenceConstants;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.util.groovy.editor;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.controller.UserFileController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LinkEditorHandler implements EventHandler {
    @Inject
    private EModelService modelService;

    @Inject
    private IEventBroker eventBroker;

    private ScopedPreferenceStore store;

    @Inject
    private MApplication application;

    private MWindow window;

    private MPartStack stack;

    @PostConstruct
    public void initListener() {
        store = getPreferenceStore(LinkEditorHandler.class);
        eventBroker.subscribe(UIEvents.UILifeCycle.BRINGTOTOP, this);
    }

    private void setActive(boolean isActive) {
        try {
            store.setValue(ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART, isActive);
            store.save();
        } catch (IOException e) {
            logError(e);
        }
    }

    private boolean getActive() {
        return store.getBoolean(ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART);
    }

    @CanExecute
    public boolean canExecute(MHandledToolItem item) {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(MHandledToolItem item) {
        if (item.isSelected()) {
            setActive(true);
            window = application.getChildren().get(0);
            IEclipseContext appContext = application.getContext();
            IEclipseContext context = window.getContext();
            if (context == null) {
                context = appContext;
            }
            modelService = context.get(EModelService.class);
            stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);

            if (stack != null) {
                performLinkWithPart((MPart) stack.getSelectedElement());
            }
        } else {
            setActive(false);
        }
    }

    @Override
    public void handleEvent(Event event) {
        Object object = event.getProperty(EventTags.ELEMENT);
        if (getActive()
                && UIEvents.UILifeCycle.BRINGTOTOP.equals(event.getTopic()) 
                && (object != null) 
                && (object instanceof MPart)) {
            MPart mpart = (MPart) object;
            performLinkWithPart(mpart);
        }
    }

    private void performLinkWithPart(MPart mpart) {
        try {
            IEntity entity = EntityPartUtil.getEntityByPartId(mpart.getElementId());

            ITreeEntity treeEntity = null;

            if (entity != null) {
                ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
                if (entity instanceof TestCaseEntity) {
                    treeEntity = TreeEntityUtil.getTestCaseTreeEntity((TestCaseEntity) entity, projectEntity);
                } else if (entity instanceof WebElementEntity) {
                    treeEntity = TreeEntityUtil.getWebElementTreeEntity((WebElementEntity) entity, projectEntity);
                } else if (entity instanceof TestSuiteEntity) {
                    treeEntity = TreeEntityUtil.getTestSuiteTreeEntity((TestSuiteEntity) entity, projectEntity);
                } else if (entity instanceof DataFileEntity) {
                    treeEntity = TreeEntityUtil.getTestDataTreeEntity((DataFileEntity) entity, projectEntity);
                } else if (entity instanceof ReportCollectionEntity) {
                    treeEntity = TreeEntityUtil.getReportCollectionTreeEntity((ReportCollectionEntity) entity,
                            projectEntity);
                } else if (entity instanceof ReportEntity) {
                    treeEntity = TreeEntityUtil.getReportTreeEntity((ReportEntity) entity, projectEntity);
                } else if (entity instanceof TestSuiteCollectionEntity) {
                    treeEntity = TreeEntityUtil.getTestSuiteCollectionTreeEntity((TestSuiteCollectionEntity) entity,
                            projectEntity);
                } else if (entity instanceof CheckpointEntity) {
                    treeEntity = TreeEntityUtil.getCheckpointTreeEntity((CheckpointEntity) entity, projectEntity);
                } else if (entity instanceof ExecutionProfileEntity) {
                    FolderEntity folderEntity = FolderController.getInstance().getProfileRoot(projectEntity);
                    ITreeEntity profileRootFolder = new FolderTreeEntity(folderEntity, null);
                    treeEntity = TreeEntityUtil.getProfileTreeEntity((ExecutionProfileEntity) entity,
                            (FolderEntity) profileRootFolder.getObject());
                }
            } else {
                IEditorPart editorPart = editor.getEditor(mpart);
                if (editorPart == null) {
                    return;
                }
                IEditorInput editorInput = editorPart.getEditorInput();
                switch (getRootFolderName(editorInput)) {
                    case StringConstants.TEST_LISTENERS_FOLDER_NAME:
                        treeEntity = getListenerTreeEntity(editorInput);
                        break;
                    case StringConstants.KEYWORD_FOLDER_NAME:
                        treeEntity = getKeywordTreeEntity(editorPart);
                        break;
                    case StringConstants.INCLUDE_FOLDER_NAME:
                        String[] pathSegments = getRelativePath(editorInput).split("/");

                        if (pathSegments.length > 2) {
                            String firstChildrentFolderName = pathSegments[1];
                            if (firstChildrentFolderName.equals(StringConstants.SCRIPTS_FOLDER_NAME)) {
                                treeEntity = getKeywordTreeEntity(editorPart);
                            } else {
                                treeEntity = getSystemFileRootEntity(editorInput);
                            }
                        } else {
                            treeEntity = getSystemFileRootEntity(editorInput);
                        }
                        break;
                    default:
                        treeEntity = getUserFileTreeRootEntity(editorInput);
                }
            }
            if (treeEntity == null) {
                return;
            }
            eventBroker.post(EventConstants.EXPLORER_SHOW_ITEM, treeEntity);
        } catch (Exception e) {
            logError(e);
        }
    }

    private KeywordTreeEntity getKeywordTreeEntity(IEditorPart editorPart) throws Exception {
        if (editorPart != null) {
            IJavaElement elem = JavaUI.getEditorInputJavaElement(editorPart.getEditorInput());
            if (elem instanceof GroovyCompilationUnit && elem.getParent() instanceof IPackageFragment) {
                ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance()
                        .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);

                ITreeEntity newPackageTreeEntity = new PackageTreeEntity((IPackageFragment) elem.getParent(),
                        keywordRootFolder);

                return new KeywordTreeEntity((ICompilationUnit) elem, newPackageTreeEntity);
            }
        }
        return null;
    }

    private TestListenerTreeEntity getListenerTreeEntity(IEditorInput editorInput) throws Exception {
        FolderEntity folderEntity = FolderController.getInstance()
                .getTestListenerRoot(ProjectController.getInstance().getCurrentProject());
        ITreeEntity testListenerRootFolder = new FolderTreeEntity(folderEntity, null);
        String[] fileName = editorInput.getName().split("\\.");
        TestListenerEntity testListenerEntity = TestListenerController.getInstance().getTestListener(fileName[0],
                folderEntity);
        return TreeEntityUtil.getTestListenerTreeEntity(testListenerEntity,
                (FolderEntity) testListenerRootFolder.getObject());
    }

    private SystemFileTreeEntity getSystemFileRootEntity(IEditorInput editorInput) throws Exception {
        String filePath = getFilePath(editorInput);
        SystemFileEntity systemFileEntity = SystemFileController.getInstance().getSystemFile(filePath,
                ProjectController.getInstance().getCurrentProject());
        return TreeEntityUtil.getSystemFileTreeEntity(systemFileEntity, systemFileEntity.getParentFolder());
    }

    private UserFileTreeEntity getUserFileTreeRootEntity(IEditorInput editorInput) throws Exception {
        String filePath = getFilePath(editorInput);
        UserFileEntity userFileEntity = UserFileController.getInstance().getUserFileEntity(filePath,
                ProjectController.getInstance().getCurrentProject());
        return new UserFileTreeEntity(userFileEntity, null);
    }

    private String getFilePath(IEditorInput editorInput) {
        String filePath;
        if (editorInput instanceof FileStoreEditorInput) {
            IPath path = Path.fromOSString(((FileStoreEditorInput) editorInput).getURI().getPath().toString());
            filePath = path.toOSString();
        } else filePath = ((FileEditorInput) editorInput).getPath().toOSString();
        return filePath;
    }

    private String getRelativePath(IEditorInput editorInput) {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        String relativePath = getFilePath(editorInput).substring(projectEntity.getFolderLocation().length() + 1)
                .replace("\\", "/");
        return relativePath;
    }

    private String getRootFolderName(IEditorInput editorInput) {
        String relativePath = getRelativePath(editorInput);
        String[] pathSegments = relativePath.split("/");
        if (pathSegments.length == 1) {
            return StringUtils.EMPTY;
        }
        return pathSegments[0];
    }
}
