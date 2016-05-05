package com.kms.katalon.composer.testcase.dialogs;

import org.apache.commons.lang.StringUtils;
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
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestCaseDialog extends CommonNewEntityDialog {

    private String tag;

    public NewTestCaseDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell, parentFolder);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_CASE);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_CASE);
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

    public String getTag() {
        return StringUtils.trimToEmpty(tag);
    }

}
