package com.kms.katalon.composer.testsuite.collection.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class NewTestSuiteCollectionDialog extends CommonNewEntityDialog<TestSuiteCollectionEntity> {

    private String tag;

    public NewTestSuiteCollectionDialog(Shell parentShell, FolderEntity parentFolder, String preferedName) {
        super(parentShell, parentFolder, preferedName);
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_NEW_TEST_SUITE_COLLECTION;
    }

    @Override
    public String getDialogMsg() {
        return StringConstants.DIA_MSG_NEW_TEST_SUITE_COLLECTION;
    }

    @Override
    protected Control createPropertiesControl(Composite parent, int column, int span) {
        super.createPropertiesControl(parent, column, span);

        Label lblTag = new Label(parent, SWT.NONE);
        lblTag.setText(StringConstants.TAG);

        Text txtTag = new Text(parent, SWT.BORDER);
        txtTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtTag.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                tag = ((Text) e.getSource()).getText();
            }
        });

        if (span > 0) {
            createEmptySpace(parent, span);
        }

        return container;
    }

    @Override
    protected void createEntity() {
        try {
            entity = TestSuiteCollectionController.getInstance().newTestSuiteCollection(parentFolder, getName());
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HDL_MSG_UNABLE_TO_CREATE_TEST_SUITE_COLLECTION,
                    e.getMessage());
        }
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        entity.setTag(StringUtils.trimToEmpty(tag));
    }
}
