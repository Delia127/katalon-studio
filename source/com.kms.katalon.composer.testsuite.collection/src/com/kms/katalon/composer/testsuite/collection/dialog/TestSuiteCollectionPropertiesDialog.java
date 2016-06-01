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

import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionPropertiesDialog extends CommonPropertiesDialog {

    private Text txtCreatedDate;

    private Text txtModifiedDate;
    private Text txtTag;

    public TestSuiteCollectionPropertiesDialog(Shell parentShell, FileEntity entity) {
        super(parentShell, entity);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_COLLECTION_SUITE_PROPERTIES);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = (Composite) super.createDialogContainer(parent);

        Label lblTag = new Label(container, SWT.NONE);
        lblTag.setText(StringConstants.TAG);

        txtTag = new Text(container, SWT.BORDER);
        txtTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblCreatedDate = new Label(container, SWT.NONE);
        lblCreatedDate.setText(StringConstants.PA_LBL_CREATED_DATE);

        txtCreatedDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtCreatedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblModifiedDate = new Label(container, SWT.NONE);
        lblModifiedDate.setText(StringConstants.PA_LBL_LAST_UPDATED);

        txtModifiedDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtModifiedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setInput() {
        super.setInput();
        TestSuiteCollectionEntity testSuiteCollection = getEntity();
        txtTag.setText(StringUtils.defaultString(testSuiteCollection.getTag()));
        txtCreatedDate.setText(testSuiteCollection.getDateCreated().toString());
        txtModifiedDate.setText(testSuiteCollection.getDateModified().toString());
    }

    @Override
    protected void updateChanges() {
        if (!isModified()) {
            return;
        }

        TestSuiteCollectionEntity testSuiteCollection = getEntity();
        testSuiteCollection.setDescription(txtDescription.getText());
        testSuiteCollection.setTag(txtTag.getText());
    }

    @Override
    protected void registerControlModifyListeners() {
        super.registerControlModifyListeners();

        txtTag.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setModified(true);
            }
        });
    }

    @Override
    public TestSuiteCollectionEntity getEntity() {
        return (TestSuiteCollectionEntity) super.getEntity();
    }
}
