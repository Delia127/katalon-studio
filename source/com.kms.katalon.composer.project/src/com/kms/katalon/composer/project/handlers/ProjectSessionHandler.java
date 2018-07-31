package com.kms.katalon.composer.project.handlers;

import static com.kms.katalon.composer.components.impl.util.EntityPartUtil.getOpenedEntityIds;
import static com.kms.katalon.composer.components.impl.util.TreeEntityUtil.getTreeEntityIds;
import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.project.constants.ProjectPreferenceConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

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

    public static ScopedPreferenceStore getGeneralStore() {
        return getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }

    @CanExecute
    public boolean canExecute() {
        return getGeneralStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION);
    }
    
    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.PROJECT_SAVE_SESSION, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                try {
                    if (!canExecute()) {
                        return;
                    }
                    String[] expandedEntities = rememberExpandedTreeEntities();
                    String[] openedEntities = rememberOpenedEntities();
                    saveSessionEntities(openedEntities, expandedEntities);
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

    @Inject
    @Execute
    public void execute() {
        
    }

    private void restoreExpandedTreeEntities() throws Exception {
        CTreeViewer viewer = getTreeViewer(getTestExplorerPart());
        if (viewer == null) {
            return;
        }

        viewer.getControl().setRedraw(false);

        LastSessionEntities sessionEntities = getSessionEntities();
        Object[] expandedEntities = TreeEntityUtil
                .getExpandedTreeEntitiesFromIds(Arrays.asList(sessionEntities.getExpandedEntities())).toArray();
        for (Object expanded : expandedEntities) {
            if (expanded != null) {
                viewer.setExpandedState(expanded, true);
            }
        }
        viewer.getControl().setRedraw(true);
    }

    private void restoreOpenedEntities() throws Exception {
        Job job = new Job("Restoring Previous Session") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    LastSessionEntities sessionEntities = getSessionEntities();
                    List<ITreeEntity> treeEntities = TreeEntityUtil
                            .getOpenedTreeEntitiesFromIds(
                                    Arrays.asList(sessionEntities.getOpenedEntities()));

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
                    eventBroker.post(EventConstants.PROJECT_RESTORE_SESSION_COMPLETED, null);
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private String[] rememberExpandedTreeEntities() throws Exception {
        CTreeViewer viewer = getTreeViewer(getTestExplorerPart());
        if (viewer == null) {
            return new String[0];
        }
        List<String> expandedTreeEntityIds = getTreeEntityIds(viewer.getExpandedElements());
        return expandedTreeEntityIds.toArray(new String[0]);
    }

    private String[] rememberOpenedEntities() throws Exception {
        List<String> openedEntityIds = getOpenedEntityIds(partService.getParts());
        return openedEntityIds.toArray(new String[0]);
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

    private static class LastSessionEntities {
        private String[] openedEntities;

        private String[] expandedEntities;

        public static LastSessionEntities empty() {
            LastSessionEntities sessionEntities = new LastSessionEntities();
            sessionEntities.openedEntities = new String[0];
            sessionEntities.expandedEntities = new String[0];
            return sessionEntities;
        }

        public String[] getOpenedEntities() {
            if (openedEntities == null) {
                openedEntities = new String[0];
            }
            return openedEntities;
        }

        public String[] getExpandedEntities() {
            if (expandedEntities == null) {
                expandedEntities = new String[0];
            }
            return expandedEntities;
        }
    }

    private LastSessionEntities getSessionEntities() {
        IPreferenceStore store = PreferenceStoreManager.getPreferenceStore(getClass());
        String lastSessionEntities = store.getString(ProjectPreferenceConstants.LATEST_SESSION_ENTITIES);
        if (StringUtils.isEmpty(lastSessionEntities)) {
            return LastSessionEntities.empty();
        }
        return JsonUtil.fromJson(lastSessionEntities, LastSessionEntities.class);
    }

    private void saveSessionEntities(String[] openedEntities, String[] expandedEntities) throws IOException {
        LastSessionEntities sessionEntities = new LastSessionEntities();
        sessionEntities.openedEntities = openedEntities;
        sessionEntities.expandedEntities = expandedEntities;
        IPreferenceStore store = PreferenceStoreManager.getPreferenceStore(getClass());
        store.setValue(ProjectPreferenceConstants.LATEST_SESSION_ENTITIES, JsonUtil.toJson(sessionEntities));
        ((IPersistentPreferenceStore) store).save();
    }
}
