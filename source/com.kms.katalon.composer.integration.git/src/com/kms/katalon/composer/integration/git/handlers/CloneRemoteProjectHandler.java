package com.kms.katalon.composer.integration.git.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.editors.MarkdownPart;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.composer.project.dialog.ProjectChoosingDialog;
import com.kms.katalon.composer.project.handlers.NewProjectHandler;
import com.kms.katalon.composer.project.handlers.OpenProjectHandler;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.tracking.service.Trackings;


@SuppressWarnings("restriction")
public class CloneRemoteProjectHandler {
    private static final int FETCH_TIMEOUT_IN_MILLIS = 15000;

    private static final String MARKDOWN_PART_URI = "bundleclass://"
            + FrameworkUtil.getBundle(MarkdownPart.class).getSymbolicName() + "/" + MarkdownPart.class.getName();

    private boolean shouldHandleProjectOpenAfterClone = false;

    private File destinationFolder = null;
    
    private ProjectType projectType;

    private SampleRemoteProject sample;


    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @PostConstruct
    public void registerEventHandler() {
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.GIT_CLONE_REMOTE_PROJECT,
                new EventServiceAdapter() {

                    @Override
                    public void handleEvent(Event event) {
                        Object[] objects = getObjects(event);


                        sample = (SampleRemoteProject) objects[0];
                        String projectLocation = ((ProjectEntity) objects[1]).getLocation();
                        projectType = ((ProjectEntity) objects[1]).getType();

                        File workdir = new File(projectLocation);
                        workdir.mkdirs();

                        Job job = new Job("Cloning remote project") {
                            @Override
                            protected IStatus run(IProgressMonitor monitor) {

                                try {
                                    monitor.beginTask("Cloning remote project...", 100);

                                    URIish uri = new URIish(sample.getSourceUrl());
                                    final Repository db = FileRepositoryBuilder.create(new File("/tmp")); //$NON-NLS-1$
                                    Collection<Ref> refs = new ArrayList<>();
                                    try (Git git = new Git(db)) {
                                        LsRemoteCommand rc = git.lsRemote();
                                        rc.setRemote(uri.toString()).setTimeout(FETCH_TIMEOUT_IN_MILLIS);
                                        try {
                                            refs = rc.call();
                                        } catch (GitAPIException e) {
                                            return Status.CANCEL_STATUS;
                                        }
                                    }
                                    monitor.worked(30);
                                    CloneOperation cloneOp = new CloneOperation(uri, false, refs, workdir, sample.getDefaultBranch(),
                                            "origin", FETCH_TIMEOUT_IN_MILLIS);
                                    cloneOp.run(monitor);

                                    return Status.OK_STATUS;
                                } catch (InvocationTargetException | InterruptedException | URISyntaxException
                                        | IOException e) {
                                    return Status.CANCEL_STATUS;
                                } finally {
                                    monitor.done();
                                }
                            }
                        };
                        job.setUser(true);
                        job.addJobChangeListener(new JobChangeAdapter() {
                            @Override
                            public void done(IJobChangeEvent event) {
                                destinationFolder = workdir;
                                Thread thread = new Thread(() -> {
                                    try {
                                        Thread.sleep(1000L);
                                    } catch (InterruptedException ignored) {}
                                    UISynchronizeService.syncExec(() -> openOrCreateNewProjectAtDestination());
                                });
                                thread.start();
                            }
                        });
                        job.schedule();
                    }
                });
    }

    @Inject
    @Optional
    private void gitCloneSuccessEventHandler(@UIEventTopic(EventConstants.PROJECT_OPENED) final Object object)
            throws InvocationTargetException, InterruptedException {
        if (!shouldHandleProjectOpenAfterClone) {
            return;
        }

        openReadme(this.destinationFolder);

        shouldHandleProjectOpenAfterClone = false;
        destinationFolder = null;
    }

    private void openOrCreateNewProjectAtDestination() {
        File projectFile = OpenProjectHandler.getProjectFile(destinationFolder);
        if (projectFile == null) {
            List<File> nestedProjectFiles = OpenProjectHandler.getProjectFiles(destinationFolder);
            if (nestedProjectFiles.size() > 0) {
                ProjectChoosingDialog dialog = new ProjectChoosingDialog(Display.getCurrent().getActiveShell(),
                        destinationFolder, nestedProjectFiles);
                if (dialog.open() == ProjectChoosingDialog.OK) {
                    projectFile = dialog.getSelectedProjectFile();
                } else {
                    return;
                }
            } else {
                try {
                    shouldHandleProjectOpenAfterClone = true;
                    ProjectEntity newProject = NewProjectHandler.createNewProject(destinationFolder.getName(),
                            destinationFolder.getParentFile().getAbsolutePath(), "");
                    ShareProjectHandler.addDefaultIgnores(newProject.getFolderLocation());
                    projectFile = new File(newProject.getLocation());
                    EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.PROJECT_CREATED,
                            newProject);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), GitStringConstants.ERROR,
                            GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
                    return;
                }
            }
        }

        try {
            shouldHandleProjectOpenAfterClone = true;
            
            ProjectEntity project = updateProject(projectFile);
            
            Trackings.trackCreatingSampleProject(sample.getName(), project.getUUID(), projectType);
            EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.PROJECT_CREATED,
            		project);
            OpenProjectHandler.doOpenProject(null, projectFile.getAbsolutePath(),

                    UISynchronizeService.getInstance().getSync(), EventBrokerSingleton.getInstance().getEventBroker(),
                    PartServiceSingleton.getInstance().getPartService(),
                    ModelServiceSingleton.getInstance().getModelService(),
                    ApplicationSingleton.getInstance().getApplication());
            
            TimeUnit.SECONDS.sleep(1);
            if (projectType == ProjectType.WEBSERVICE) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.API_QUICK_START_DIALOG_OPEN, null);
            }
            if (projectType == ProjectType.WEBUI) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.API_QUICK_START_WEB_DIALOG_OPEN, null);
            }
            if (projectType == ProjectType.MOBILE) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.API_QUICK_START_MOBILE_DIALOG_OPEN, null);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), GitStringConstants.ERROR,
                    GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
        }
        return;
    }

    private ProjectEntity updateProject(File projectFile) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getProject(projectFile.getAbsolutePath());
        project.setUUID(Util.generateGuid());
        project.setType(projectType);
        project.setFolderLocation(destinationFolder.getAbsolutePath());
        ProjectController.getInstance().updateProject(project);
        return project;
    }
    
    public void openReadme(File repoLocation) {
        File readme = new File(repoLocation, "README.md");
        if (!readme.exists()) {
            return;
        }
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);

        String partId = readme.getAbsolutePath();
        MPart mPart = (MPart) modelService.find(partId, application);
        mPart = modelService.createModelElement(MPart.class);
        mPart.setElementId(partId);
        mPart.setLabel(readme.getName());
        mPart.setIconURI(ImageManager.getImageURLString(IImageKeys.TXT_TEST_OBJECT_16));
        mPart.setContributionURI(MARKDOWN_PART_URI);
        mPart.setTooltip(readme.getAbsolutePath());
        mPart.setCloseable(true);
        stack.getChildren().add(mPart);

        if (mPart.getObject() == null) {
            mPart.setObject(readme);
        }

        partService.showPart(mPart, PartState.ACTIVATE);
        stack.setSelectedElement(mPart);
    }
}