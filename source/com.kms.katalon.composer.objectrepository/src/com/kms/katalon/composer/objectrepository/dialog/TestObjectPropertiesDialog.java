package com.kms.katalon.composer.objectrepository.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectPropertiesDialog extends CommonPropertiesDialog<WebElementEntity> {

    public TestObjectPropertiesDialog(Shell parentShell, WebElementEntity testObject) {
        super(parentShell, testObject);
        setDialogTitle(StringConstants.VIEW_TITLE_TEST_OBJ_PROPERTIES);
    }

}
