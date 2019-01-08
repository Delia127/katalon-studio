package com.kms.katalon.composer.testcase.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddNewTestCaseTagDialog extends Dialog {
    
    private Set<String> existingTags;
    
    private Text txtTagName;
    
    private Label lblError;
    
    private String newTagName;

    public AddNewTestCaseTagDialog(Shell parentShell, Set<String> existingTags) {
        super(parentShell);
        this.existingTags = existingTags != null ? existingTags : new HashSet<>();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(2, false));
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 300;
        body.setLayoutData(gdBody);
        
        Label lblTagName = new Label(body, SWT.NONE);
        lblTagName.setText("Tag name");
        
        txtTagName = new Text(body, SWT.BORDER);
        txtTagName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        txtTagName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                newTagName = txtTagName.getText();
                validateTagName(newTagName);
            }
        });
        
        Label lblEmpty = new Label(body, SWT.NONE);
        lblEmpty.setText(StringUtils.EMPTY);
        
        lblError = new Label(body, SWT.NONE);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        return super.createDialogArea(parent);
    }
    
    private void validateTagName(String tagName) {
        setErrorMessageVisible(false);
        
        String name = tagName.trim();
        if (StringUtils.isBlank(name)) {
            setButtonOkEnabled(false);
        } else if (nameExists(name)) {
            displayErrorMessage("A tag with the same name already exists.");
            setButtonOkEnabled(false);
        } else {
            setButtonOkEnabled(true);
        }
    }
    
    private boolean nameExists(String tagName) {
        return existingTags.contains(tagName);
    }
    
    private void setButtonOkEnabled(boolean enabled) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
    
    private void displayErrorMessage(String message) {
        lblError.setText(message);
        setErrorMessageVisible(true);
    }
    
    private void setErrorMessageVisible(boolean visible) {
        ((GridData) lblError.getLayoutData()).exclude = !visible;
        lblError.setVisible(visible);
    }
    
    public String getTagName() {
        return newTagName;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Add New Tag");
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
}
