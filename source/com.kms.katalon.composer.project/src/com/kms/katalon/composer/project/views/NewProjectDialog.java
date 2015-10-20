package com.kms.katalon.composer.project.views;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class NewProjectDialog extends TitleAreaDialog {

    private Text txtProjectName;

    private Text txtProjectLocation;

    private Text txtProjectDescription;

    private String name, loc, desc;

    private ProjectEntity project;

    private boolean showError;

    private Button btnFolderChooser;

    public NewProjectDialog(Shell parentShell) {
        super(parentShell);
    }

    public NewProjectDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        this.project = project;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        getShell().setText(
                project == null ? StringConstants.VIEW_TITLE_NEW_PROJ : StringConstants.VIEW_TITLE_UPDATE_PROJ);
        setTitle(project == null ? StringConstants.VIEW_TITLE_NEW_PROJ : StringConstants.VIEW_TITLE_UPDATE_PROJ);
        setMessage(StringConstants.VIEW_MSG_PLS_ENTER_PROJ_INFO);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(2, false));

        Label l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_NAME);

        txtProjectName = new Text(container, SWT.BORDER);
        txtProjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Add
        l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_LOCATION);
        createFileChooserComposite(container);

        l = new Label(container, SWT.NONE);
        l.setText(StringConstants.VIEW_LBL_DESCRIPTION);

        txtProjectDescription = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        txtProjectDescription.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (project != null) {
            txtProjectName.setText(project.getName());
            txtProjectLocation.setText(project.getFolderLocation());
            txtProjectDescription.setText(project.getDescription());
            txtProjectLocation.setEnabled(false);
        }

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addControlModifyListeners();
        return area;
    }

    private Composite createFileChooserComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout theLayout = new GridLayout((project == null) ? 2 : 1, false);
        theLayout.marginWidth = 0;
        container.setLayout(theLayout);

        txtProjectLocation = new Text(container, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
                validate();
            }
        });

        txtProjectName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
        if (btnFolderChooser != null) {
            btnFolderChooser.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    DirectoryDialog dialog = new DirectoryDialog(btnFolderChooser.getShell());
                    String path = dialog.open();
                    if (path == null) return;
                    txtProjectLocation.setText(path);
                }
            });
        }
    }

    public void setErrorMessage(String newErrorMessage) {
        if (showError) {
            super.setErrorMessage(newErrorMessage);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        showError = false;
        validate();
        showError = true;
    }

    private boolean validateProjectFolderLocation(String projectLocation) {
        if (projectLocation == null || projectLocation.isEmpty()) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_CANNOT_BE_BLANK);
            return false;
        }
        File folderLocation = new File(txtProjectLocation.getText());
        return folderLocation.isDirectory() ? true : false;
    }

    private boolean validate() {
        String projectLocation = txtProjectLocation.getText().trim();
        String projectName = txtProjectName.getText().trim();
        if (validateProjectFolderLocation(projectLocation)) {
            try {
                if (projectName == null || projectName.isEmpty()) {
                    setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK);
                    return false;
                }
                if (ProjectController.getInstance().validateNewProjectName(projectLocation, projectName)) {
                    getButton(Dialog.OK).setEnabled(true);
                    setErrorMessage(null);
                    return true;
                } else {
                    setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_EXISTED_IN_LOC);
                }
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
            }
            getButton(Dialog.OK).setEnabled(false);
        } else {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_DOES_NOT_EXIST);
            getButton(Dialog.OK).setEnabled(false);
        }
        return false;
    }

    @Override
    protected void okPressed() {
        name = txtProjectName.getText();
        loc = txtProjectLocation == null ? "" : txtProjectLocation.getText();
        desc = txtProjectDescription.getText();
        super.okPressed();
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