package com.kms.katalon.composer.integration.git.handlers;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.egit.core.GitCorePreferences;
import org.eclipse.egit.core.internal.job.JobUtil;
import org.eclipse.egit.core.internal.util.ResourceUtil;
import org.eclipse.egit.core.op.DisconnectProviderOperation;
import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.decorators.GitLightweightDecorator;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.git.preference.GitPreferenceUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class DisconnectProjectHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventHandler() {
            
            @Override
            public void handleEvent(Event event) {
                try {
                    ScopedPreferenceStore egitStore = PreferenceStoreManager.getPreferenceStore("org.eclipse.egit.core");
                    if (!egitStore.getBoolean(GitCorePreferences.core_autoShareProjects)) {
                        egitStore.setValue(GitCorePreferences.core_autoShareProjects, false);
                    }
                    egitStore.save();
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        });

        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                IProject groovyProject = GroovyUtil.getGroovyProject(project);
                if (!GitPreferenceUtil.isGitEnabled() && ResourceUtil.getGitProvider(groovyProject) != null) {
                    JobUtil.scheduleUserJob(new DisconnectProviderOperation(Arrays.asList(groovyProject)),
                            UIText.Disconnect_disconnect, JobFamilies.DISCONNECT, new JobChangeAdapter() {
                                @Override
                                public void done(IJobChangeEvent actEvent) {
                                    GitLightweightDecorator.refresh();
                                }
                            });
                }
            }
        });
    }
}
