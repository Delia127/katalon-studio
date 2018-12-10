package com.kms.katalon.composer.webservice.dialogs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.core.webservice.common.WebServiceMethod;

public class AddOrEditWebServiceMethodDialog extends Dialog {
    
    private WebServiceMethod method;
    
    private Text txtMethod;
    
    private String methodName;
    
    private Label lblError;
    
    private Text txtDescription;
    
    private String description;
    
    private List<WebServiceMethod> existingMethods;
    
    private boolean editMode = false;

    public AddOrEditWebServiceMethodDialog(Shell parentShell, WebServiceMethod method,
            List<WebServiceMethod> existingMethods, boolean editMode) {
        super(parentShell);
        this.method = method != null ? method : new WebServiceMethod();
        this.methodName = this.method.getName();
        this.description = this.method.getDescription();
        this.existingMethods = existingMethods;
        this.editMode = editMode;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        body.setLayout(layout);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 400;
        body.setLayoutData(gdBody);
        
        Label lblMethod = new Label(body, SWT.NONE); 
        lblMethod.setText(StringConstants.DiaEditWSMethod_LBL_METHOD);
        
        txtMethod = new Text(body, SWT.BORDER);
        txtMethod.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtMethod.setText(method.getName());
        
        Label lblDescription = new Label(body, SWT.NONE);
        lblDescription.setText(StringConstants.DiaEditWSMethod_LBL_DESCRIPTION);
        
        txtDescription = new Text(body, SWT.BORDER);
        txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtDescription.setText(method.getDescription());
        
        Label lblEmpty = new Label(body, SWT.NONE);
        lblEmpty.setText(StringUtils.EMPTY);
        
        lblError = new Label(body, SWT.NONE);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        setLblErrorVisible(false);
        
        registerControlListeners();
        
        return super.createDialogArea(parent);
    }

    private void registerControlListeners() {
        txtMethod.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                methodName = txtMethod.getText();
            }
        });
        
        txtMethod.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                e.text = e.text.toUpperCase();
            }
        });
        
        txtDescription.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                description = txtDescription.getText();
            }
        });
    }

    protected boolean validateMethodName(String methodName) {
        setLblErrorVisible(false);
        boolean hasError = false;
        String errorMessage = null;
        
        if (StringUtils.isBlank(methodName)) {
            hasError = true;
            errorMessage = StringConstants.DiaEditWSMethod_MSG_EMPTY_METHOD_NAME;
        } else if (methodExists(methodName)) {
            if (!editMode) {
                hasError = true;
            } else {
                if (!methodName.equalsIgnoreCase(method.getName())) {
                    hasError = true;
                } else {
                    hasError = false;
                }
            }
            
            if (hasError) {
                errorMessage = StringConstants.DiaEditWSMethod_MSG_DUPLICATED_METHOD_NAME;
            }
        } else {
            hasError = false;
        }
        
        if (hasError) {
            displayErrorMessage(errorMessage);
        }
        
        return hasError;
    }
    
    private String trimMethodName(String methodName) {
        return methodName.trim().replaceAll("\\s+", " ");
    }
    
    private boolean methodExists(String methodName) {
        return existingMethods.stream()
                .anyMatch(method -> method.getName().equalsIgnoreCase(methodName));
    }
    
    private void displayErrorMessage(String message) {
        setLblErrorVisible(true);
        lblError.setText(message);
    }
    
    private void setLblErrorVisible(boolean visible) {
        ((GridData) lblError.getLayoutData()).exclude = !visible;
        lblError.setVisible(visible);
        lblError.getParent().layout(true, true);
    }
    
    public WebServiceMethod getMethod() {
        method.setName(methodName);
        method.setType(WebServiceMethod.TYPE_CUSTOM);
        method.setDescription(description);
        return method;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (editMode) {
            shell.setText(StringConstants.DiaEditWSMethod_TITLE_EDIT);
        } else {
            shell.setText(StringConstants.DiaEditWSMethod_TITLE_ADD_NEW);
        }
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    protected void okPressed() {
        methodName = trimMethodName(methodName);
        boolean hasError = validateMethodName(methodName);
        if (!hasError) {
            super.okPressed();
        }
    }
}
