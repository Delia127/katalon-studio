package com.kms.katalon.composer.project.dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.MarshalException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.sample.SampleProject;
import com.kms.katalon.composer.project.sample.SampleProjectType;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.tracking.service.Trackings;

public class NewProjectDialog extends TitleAreaDialog {
    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private static final int PROJECT_DESC_TEXT_LEFT_MARGIN = 5;

    private static final int PROJECT_DESC_DISPLAY_LINE_NUMBER = 4;

    private static final String BLANK_PROJECT = StringConstants.BLANK_PROJECT;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private List<SampleProject> sampleProjects = new ArrayList<>();

    private Text txtProjectName;

    private Text txtProjectLocation;

    private StyledText txtProjectDescription;

    private String name, loc, desc;

    private ProjectEntity project;

    private SampleProject initialSampleProject;

    private boolean showError;

    private Button btnFolderChooser;

    private String title;

    private Combo cboProjects;

    private Text txtRepoUrl;

    private Button rbWebServiceProjectType;

    private Button rbGenericProjectType;

    private Button rbWebProjectType;

    private Button rbMobileProjectType;
    
    private Button cbGenerateGitignoreFile;
    
    private Button cbGenerateGradleFile;

    private boolean okButtonClicked = false;

    private GridData gdGenericProjectType;

    private Composite container;

    public NewProjectDialog(Shell parentShell) {
        this(parentShell, (SampleRemoteProject) null);
    }

    public NewProjectDialog(Shell parentShell, SampleProject sampleProject) {
        super(parentShell);
        this.title = StringConstants.VIEW_TITLE_NEW_PROJ;
        this.initialSampleProject = sampleProject;

    }

    public NewProjectDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        this.project = project;
        this.title = StringConstants.VIEW_TITLE_PROJECT_PROPERTIES;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        initSampleProjects();

        Composite area = (Composite) super.createDialogArea(parent);

        getShell().setText(title);
        setTitle(title);
        setMessage(StringConstants.VIEW_MSG_PLS_ENTER_PROJ_INFO);

        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout g = new GridLayout(2, false);
        container.setLayout(g);
        g.verticalSpacing = 15;
        g.horizontalSpacing = 15;
        g.marginHeight = 15;
        g.marginBottom = 15;
        g.marginLeft = 20;
        g.marginRight = 20;

        Label l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_NAME);

        txtProjectName = new Text(container, SWT.BORDER);
        txtProjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblProjectType = new Label(container, SWT.NONE);
        lblProjectType.setText("Type");

        Composite projectTypeComposite = new Composite(container, SWT.NONE);
        projectTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        projectTypeComposite.setLayout(new GridLayout(5, false));

        // WEB
        rbWebProjectType = new Button(projectTypeComposite, SWT.RADIO);
        GridData WebProjectType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rbWebProjectType.setLayoutData(WebProjectType);
        rbWebProjectType.setText(StringConstants.VIEW_OPTION_WEB_PROJECT);
        rbWebProjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                populateWebProjects();
            }
        });

        // WEBSERVICE
        rbWebServiceProjectType = new Button(projectTypeComposite, SWT.RADIO);
        GridData gdWebServiceProjectType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rbWebServiceProjectType.setLayoutData(gdWebServiceProjectType);
        rbWebServiceProjectType.setText(StringConstants.VIEW_OPTION_WEB_SERVICE_PROJECT);
        rbWebServiceProjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                populateWebServiceProjects();
            }
        });

        // MOBILE
        rbMobileProjectType = new Button(projectTypeComposite, SWT.RADIO);
        GridData gdMobileProjectType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rbMobileProjectType.setLayoutData(gdMobileProjectType);
        rbMobileProjectType.setText(StringConstants.VIEW_OPTION_MOBILE_PROJECT);
        rbMobileProjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                populateMobileProjects();
            }
        });

        // GENERIC
        rbGenericProjectType = new Button(projectTypeComposite, SWT.RADIO);
        gdGenericProjectType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rbGenericProjectType.setLayoutData(gdGenericProjectType);
        rbGenericProjectType.setText(StringConstants.VIEW_OPTION_GENERIC_PROJECT);
        rbGenericProjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                populateGenericProjects();
            }
        });
        // HELP Button
        new HelpCompositeForDialog(projectTypeComposite, DocumentationMessageConstants.MANAGE_TEST_PROJECT) {
            @Override
            protected GridData createGridData() {
                return new GridData(SWT.RIGHT, SWT.CENTER, true, false);
            }

            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginBottom = 0;
                layout.marginWidth = 0;

                return layout;
            }
        };

        Label lblProject = new Label(container, SWT.NONE);
        lblProject.setText(StringConstants.VIEW_LBL_PROJECT);

        cboProjects = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        cboProjects.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblRepoUrl = new Label(container, SWT.NONE);
        lblRepoUrl.setText(StringConstants.VIEW_LBL_REPOSITORY_URL);

        txtRepoUrl = new Text(container, SWT.BORDER);
        txtRepoUrl.setEditable(false);
        txtRepoUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Add
        l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_LOCATION);
        createFileChooserComposite(container);

        l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_DESCRIPTION);

        txtProjectDescription = new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        // increase projectDescription's height on MAC OS so that it is able to show 4 text line
        // and modify the left margin so that it is vertical alignment with project name and project location

        GridData layout = new GridData(GridData.FILL_BOTH);

        GC graphicContext = new GC(txtProjectDescription);
        FontMetrics fm = graphicContext.getFontMetrics();
        layout.heightHint = PROJECT_DESC_DISPLAY_LINE_NUMBER * fm.getHeight();
        txtProjectDescription.setLayoutData(layout);
        graphicContext.dispose();

        txtProjectDescription.setLeftMargin(PROJECT_DESC_TEXT_LEFT_MARGIN);

        if (project != null) {
            txtProjectName.setText(project.getName());
            txtProjectLocation.setText(project.getFolderLocation());
            txtProjectDescription.setText(project.getDescription());
            txtProjectLocation.setEnabled(false);
        }
        
        new Label(container, SWT.NONE);
        
        cbGenerateGitignoreFile = new Button(container, SWT.CHECK);
        cbGenerateGitignoreFile.setText(StringConstants.CB_GENERATE_GITIGNORE_FILE);
        
        new Label(container, SWT.NONE);
        
        cbGenerateGradleFile = new Button(container, SWT.CHECK);
        cbGenerateGradleFile.setText(StringConstants.CB_GENERATE_GRADLE_FILE);

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (initialSampleProject != null) {
            if (initialSampleProject.getType() == SampleProjectType.WEBUI) {

                populateWebProjects();
                rbWebProjectType.setSelection(true);
            } else if (initialSampleProject.getType() == SampleProjectType.MIXED) {
                populateGenericProjects();
                rbGenericProjectType.setSelection(true);
            }

            else if (initialSampleProject.getType() == SampleProjectType.MOBILE) {
                populateMobileProjects();
                rbMobileProjectType.setSelection(true);
            }

            else if (initialSampleProject.getType() == SampleProjectType.WS) {
                populateWebServiceProjects();
                rbWebServiceProjectType.setSelection(true);
            }
            cboProjects.select(cboProjects.indexOf(initialSampleProject.getName()));
        } else {
            populateWebProjects();
            rbWebProjectType.setSelection(true);
            rbWebProjectType.notifyListeners(SWT.Selection, new Event());
            cboProjects.select(0);
        }
        showRepoUrlBySelectedProject();
        enableGenerateGitignoreFileBySelectedProject();
        enableGenerateGradleFileBySelectedProject();

        hideGenericProjectTypeIfNotEnterprise();
        addControlModifyListeners();

        return area;
    }

    private void hideGenericProjectTypeIfNotEnterprise() {
        if (!isEnterpriseAccount()) {
            gdGenericProjectType.exclude = true;
            rbGenericProjectType.setVisible(false);
            container.layout(true);
        }
    }

    private boolean isEnterpriseAccount() {
        return LicenseUtil.isNotFreeLicense();
    }
    
    private void initSampleProjects() {
        List<SampleRemoteProject> remoteSamples = SampleRemoteProjectProvider.getCachedProjects();
        if (remoteSamples.size() > 0) {
            sampleProjects.addAll(remoteSamples);
        } else { // if remote samples are not available, use local ones
            List<SampleLocalProject> localSamples = SampleProjectProvider.getInstance().getSampleProjects();
            sampleProjects.addAll(localSamples);
        }
    }

    private void populateGenericProjects() {
        populateProjects(SampleProjectType.WEBUI, SampleProjectType.MOBILE, SampleProjectType.WS,
                SampleProjectType.MIXED);
    }

    private void populateMobileProjects() {
        populateProjects(SampleProjectType.MOBILE);
    }

    private void populateWebServiceProjects() {
        populateProjects(SampleProjectType.WS);
    }

    private void populateWebProjects() {
        populateProjects(SampleProjectType.WEBUI, SampleProjectType.MIXED);

    }

    private void populateProjects(SampleProjectType... sampleProjectTypes) {
        List<SampleProjectType> sampleProjectTypeList = Arrays.asList(sampleProjectTypes);

        cboProjects.removeAll();

        cboProjects.add(BLANK_PROJECT);

        sampleProjects.stream().filter(sample -> sampleProjectTypeList.contains(sample.getType())).forEach(sample -> {
            cboProjects.add(sample.getName());
            cboProjects.setData(sample.getName(), sample);
        });

        cboProjects.select(0);
        enableGenerateGitignoreFileBySelectedProject();
        enableGenerateGradleFileBySelectedProject();
    }

    private Composite createFileChooserComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout theLayout = new GridLayout((project == null) ? 2 : 1, false);
        theLayout.marginWidth = 0;
        container.setLayout(theLayout);

        txtProjectLocation = new Text(container, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtProjectLocation.setText(DEFAULT_PROJECT_LOCATION);

        if (project == null) {
            btnFolderChooser = new Button(container, SWT.PUSH);
            btnFolderChooser.setText(StringConstants.VIEW_BTN_BROWSE);
        }

        return container;
    }
    
    private void enableGenerateGitignoreFileBySelectedProject() {
        if (isBlankProjectSelected()) {
            cbGenerateGitignoreFile.setEnabled(true);
            cbGenerateGitignoreFile.setSelection(true);
        } else {
            cbGenerateGitignoreFile.setEnabled(false);
            cbGenerateGitignoreFile.setSelection(false);
        }
    }
    
    private void enableGenerateGradleFileBySelectedProject() {
        if (isBlankProjectSelected()) {
            cbGenerateGradleFile.setEnabled(true);
            cbGenerateGradleFile.setSelection(true);
        } else {
            cbGenerateGradleFile.setEnabled(false);
            cbGenerateGradleFile.setSelection(false);
        }
    }
    
    private boolean isBlankProjectSelected() {
        return StringConstants.BLANK_PROJECT.equals(cboProjects.getText());
    }

    private void addControlModifyListeners() {
        txtProjectLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                checkInput();
            }
        });

        txtProjectName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                checkInput();
            }
        });

        if (btnFolderChooser == null) {
            return;
        }
        btnFolderChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(btnFolderChooser.getShell());
                dialog.setFilterPath(getProjectLocationInput());
                String path = dialog.open();
                if (path == null) {
                    return;
                }
                txtProjectLocation.setText(path);
            }
        });

        cboProjects.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showRepoUrlBySelectedProject();
                enableGenerateGitignoreFileBySelectedProject();
                enableGenerateGradleFileBySelectedProject();
            }
        });
    }

    private void showRepoUrlBySelectedProject() {
        txtRepoUrl.setText(StringUtils.EMPTY);

        int selectionIdx = cboProjects.getSelectionIndex();
        String selectedProjectName = cboProjects.getItem(selectionIdx);
        if (!selectedProjectName.equals(BLANK_PROJECT)) {
            Object sampleProject = cboProjects.getData(selectedProjectName);
            if (sampleProject instanceof SampleRemoteProject) {
                txtRepoUrl.setText(((SampleRemoteProject) sampleProject).getSourceUrl());
            }
        }
    }

    @Override
    public void setErrorMessage(String newErrorMessage) {
        if (showError) {
            super.setErrorMessage(newErrorMessage);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        showError = false;
        checkInput();
        showError = true;
    }

    private boolean validateProjectFolderLocation() {

        String projectLocation = getProjectLocationInput();
        if (StringUtils.isBlank(projectLocation)) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_CANNOT_BE_BLANK);
            return false;
        }
        Path folderPath = null;
        try {
            folderPath = Paths.get(projectLocation);
        } catch (InvalidPathException invalidPathException) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_INVALID);
            return false;
        }

        File folderLocation = folderPath.toFile();
        if (!folderLocation.exists()) {
            return true;
        }
        if (!folderLocation.canRead()) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_NOT_READABLE);
            return false;
        }
        if (!folderLocation.canWrite()) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_NOT_WRITEABLE);
            return false;
        }

        try {
            String katalonFolderAbsolutePath = new File(Platform.getInstallLocation().getURL().getFile())
                    .getAbsolutePath();
            String locFileAbsolutePath = new File(projectLocation).getAbsolutePath();
            if (locFileAbsolutePath.startsWith(katalonFolderAbsolutePath)) {
                setErrorMessage(StringConstants.CANNOT_CREATE_PROJECT_IN_KATALON_FOLDER);
                return false;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
        return true;
    }

    private String getProjectLocationInput() {
        if (txtProjectLocation == null || StringUtils.isBlank(txtProjectLocation.getText())) {
            return "";
        }
        String projectLocation = txtProjectLocation.getText().trim();
        if (!projectLocation.contains(File.separator)) {
            projectLocation = DEFAULT_PROJECT_LOCATION + File.separator + projectLocation;
        }
        return projectLocation;
    }

    private boolean validateProjectName() {
        if (StringUtils.isBlank(txtProjectName.getText())) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK);
            return false;
        }
        return true;
    }

    private boolean validateProjectNameDuplication() {
        String projectLocation = getProjectLocationInput();
        String projectName = txtProjectName.getText().trim();
        try {
            if (!ProjectController.getInstance().validateNewProjectName(projectLocation, projectName)) {
                setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_EXISTED_IN_LOC);
                return false;
            }
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
        }
        return false;
    }

    private void checkInput() {
        if (okButtonClicked) {
            return;
        }
        setErrorMessage(null);
        getButton(Dialog.OK).setEnabled(
                validateProjectFolderLocation() && validateProjectName() && validateProjectNameDuplication());
    }

    @Override
    protected void okPressed() {
        okButtonClicked = true;

        name = txtProjectName.getText();
        loc = getProjectLocationInput();
        desc = txtProjectDescription.getText();

        int selectionIdx = cboProjects.getSelectionIndex();
        String selectedProjectName = cboProjects.getItem(selectionIdx);
        Object selectedSampleProject = cboProjects.getData(selectedProjectName);
        if (!selectedProjectName.equals(BLANK_PROJECT)) {
            if (selectedSampleProject instanceof SampleRemoteProject) {
                handleCreatingSampleRemoteProject((SampleRemoteProject) selectedSampleProject);
            } else if (selectedSampleProject instanceof SampleLocalProject) {
                handleCreatingSampleBuiltInProject((SampleLocalProject) selectedSampleProject);
            }
        } else {
            handleCreatingBlankProject();
        }
        super.okPressed();
        
        if (!(selectedSampleProject instanceof SampleRemoteProject)) {
        	KatalonApplicationActivator.getTestOpsConfiguration().testOpsQuickIntergration();
        }
    }

    private void handleCreatingSampleRemoteProject(SampleRemoteProject sampleRemoteProject) {
        String projectName = getProjectName();
        String projectParentLocation = getProjectLocation();
        String projectLocation = new File(projectParentLocation, projectName).getAbsolutePath();
        String projectDescription = getProjectDescription();
        ProjectType projectType = getSelectedProjectType();

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(projectName);
        projectEntity.setFolderLocation(projectLocation);
        projectEntity.setDescription(projectDescription);
        projectEntity.setType(projectType);

        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.GIT_CLONE_REMOTE_PROJECT,
                new Object[] { sampleRemoteProject, projectEntity, false });
    }

    private void handleCreatingSampleBuiltInProject(SampleLocalProject sampleBuiltInProject) {
        try {
            String projectName = getProjectName();
            String projectParentLocation = getProjectLocation();
            String projectDescription = getProjectDescription();
            ProjectType projectType = getSelectedProjectType();

            String projectLocation = new File(projectParentLocation, projectName).getAbsolutePath();
            SampleProjectProvider.getInstance().extractSampleWebUIProject(sampleBuiltInProject, projectLocation);
            FileUtils.forceDelete(ProjectController.getInstance().getProjectFile(projectLocation));

            ProjectEntity newProject = ProjectController.getInstance().newProjectEntity(projectName, projectDescription,
                    projectParentLocation, true);
            if (newProject == null) {
                return;
            }
            updateProjectType(newProject, projectType);
            eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
            Trackings.trackCreatingSampleProject(sampleBuiltInProject.getName(), newProject.getUUID(), projectType);

            eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());

            TimeUnit.SECONDS.sleep(1);

            eventBroker.post(EventConstants.API_QUICK_START_DIALOG_OPEN, projectType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @SuppressWarnings("restriction")
    private void handleCreatingBlankProject() {
        try {
            String projectName = getProjectName();
            String projectLocation = getProjectLocation();
            String projectDescription = getProjectDescription();
            ProjectType projectType = getSelectedProjectType();
            boolean generateGitignoreFile = cbGenerateGitignoreFile.isEnabled() && cbGenerateGitignoreFile.getSelection();
            boolean generateGradleFile = cbGenerateGradleFile.isEnabled() && cbGenerateGradleFile.getSelection();

            ProjectEntity newProject = createNewProject(projectName, projectLocation, projectDescription,
                    generateGitignoreFile, generateGradleFile);
            if (newProject == null) {
                return;
            }
            updateProjectType(newProject, projectType);
            eventBroker.send(EventConstants.PROJECT_CREATED, newProject);

            Trackings.trackCreatingProject(newProject.getUUID(), projectType);

            eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());

            TimeUnit.SECONDS.sleep(1);

            if (!(getSelectedProjectType() == ProjectType.GENERIC)) {
                eventBroker.post(EventConstants.API_QUICK_START_DIALOG_OPEN, projectType);
            }
        } catch (FilePathTooLongException ex) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    ex.getMessage());
        } catch (Exception ex) {
            LoggerSingleton.getInstance().getLogger().error(ex);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_PROJ);
        }
    }

    @SuppressWarnings("restriction")
    private ProjectEntity createNewProject(String projectName, String projectLocation, String projectDescription,
            boolean generateGitignoreFile, boolean generateGradleFile)
            throws Exception {
        try {
            ProjectEntity newProject = ProjectController.getInstance().addNewProject(projectName, projectDescription,
                    projectLocation);
            // EntityTrackingHelper.trackProjectCreated();
            if (generateGitignoreFile) {
                initGitignoreFile(newProject);
            }
            
            if (generateGradleFile) {
                initGradleFile(newProject);
            }
            return newProject;
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
    
    public static void initGitignoreFile(ProjectEntity project) throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(NewProjectDialog.class);
        URL url = FileLocator.find(bundle,
                new org.eclipse.core.runtime.Path("/resources/gitignore/gitignore_template"), null);
        File templateFile = FileUtils.toFile(FileLocator.toFileURL(url));

        File gitignoreFile = new File(project.getFolderLocation(), ".gitignore");
        if (!gitignoreFile.exists()) {
            gitignoreFile.createNewFile();
            FileUtils.copyFile(templateFile, gitignoreFile);
        }
    }
    
    public static void initGradleFile(ProjectEntity project) throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(NewProjectDialog.class);
        URL url = FileLocator.find(bundle,
                new org.eclipse.core.runtime.Path("/resources/gradle/gradle_template"), null);
        File templateFile = FileUtils.toFile(FileLocator.toFileURL(url));

        File gradleFile = new File(project.getFolderLocation(), "build.gradle");
        if (!gradleFile.exists()) {
            gradleFile.createNewFile();
            FileUtils.copyFile(templateFile, gradleFile);
        }
    }

    private void updateProjectType(ProjectEntity project, ProjectType type) throws Exception {
        project.setType(type);
        ProjectController.getInstance().updateProject(project);
    }

    private ProjectType getSelectedProjectType() {
        if (rbWebServiceProjectType.getSelection()) {
            return ProjectType.WEBSERVICE;
        } else if (rbWebProjectType.getSelection()) {
            return ProjectType.WEBUI;
        } else if (rbMobileProjectType.getSelection()) {
            return ProjectType.MOBILE;
        } else {
            return ProjectType.GENERIC;
        }
    }

    @Override
    protected Point getInitialSize() {
        return getShell().computeSize(convertHorizontalDLUsToPixels(350), SWT.DEFAULT, true);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    public String getProjectName() {
        return name;
    }

    public String getProjectLocation() {
        return loc;
    }

    public String getProjectDescription() {
        return desc;
    }
}
