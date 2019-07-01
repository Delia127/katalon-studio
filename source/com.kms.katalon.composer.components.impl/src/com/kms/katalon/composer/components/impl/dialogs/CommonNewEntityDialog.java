package com.kms.katalon.composer.components.impl.dialogs;

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

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public abstract class CommonNewEntityDialog<T extends FileEntity> extends AbstractEntityDialog {

    protected T entity;

    private String description;

    public CommonNewEntityDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder);
        setName(suggestedName);
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        return createPropertiesControl(parent, column, span);
    }

    protected Control createPropertiesControl(Composite parent, int column, int span) {
        Label lblDescription = new Label(parent, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(StringConstants.DESCRIPTION);

        Text txtDescription = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridData descLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        descLayoutData.heightHint = 100;
        txtDescription.setLayoutData(descLayoutData);
        txtDescription.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                description = ((Text) e.getSource()).getText();
            }
        });

        if (span > 0) {
            createEmptySpace(parent, span);
        }

        return parent;
    }

    public T getEntity() {
        return entity;
    }

    /**
     * Create new entity (without save)
     */
    protected abstract void createEntity();

    protected void setEntityProperties() {
        entity.setDescription(StringUtils.trimToEmpty(description));
    }

    @Override
    protected void okPressed() {
        createEntity();
        if (entity != null) {
            setEntityProperties();
        }
        super.okPressed();
    }

    @Override
    public void validateEntityName(String entityName) throws Exception {
        if (EntityNameController.getInstance().isNameExisted(parentFolder, entityName)) {
            throw new InvalidNameException(StringConstants.DIA_NAME_EXISTED);
        }
    }

}
