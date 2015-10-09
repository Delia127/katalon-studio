package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class OpenKeywordHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ICompilationUnit
                        && ((ICompilationUnit) object).getElementName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    excute((ICompilationUnit) object);
                }
            }
        });
    }

    /**
     * Open a custom keyword file and validate that file after user save it
     * 
     * @param keywordFile
     */
    private void excute(ICompilationUnit keywordFile) {
        if (keywordFile != null && keywordFile.exists()) {
            try {
                if (!keywordFile.isWorkingCopy()) {
                    keywordFile.becomeWorkingCopy(null);
                }
                
                IEditorPart keywordPath = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage(), (IFile) keywordFile.getResource());
                
                keywordPath.addPropertyListener(new IPropertyListener() {

                    @Override
                    public void propertyChanged(Object source, int propId) {
                        if (source instanceof ISaveablePart && propId == ISaveablePart.PROP_DIRTY) {
                            try {
                                if (!((ISaveablePart) source).isDirty()) {
                                    final ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                                    final IFile file = ((FileEditorInput) ((IEditorPart) source).getEditorInput())
                                            .getFile();
                                    Job job = new Job(StringConstants.HAND_COLLECTING_CUSTOM_KEYWORD) {

                                        @Override
                                        protected IStatus run(IProgressMonitor monitor) {
                                            try {
                                                KeywordController.getInstance().parseCustomKeywordFile(file, project);
                                            } catch (Exception e) {
                                                LoggerSingleton.getInstance().getLogger().error(e);
                                                return Status.CANCEL_STATUS;
                                            }
                                            return Status.OK_STATUS;
                                        }

                                    };
                                    job.schedule();
                                }
                            } catch (Exception e) {
                                LoggerSingleton.getInstance().getLogger().error(e);
                            }

                        }
                    }
                });

            } catch (Exception e) {
                LoggerSingleton.getInstance().getLogger().error(e);
                MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                        StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_KEYWORD_FILE);
            }
        }
    }
}
