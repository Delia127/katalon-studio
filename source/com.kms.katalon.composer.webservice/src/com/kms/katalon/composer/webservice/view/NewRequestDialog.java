package com.kms.katalon.composer.webservice.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class NewRequestDialog extends CommonNewEntityDialog<WebServiceRequestEntity> {

    private String webServiveType = WebServiceRequestEntity.SERVICE_TYPES[0];

    private Combo cbbRequestType;

    public NewRequestDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ);
        setDialogMsg(StringConstants.VIEW_DIA_MSG_CREATE_NEW_WEBSERVICE_REQ);
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        createRequestTypeControl(parent, column);
        return super.createEntityCustomControl(parent, column, span);
    }

    private Control createRequestTypeControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));
        Label labelRequestType = new Label(parent, SWT.NONE);
        labelRequestType.setText(StringConstants.VIEW_LBL_REQ_TYPE);

        cbbRequestType = new Combo(parent, SWT.READ_ONLY);
        cbbRequestType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbbRequestType.setItems(WebServiceRequestEntity.SERVICE_TYPES);
        cbbRequestType.select(0);
        cbbRequestType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setWebServiveType(((Combo) e.getSource()).getText());
            }
        });

        return parent;
    }

    private void setWebServiveType(String webServiveType) {
        this.webServiveType = webServiveType;
    }

    @Override
    protected void createEntity() {
        try {
            entity = ObjectRepositoryController.getInstance().newWSTestObjectWithoutSave(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        entity.setServiceType(webServiveType);
    }

}
