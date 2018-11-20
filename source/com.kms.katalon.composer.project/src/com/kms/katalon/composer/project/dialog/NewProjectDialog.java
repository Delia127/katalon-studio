package com.kms.katalon.composer.project.dialog;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
import com.kms.katalon.tracking.service.Trackings;

public class NewProjectDialog extends TitleAreaDialog {
    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private static final int PROJECT_DESC_TEXT_LEFT_MARGIN = 3;

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

    private Combo cbProjects;

    private Text txtRepoUrl;

    private Button rbWebServiceProjectType;

    private Button rbGenericProjectType;
    
    private boolean okButtonClicked = false;

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

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(2, false));

        Label l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_NAME);

        txtProjectName = new Text(container, SWT.BORDER);
        txtProjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblProjectType = new Label(container, SWT.NONE);
        lblProjectType.setText("Type");

        Composite projectTypeComposite = new Composite(container, SWT.NONE);
        projectTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        projectTypeComposite.setLayout(new GridLayout(3, false));

        rbGenericProjectType = new Button(projectTypeComposite, SWT.RADIO);
        GridData gdGenericProjectType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rbGenericProjectType.setLayoutData(gdGenericProjectType);
        rbGenericProjectType.setText(StringConstants.VIEW_OPTION_GENERIC_PROJECT);
        rbGenericProjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                populateGenericProjects();
            }
        });

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

        cbProjects = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        cbProjects.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (initialSampleProject != null) {
            if (initialSampleProject.getType() == SampleProjectType.WS) {
                populateWebServiceProjects();
                rbWebServiceProjectType.setSelection(true);
            } else {
                populateGenericProjects();
                rbGenericProjectType.setSelection(true);
            }
            cbProjects.select(cbProjects.indexOf(initialSampleProject.getName()));
        } else {
            populateGenericProjects();
            rbGenericProjectType.setSelection(true);
            cbProjects.select(0);
        }
        showRepoUrlBySelectedProject();

        addControlModifyListeners();

        return area;
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

    private void populateWebServiceProjects() {
        populateProjects(SampleProjectType.WS);
    }

    private void populateProjects(SampleProjectType... sampleProjectTypes) {
        List<SampleProjectType> sampleProjectTypeList = Arrays.asList(sampleProjectTypes);

        cbProjects.removeAll();

        cbProjects.add(BLANK_PROJECT);

        sampleProjects.stream().filter(sample -> sampleProjectTypeList.contains(sample.getType())).forEach(sample -> {
            cbProjects.add(sample.getName());
            cbProjects.setData(sample.getName(), sample);
        });

        cbProjects.select(0);
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

        cbProjects.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showRepoUrlBySelectedProject();
            }
        });
    }

    private void showRepoUrlBySelectedProject() {
        txtRepoUrl.setText(StringUtils.EMPTY);

        int selectionIdx = cbProjects.getSelectionIndex();
        String selectedProjectName = cbProjects.getItem(selectionIdx);
        if (!selectedProjectName.equals(BLANK_PROJECT)) {
            Object sampleProject = cbProjects.getData(selectedProjectName);
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

        int selectionIdx = cbProjects.getSelectionIndex();
        String selectedProjectName = cbProjects.getItem(selectionIdx);
        if (!selectedProjectName.equals(BLANK_PROJECT)) {
            Object selectedSampleProject = cbProjects.getData(selectedProjectName);
            if (selectedSampleProject instanceof SampleRemoteProject) {
                handleCreatingSampleRemoteProject((SampleRemoteProject) selectedSampleProject);
            } else if (selectedSampleProject instanceof SampleLocalProject) {
                handleCreatingSampleBuiltInProject((SampleLocalProject) selectedSampleProject);
            }
        } else {
            handleCreatingBlankProject();
        }
        super.okPressed();
    }

    private void handleCreatingSampleRemoteProject(SampleRemoteProject sampleRemoteProject) {
        String projectName = getProjectName();
        String projectLocation = getProjectLocation();
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
            if (getSelectedProjectType() == ProjectType.WEBSERVICE) {
                eventBroker.post(EventConstants.API_QUICK_START_DIALOG_OPEN, null);
            }
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

            ProjectEntity newProject = createNewProject(projectName, projectLocation, projectDescription);
            if (newProject == null) {
                return;
            }
            updateProjectType(newProject, projectType);
            eventBroker.send(EventConstants.PROJECT_CREATED, newProject);

            Trackings.trackCreatingProject(newProject.getUUID(), projectType);

            eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());
            
            TimeUnit.SECONDS.sleep(1);
            if (getSelectedProjectType() == ProjectType.WEBSERVICE) {
                eventBroker.post(EventConstants.API_QUICK_START_DIALOG_OPEN, null);
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
    private ProjectEntity createNewProject(String projectName, String projectLocation, String projectDescription)
            throws Exception {
        try {
            ProjectEntity newProject = ProjectController.getInstance().addNewProject(projectName, projectDescription,
                    projectLocation);
            // EntityTrackingHelper.trackProjectCreated();
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

    private void updateProjectType(ProjectEntity project, ProjectType type) throws Exception {
        project.setType(type);
        ProjectController.getInstance().updateProject(project);
    }

    private ProjectType getSelectedProjectType() {
        if (rbGenericProjectType.getSelection()) {
            return ProjectType.GENERIC;
        } else {
            return ProjectType.WEBSERVICE;
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
