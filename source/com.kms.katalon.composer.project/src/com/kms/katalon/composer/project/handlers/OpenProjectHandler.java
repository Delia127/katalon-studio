package com.kms.katalon.composer.project.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class OpenProjectHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication app;

    @Inject
    private EPartService partService;

    @Inject
    private MApplication application;

    @Inject
    private UISynchronize sync;

    @Execute
    public void execute(Shell shell) {
        try {
            DirectoryDialog directoryDialog = new DirectoryDialog(shell);
            directoryDialog.open();

            if (directoryDialog.getFilterPath() != null) {
                File projectDirectory = new File(directoryDialog.getFilterPath());
                if (projectDirectory != null && projectDirectory.exists() && projectDirectory.isDirectory()) {
                    File projectFile = null;
                    for (File file : projectDirectory.listFiles()) {
                        if (('.' + FilenameUtils.getExtension(file.getAbsolutePath())).equals(ProjectEntity
                                .getProjectFileExtension())) {
                            projectFile = file;
                            break;
                        }
                    }

                    if (projectFile != null) {
                        if (!CloseProjectHandler.closeCurrentProject(partService, modelService, application,
                                eventBroker)) return;
                        openProjectEventHandler(projectFile.getAbsolutePath());
                    } else {
                        MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                                StringConstants.HAND_WARN_MSG_NO_PROJ_FOUND);
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_PROJ);
        }

    }

    @Inject
    @Optional
    private void openProjectEventHandler(@UIEventTopic(EventConstants.PROJECT_OPEN) final String projectPk) {
        Job job = new Job(StringConstants.HAND_OPEN_PROJ) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask(StringConstants.HAND_OPENING_PROJ, 10);
                    monitor.worked(1);
                    monitor.subTask(StringConstants.HAND_LOADING_PROJ);
                    final ProjectEntity project = ProjectController.getInstance().openProjectForUI(projectPk,
                            new SubProgressMonitor(monitor, 7, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
                    monitor.subTask(StringConstants.HAND_REFRESHING_EXPLORER);
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<ITreeEntity> treeEntities = new ArrayList<ITreeEntity>();
                                if (project != null) {
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance()
                                            .getTestCaseRoot(project), null));
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance()
                                            .getObjectRepositoryRoot(project), null));
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance()
                                            .getTestSuiteRoot(project), null));
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance()
                                            .getTestDataRoot(project), null));
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance()
                                            .getKeywordRoot(project), null));
                                    treeEntities.add(new FolderTreeEntity(FolderController.getInstance().getReportRoot(
                                            project), null));

                                    // Set project name on window title
                                    OpenProjectHandler.updateProjectTitle(project, modelService, app);
                                }
                                eventBroker.post(EventConstants.EXPLORER_RELOAD_INPUT, treeEntities);
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    });
                    eventBroker.post(EventConstants.GLOBAL_VARIABLE_REFRESH, null);
                    monitor.worked(1);
                    LauncherManager.refresh();
                    eventBroker.post(EventConstants.JOB_REFRESH, null);
                    monitor.worked(1);

                    TimeUnit.SECONDS.sleep(1);
                    eventBroker.send(EventConstants.PROJECT_OPENED, null);
                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    sync.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_PROJ,
                                    e.getClass().getSimpleName());
                        }
                    });
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        if (CloseProjectHandler.closeCurrentProject(partService, modelService, application, eventBroker)) {
            job.setUser(true);
            job.schedule();
        }
    }

    public static void updateProjectTitle(ProjectEntity projectEntity, EModelService modelService, MApplication app) {
        MWindow win = (MWindow) modelService.find(IdConstants.MAIN_WINDOW_ID, app);
        if (win != null) {
            win.setLabel(win.getLabel().split(" - ")[0] + " - " + projectEntity.getName() + " - [Location: "
                    + projectEntity.getFolderLocation() + "]");
            win.updateLocalization();
        }
    }
}
