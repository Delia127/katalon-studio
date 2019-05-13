package com.kms.katalon.composer.folder.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class NewRootFolderDialog extends CustomTitleAreaDialog {
    
    private Text txtName;
    
    private String name;
    
    private List<String> siblingFileNames;

    public NewRootFolderDialog(Shell parentShell) {
        super(parentShell);
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        try {
            siblingFileNames = FolderController.getInstance().getRootFileOrFolderNames(project);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            siblingFileNames = new ArrayList<>();
        }
    }

    @Override
    protected Composite createContentArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(2, false));
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Label lblName = new Label(body, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblName.setText(GlobalMessageConstants.NAME);
        
        txtName = new Text(body, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        return body;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                checkNewName(txtName.getText());
            } 
        });      
    }
    
    private void checkNewName(String newName) {
        if (isNameDuplicated(newName)) {
            setMessage(StringConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
            return;
        }
        
        try {
            EntityNameController.getInstance().validateName(newName);
            setMessage(StringConstants.DIA_MSG_CREATE_NEW_FOLDER, IMessageProvider.INFORMATION);
            getButton(OK).setEnabled(true);
        } catch (Exception e) {
            setMessage(e.getMessage(), IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
        }
    }
    
    private boolean isNameDuplicated(String newName) {
        return siblingFileNames.stream()
                .filter(n -> n.equalsIgnoreCase(newName))
                .findAny()
                .isPresent();
    }
    
    @Override
    protected void setInput() {
        txtName.setText(getSuggestion("New Folder"));
        setMessage(StringConstants.DIA_MSG_CREATE_NEW_FOLDER, IMessageProvider.INFORMATION);
    }

    private String getSuggestion(String suggestion) {
        String newName = String.format("%s", suggestion);
        int index = 0;

        while (isNameDuplicated(newName)) {
            index += 1;
            newName = String.format("%s %d", suggestion, index);
        }
        return newName;
    }
    
    @Override
    protected void okPressed() {
        name = txtName.getText();
        super.okPressed();
    }
    
    public String getNewFolderName() {
        return name;
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(400, 250);
    }
}
