package com.kms.katalon.composer.webservice.editor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;

public class NoContentBodyEditor extends HttpBodyEditor {

    public NoContentBodyEditor(Composite parent, int style) {
        super(parent, style);
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        
        Label separator = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
        
        Label lblMessage = new Label(this, SWT.NONE);
        GridData gdMessage = new GridData(GridData.FILL_BOTH);
        gdMessage.verticalIndent = 10;
        lblMessage.setLayoutData(gdMessage);
        lblMessage.setAlignment(SWT.CENTER);
        lblMessage.setText(ComposerWebserviceMessageConstants.MSG_REQUEST_BODY_NO_CONTENT);
    }

    @Override
    public String getContentData() {
        return StringUtils.EMPTY;
    }
    
    @Override
    public void setInput(String httpBodyContent) {
    }

}
