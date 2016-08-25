package com.kms.katalon.composer.project.views;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.kms.katalon.composer.project.constants.StringConstants;

public class NewProjectWizard extends Wizard {

    private NewEmptyProjectPage newEmptyProjectPage;

    private NewTemplateProjectPage newTemplateProjectPage;

    private String projectName;

    private String projectLocation;

    private String projectDescription;

    private List<String> selectedTemplates;

    public NewProjectWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public String getWindowTitle() {
        return StringConstants.VIEW_TITLE_NEW_PROJ;
    }

    @Override
    public void addPages() {
        newEmptyProjectPage = new NewEmptyProjectPage();
        newTemplateProjectPage = new NewTemplateProjectPage();
        addPage(newEmptyProjectPage);
        addPage(newTemplateProjectPage);
    }

    @Override
    public boolean performFinish() {
        projectName = newEmptyProjectPage.getProjectName();
        projectLocation = newEmptyProjectPage.getProjectLocation();
        projectDescription = newEmptyProjectPage.getProjectDescription();
        selectedTemplates = newTemplateProjectPage.getSelectedTemplates();
        return true;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public List<String> getSelectedTemplates() {
        return selectedTemplates;
    }
}
