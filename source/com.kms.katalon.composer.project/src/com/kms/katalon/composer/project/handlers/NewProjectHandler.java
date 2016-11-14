package com.kms.katalon.composer.project.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.MarshalException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.template.TemplateProjectGenerator;
import com.kms.katalon.composer.project.views.NewProjectWizard;
import com.kms.katalon.composer.project.views.NewProjectWizardDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

@SuppressWarnings("restriction")
public class NewProjectHandler {
    public static final String TEMPL_CUSTOM_KW_PKG_REL_PATH = "Keywords/com/example";
    
    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(Shell shell) {
        NewProjectWizard wizard = new NewProjectWizard();
        NewProjectWizardDialog wizardDialog = new NewProjectWizardDialog(shell, wizard);
        if (wizardDialog.open() != WizardDialog.OK) {
            return;
        }
        try {
            String projectName = wizard.getProjectName();
            String projectLocation = wizard.getProjectLocation();
            String projectDescription = wizard.getProjectDescription();
            String projectId = "";
            boolean isTemplateSelected = false;
            if (wizardDialog.getCurrentPage() == wizard.getPage(StringConstants.VIEW_TESTING_TYPES_PROJECT_PAGE_NAME)) {
                // Copy template project if user selected any template option
                List<String> selectedTemplates = wizard.getSelectedTemplates();
                if (selectedTemplates != null && selectedTemplates.size() > 0) {
                    isTemplateSelected = true;
                    new TemplateProjectGenerator(projectLocation, projectName).copyTemplates(selectedTemplates);
                }
            }
            //If user select to create new blank project, and no template selected
            if(!isTemplateSelected){
                ProjectEntity newProject = createNewProject(projectName, projectLocation, projectDescription);
                if (newProject == null) {
                    return;
                }
                eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
                projectId = newProject.getId();
            }
            else{
                projectId = projectLocation + File.separator + projectName + File.separator + projectName + ".prj";
            }

            // Open created project
            eventBroker.send(EventConstants.PROJECT_OPEN, projectId);

            LauncherManager.refresh();
            eventBroker.post(EventConstants.JOB_REFRESH, null);
            eventBroker.post(EventConstants.CONSOLE_LOG_REFRESH, null);

            //If user select to generate templates, 
            //extract template project, re-name and open it, 
            //no need to create new to improve performance
            if(isTemplateSelected){
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                PackageTreeEntity pack = TreeEntityUtil.getPackageTreeEntity(TEMPL_CUSTOM_KW_PKG_REL_PATH, currentProject);
                eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, pack);                 
            }
        } catch (FilePathTooLongException ex) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, ex.getMessage());
        } catch (Exception ex) {            
            LoggerSingleton.getInstance().getLogger().error(ex);
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_PROJ);
        }
    }

    public static ProjectEntity createNewProject(String projectName, String projectLocation,
            String projectDescription) throws Exception {
        try {
            return ProjectController.getInstance().addNewProject(projectName, projectDescription, projectLocation);
        } catch (MarshalException ex) {
            if (!(ex.getLinkedException() instanceof FileNotFoundException)) {
                throw ex;
            }
            LoggerSingleton.getInstance().getLogger().error(ex);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_NEW_PROJ_LOCATION_INVALID);
        }
        return null;
    }
}
