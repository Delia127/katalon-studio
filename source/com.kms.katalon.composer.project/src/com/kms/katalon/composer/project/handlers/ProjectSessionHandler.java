package com.kms.katalon.composer.project.handlers;

import static com.kms.katalon.composer.components.impl.util.EntityPartUtil.getOpenedEntityIds;
import static com.kms.katalon.composer.components.impl.util.TreeEntityUtil.getTreeEntityIds;
import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static org.eclipse.ui.PlatformUI.getPreferenceStore;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectSessionHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    private UISynchronize sync;

    @CanExecute
    public boolean canExecute() {
        return getPreferenceStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION);
    }

    @Inject
    @Execute
    public void execute() {
        eventBroker.subscribe(EventConstants.PROJECT_SAVE_SESSION, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                try {
                    if (!canExecute()) {
                        return;
                    }
                    rememberExpandedTreeEntities();
                    rememberOpenedEntities();
                } catch (Exception e) {
                    logError(e);
                }
            }
        });

        eventBroker.subscribe(EventConstants.PROJECT_RESTORE_SESSION, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                try {
                    if (!canExecute()) {
                        return;
                    }
                    restoreExpandedTreeEntities();
                    restoreOpenedEntities();
                } catch (Exception e) {
                    logError(e);
                }
            }
        });
    }

    private void restoreExpandedTreeEntities() throws Exception {
        CTreeViewer viewer = getTreeViewer(getTestExplorerPart());
        if (viewer == null) {
            return;
        }

        viewer.getControl().setRedraw(false);

        Object[] expandedEntities = TreeEntityUtil
                .getExpandedTreeEntitiesFromIds(getProject().getRecentExpandedTreeEntityIds()).toArray();
        for (Object expanded : expandedEntities) {
            if (expanded != null) {
                viewer.setExpandedState(expanded, true);
            }
        }
        viewer.getControl().setRedraw(true);
    }

    private void restoreOpenedEntities() throws Exception {
        MPart testExplorerPart = getTestExplorerPart();
        if (testExplorerPart == null) {
            return;
        }

        // Need to activate ExplorerPart before open any entity
        partService.activate(testExplorerPart);

        Job job = new Job("Restoring Previous Session") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    List<ITreeEntity> treeEntities = TreeEntityUtil
                            .getOpenedTreeEntitiesFromIds(getProject().getRecentOpenedTreeEntityIds());

                    monitor.beginTask("Restoring Previous Session...", treeEntities.size());
                    for (ITreeEntity entity : treeEntities) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        if (entity != null && entity.getObject() != null) {
                            sync.syncExec(() -> {
                                try {
                                    eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, entity.getObject());
                                } catch (Exception ex) {
                                    LoggerSingleton.logError(ex);
                                }
                            });
                        }
                        monitor.worked(1);
                    }
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void rememberExpandedTreeEntities() throws Exception {
        CTreeViewer viewer = getTreeViewer(getTestExplorerPart());
        if (viewer == null) {
            return;
        }
        List<String> expandedTreeEntityIds = getTreeEntityIds(viewer.getExpandedElements());
        ProjectController.getInstance().keepStateOfExpandedTreeEntities(expandedTreeEntityIds);
    }

    private void rememberOpenedEntities() throws Exception {
        List<String> openedEntityIds = getOpenedEntityIds(partService.getParts());
        ProjectController.getInstance().keepStateOfOpenedEntities(openedEntityIds);
    }

    private static ProjectEntity getProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    private MPart getTestExplorerPart() {
        return (MPart) modelService.find(IdConstants.EXPLORER_PART_ID, application);
    }

    private CTreeViewer getTreeViewer(MPart testExplorerPart) {
        Object treeViewer = testExplorerPart.getTransientData().get(CTreeViewer.class.getSimpleName());
        if (treeViewer instanceof CTreeViewer) {
            return (CTreeViewer) treeViewer;
        }
        return null;
    }

}
