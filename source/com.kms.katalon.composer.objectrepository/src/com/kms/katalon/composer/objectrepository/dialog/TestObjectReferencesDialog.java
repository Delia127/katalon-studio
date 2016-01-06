package com.kms.katalon.composer.objectrepository.dialog;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteEntityDialog;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.file.FileEntity;

public class TestObjectReferencesDialog extends AbstractDeleteEntityDialog {

    public TestObjectReferencesDialog(Shell parentShell, String testObjectId, List<FileEntity> affectedEntities,
            AbstractDeleteReferredEntityHandler deleteHandler) {
        super(parentShell, deleteHandler);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_OBJECT_REFERENCES);
        setAffectedEntities(affectedEntities);
        setEntityId(testObjectId);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

}
