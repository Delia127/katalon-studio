package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.ExplorerPreferenceConstants;
import com.kms.katalon.composer.util.groovy.editor;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
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
    private EPartService partService;

    @Inject
    private IEventBroker eventBroker;

    private ScopedPreferenceStore store;

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
            if (partService.getActivePart() != null) {
                performLinkWithPart(partService.getActivePart());
            }
        } else {
            setActive(false);
        }
    }

    @Override
    public void handleEvent(Event event) {
        Object object = event.getProperty(EventTags.ELEMENT);
        if (getActive() && UIEvents.UILifeCycle.BRINGTOTOP.equals(event.getTopic()) && (object != null)
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
                    treeEntity = TreeEntityUtil.getReportCollectionTreeEntity((ReportCollectionEntity) entity, projectEntity);
                } else if (entity instanceof ReportEntity) {
                    treeEntity = TreeEntityUtil.getReportTreeEntity((ReportEntity) entity, projectEntity);
                } else if (entity instanceof TestSuiteCollectionEntity) {
                    treeEntity = TreeEntityUtil.getTestSuiteCollectionTreeEntity((TestSuiteCollectionEntity) entity, projectEntity);
                } else if (entity instanceof CheckpointEntity) {
                    treeEntity = TreeEntityUtil.getCheckpointTreeEntity((CheckpointEntity) entity);
                }
            } else {
                treeEntity = getKeywordTreeEntity(mpart);
            }
            if (treeEntity == null) {
                return;
            }
            eventBroker.post(EventConstants.EXPLORER_SHOW_ITEM, treeEntity);
        } catch (Exception e) {
            logError(e);
        }
    }

    private KeywordTreeEntity getKeywordTreeEntity(MPart mpart) throws Exception {
        IEditorPart editorPart = editor.getEditor(mpart);
        if (editorPart != null) {
            IJavaElement elem = JavaUI.getEditorInputJavaElement(editorPart.getEditorInput());
            if (elem instanceof GroovyCompilationUnit && elem.getParent() instanceof IPackageFragment) {
                ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance().getKeywordRoot(
                        ProjectController.getInstance().getCurrentProject()), null);

                ITreeEntity newPackageTreeEntity = new PackageTreeEntity((IPackageFragment) elem.getParent(),
                        keywordRootFolder);

                return new KeywordTreeEntity((ICompilationUnit) elem, newPackageTreeEntity);
            }
        }
        return null;
    }
}
