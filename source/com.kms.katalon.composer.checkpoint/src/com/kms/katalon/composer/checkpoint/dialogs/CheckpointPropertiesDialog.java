package com.kms.katalon.composer.checkpoint.dialogs;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;

public class CheckpointPropertiesDialog extends CommonPropertiesDialog<CheckpointEntity> {

    private Text txtTakenDate;

    public CheckpointPropertiesDialog(Shell parentShell, CheckpointEntity entity) {
        super(parentShell, entity);
        setDialogTitle(StringConstants.DIA_TITLE_CHECKPOINT_PROPERTIES);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = (Composite) super.createDialogContainer(parent);

        Label lblTag = new Label(container, SWT.NONE);
        lblTag.setText(StringConstants.DIA_LBL_TAKEN_DATE);

        txtTakenDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtTakenDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setInput() {
        super.setInput();
        txtTakenDate.setText(ObjectUtils.toString(getEntity().getTakenDate()));
    }

}
