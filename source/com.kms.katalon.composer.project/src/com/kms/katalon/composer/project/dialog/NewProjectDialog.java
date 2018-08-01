package com.kms.katalon.composer.project.dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.MarshalException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.sample.SampleBuiltInProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewProjectDialog extends TitleAreaDialog {
    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private static final int PROJECT_DESC_TEXT_LEFT_MARGIN = 3;

    private static final int PROJECT_DESC_DISPLAY_LINE_NUMBER = 4;
    
    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private Text txtProjectName;

    private Text txtProjectLocation;

    private StyledText txtProjectDescription;

    private String name, loc, desc;

    private ProjectEntity project;
    
    private SampleRemoteProject sampleRemoteProject;
    
    private String sampleBuiltInProjectType;
    
    private SampleBuiltInProject sampleBuiltInProject;

    private boolean showError;

    private Button btnFolderChooser;
    
    private String title;
    
    private Combo cbSampleProjects;
    
    private Text txtRepoUrl;
    
    private Button radioBlankProject;
    
    private Button radioSampleProject;
    
    private List<SampleRemoteProject> sampleRemoteProjects = new ArrayList<>();
    
    private List<SampleBuiltInProject> sampleBuiltInProjects = new ArrayList<>();
    
    private List<String> sampleProjectNames;

    public NewProjectDialog(Shell parentShell) {
        this(parentShell, (SampleRemoteProject) null);
    }
    
    public NewProjectDialog(Shell parentShell, SampleRemoteProject sampleRemoteProject) {
        super(parentShell);
        this.title = StringConstants.VIEW_TITLE_NEW_PROJ;
        this.sampleRemoteProject = sampleRemoteProject;
    }
    
    public NewProjectDialog(Shell parentShell, String sampleBuiltInProjectType) {
        super(parentShell);
        this.title = StringConstants.VIEW_TITLE_NEW_PROJ;
        this.sampleBuiltInProjectType = sampleBuiltInProjectType;
    }

    public NewProjectDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        this.project = project;
        this.title = StringConstants.VIEW_TITLE_PROJECT_PROPERTIES;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
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
        
        Composite emptyComposite = new Composite(container, SWT.NONE);
        emptyComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        emptyComposite.setLayout(new GridLayout(1, false));
        
        Composite projectTypeComposite = new Composite(container, SWT.NONE);
        projectTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        projectTypeComposite.setLayout(new GridLayout(2, false));
        
        radioBlankProject = new Button(projectTypeComposite, SWT.RADIO);
        GridData gdBlankProject = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
        radioBlankProject.setLayoutData(gdBlankProject);
        radioBlankProject.setText(StringConstants.VIEW_OPTION_BLANK_PROJECT);
        radioBlankProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setSampleProjectGroupEnabled(false);
            }
        });
        
        radioSampleProject = new Button(projectTypeComposite, SWT.RADIO);
        GridData gdSampleProject = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdSampleProject.horizontalIndent = 20;
        radioSampleProject.setLayoutData(gdSampleProject);
        radioSampleProject.setText(StringConstants.VIEW_OPTION_SAMPLE_PROJECT);
        radioSampleProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setSampleProjectGroupEnabled(true);
            }
        });
        
        Label lblSampleProjects = new Label(container, SWT.NONE);
        lblSampleProjects.setText(StringConstants.VIEW_LBL_SAMPLE_PROJECT);
        
        cbSampleProjects = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        cbSampleProjects.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
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

        populateSampleProjects();
        
        addControlModifyListeners();
       
        return area;
    }
    
    private void populateSampleProjects() {
        if (sampleRemoteProject != null) {
            setSampleProjectGroupEnabled(true);
            
            loadSampleRemoteProjects();
            
            int sampleProjectIdx = sampleProjectNames.indexOf(sampleRemoteProject.getName());
            if (sampleProjectIdx >= 0) {
                cbSampleProjects.select(sampleProjectIdx);
            }
            
            setRepoUrlBasedOnSampleProjectSelection();
        } else if (sampleBuiltInProjectType != null) {
            setSampleProjectGroupEnabled(true);
            
            loadSampleBuiltInProjects();
            
            int sampleProjectIdx = -1;
            for (SampleBuiltInProject sample : sampleBuiltInProjects) {
                sampleProjectIdx++;
                if (sample.getType().equals(sampleBuiltInProjectType)) {
                    break;
                }
            }
            
            if (sampleProjectIdx >= 0) {
                cbSampleProjects.select(sampleProjectIdx);
            }
        } else {
            setSampleProjectGroupEnabled(false);
            radioBlankProject.setSelection(true);
            
            loadSampleRemoteProjects();
            if (sampleRemoteProjects.size() > 0) {
                cbSampleProjects.select(0);
                setRepoUrlBasedOnSampleProjectSelection();
            } else {
                loadSampleBuiltInProjects();
                cbSampleProjects.select(0);
            }
        }
    }
    
    private void setSampleProjectGroupEnabled(boolean enabled) {
        radioSampleProject.setSelection(enabled);
        cbSampleProjects.setEnabled(enabled);
        txtRepoUrl.setEnabled(enabled);
    }
    
    private void loadSampleRemoteProjects() {
        sampleRemoteProjects = SampleRemoteProjectProvider.getCachedProjects();
        
        sampleProjectNames = sampleRemoteProjects
                .stream()
                .map(sample -> sample.getName())
                .collect(Collectors.toList());
        
        cbSampleProjects.setItems(sampleProjectNames.toArray(new String[sampleProjectNames.size()]));
    }
    
    private void loadSampleBuiltInProjects() {
        sampleBuiltInProjects = new ArrayList<>();
        sampleBuiltInProjects.add(new SampleBuiltInProject(SampleProjectProvider.SAMPLE_WEB_UI, StringConstants.SAMPLE_WEB_UI_PROJECT));
        sampleBuiltInProjects.add(new SampleBuiltInProject(SampleProjectProvider.SAMPLE_MOBILE, StringConstants.SAMPLE_MOBILE_PROJECT));
        sampleBuiltInProjects.add(new SampleBuiltInProject(SampleProjectProvider.SAMPLE_WEB_SERVICE, StringConstants.SAMPLE_WEB_SERVICE_PROJECT));
        
        sampleProjectNames = sampleBuiltInProjects
                .stream()
                .map(project -> project.getName())
                .collect(Collectors.toList());
        
        cbSampleProjects.setItems(sampleProjectNames.toArray(new String[sampleProjectNames.size()]));
    }
    
    private void setRepoUrlBasedOnSampleProjectSelection() {
        SampleRemoteProject selectedSampleProject = sampleRemoteProjects.get(cbSampleProjects.getSelectionIndex());
        txtRepoUrl.setText(selectedSampleProject.getSourceUrl());
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
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            txtProjectName.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    checkInput();
                }

            });
        }
        // On MAC OS, with some input source language that enable Auto correct spelling (ex: Telex or VNI)
        // if the input text not accept by the system then when the input control lost focus the text will be remove as
        // default.
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            txtProjectName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String projectName = txtProjectName.getText();
                    super.focusLost(e);
                    txtProjectName.setText(projectName);
                }
            });
        }

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
        
        cbSampleProjects.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (areRemoteSamplesAvailable()) {
                    setRepoUrlBasedOnSampleProjectSelection();
                }
            }
        });
    }
    
    private boolean areRemoteSamplesAvailable() {
        return sampleRemoteProjects.size() > 0;
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
        setErrorMessage(null);
        getButton(Dialog.OK).setEnabled(
                validateProjectFolderLocation() && validateProjectName() && validateProjectNameDuplication());
    }

    @Override
    protected void okPressed() {
        name = txtProjectName.getText();
        loc = getProjectLocationInput();
        desc = txtProjectDescription.getText();
        
        boolean useSampleProject = radioSampleProject.getSelection();
        if (useSampleProject) {
            if (areRemoteSamplesAvailable()) {
                sampleRemoteProject = sampleRemoteProjects.get(cbSampleProjects.getSelectionIndex());
                handleCreatingSampleRemoteProject(sampleRemoteProject);
            } else {
                sampleBuiltInProject = sampleBuiltInProjects.get(cbSampleProjects.getSelectionIndex());
                handleCreatingSampleBuiltInProject(sampleBuiltInProject);
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
        
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(projectName);
        projectEntity.setFolderLocation(projectLocation);
        projectEntity.setDescription(projectDescription);
        
        EventBrokerSingleton.getInstance().getEventBroker()
            .post(EventConstants.GIT_CLONE_REMOTE_PROJECT, new Object[] { sampleRemoteProject, projectEntity });
    }
    
    private void handleCreatingSampleBuiltInProject(SampleBuiltInProject sampleBuiltInProject) {
        try {
            String projectName = getProjectName();
            String projectParentLocation = getProjectLocation();
            String projectDescription = getProjectDescription();
    
            String projectLocation = new File(projectParentLocation, projectName).getAbsolutePath();
            SampleProjectProvider.getInstance().extractSampleWebUIProject(sampleBuiltInProject.getType(), projectLocation);
            FileUtils.forceDelete(ProjectController.getInstance().getProjectFile(projectLocation));
    
            ProjectEntity newProject = ProjectController.getInstance().newProjectEntity(projectName, projectDescription,
                    projectParentLocation, true);
            if (newProject == null) {
                return;
            }
            eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
            Trackings.trackCreatingSampleProject(sampleBuiltInProject.getType(), newProject.getUUID());
    
            // Open created project
            eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());
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
            
            ProjectEntity newProject = createNewProject(projectName, projectLocation, projectDescription);
            if (newProject == null) {
                return;
            }
            eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
            
            Trackings.trackCreatingProject();
    
            // Open created project
            eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());
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
//            EntityTrackingHelper.trackProjectCreated();
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
