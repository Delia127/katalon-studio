package com.kms.katalon.composer.project.sample;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class NewSampleRemoteProjectDialog extends TitleAreaDialog {
    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private SampleRemoteProject sampleProject;

    private Text txtSourceURL;

    private Text txtProjectName;

    private Text txtProjectLocation;

    private Button btnFolderChooser;

    private ProjectEntity projectInfo;

    public ProjectEntity getProjectInfo() {
        return projectInfo;
    }

    public NewSampleRemoteProjectDialog(Shell parentShell, SampleRemoteProject sampleProject) {
        super(parentShell);
        this.sampleProject = sampleProject;
    }

    protected void registerControlModifyListeners() {
        btnFolderChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(btnFolderChooser.getShell());
                dialog.setFilterPath(getProjectLocationInput());
                String path = dialog.open();
                if (path == null) {
                    return;
                }
                String location = txtProjectLocation.getText();
                if (location.isEmpty()) {
                    txtProjectLocation.setText(path);
                } else {
                    txtProjectLocation.setText(path);
                }
            }
        });
        
        ModifyListener modifyListener = new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent arg0) {
                String newLocation = txtProjectLocation.getText();
                if (newLocation.isEmpty()) {
                    setMessage("Project location cannot be empty.", IMessageProvider.WARNING);
                    getButton(OK).setEnabled(false);
                    return;
                }
                String newName = txtProjectName.getText();
                if (newName.isEmpty()) {
                    setMessage("Project name cannot be empty.", IMessageProvider.WARNING);
                    getButton(OK).setEnabled(false);
                    return;
                }
                if (new File(newLocation, newName).exists()) {
                    setMessage("A project with the same name already exists in the selected location.",
                            IMessageProvider.ERROR);
                    getButton(OK).setEnabled(false);
                    return;
                } else {
                    setInfoMessage();
                    getButton(OK).setEnabled(true);
                }
            }
        };
        txtProjectName.addModifyListener(modifyListener);
        txtProjectLocation.addModifyListener(modifyListener);
    }

    private void setInfoMessage() {
        setMessage("Please enter your project information", IMessageProvider.INFORMATION);
    }

    protected void setInput() {
        txtSourceURL.setText(sampleProject.getSourceUrl());
        txtProjectLocation.setText(DEFAULT_PROJECT_LOCATION);

        txtProjectName.setText(getSuggestedLocation(DEFAULT_PROJECT_LOCATION));
        txtProjectName.selectAll();
        txtProjectName.forceFocus();

        setInfoMessage();
    }

    private String getSuggestedLocation(String parentLocation) {
        String name = sampleProject.getName();
        String suggestedProjectLocation = new File(parentLocation, name).getAbsolutePath();
        if (!new File(suggestedProjectLocation).exists()) {
            return name;
        }
        int num = 1;
        while (true) {
            String newSuggestedName = String.format("%s_%d", name, num);
            if (!new File(parentLocation, newSuggestedName).exists()) {
                return newSuggestedName;
            }
            num++;
        }
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

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 15;
        gridLayout.verticalSpacing = 15;
        container.setLayout(gridLayout);

        Label lblSourceURL = new Label(container, SWT.NONE);
        lblSourceURL.setText("Repository URL");

        txtSourceURL = new Text(container, SWT.BORDER);
        txtSourceURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtSourceURL.setEditable(false);
        txtSourceURL.setBackground(ColorUtil.getDisabledItemBackgroundColor());

        Label lblProjectName = new Label(container, SWT.NONE);
        lblProjectName.setText("Project Name");

        txtProjectName = new Text(container, SWT.BORDER);
        txtProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblProjectLocation = new Label(container, SWT.NONE);
        lblProjectLocation.setText("Project Location");

        Composite locationControlsComposite = new Composite(container, SWT.NONE);
        locationControlsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout glLocationControl = new GridLayout(2, false);
        glLocationControl.marginWidth = 0;
        glLocationControl.marginHeight = 0;
        locationControlsComposite.setLayout(glLocationControl);
        txtProjectLocation = new Text(locationControlsComposite, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnFolderChooser = new Button(locationControlsComposite, SWT.NONE);
        btnFolderChooser.setText("Browse...");

        setInput();
        registerControlModifyListeners();
        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, super.getInitialSize().y);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Clone Remote Project");
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }

    @Override
    protected void okPressed() {
        this.projectInfo = new ProjectEntity();
        projectInfo.setName(txtProjectName.getText());
        projectInfo.setFolderLocation(txtProjectLocation.getText());
        super.okPressed();
    }
}
