package com.kms.katalon.composer.components.impl.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.entity.file.FileEntity;

public class CommonPropertiesDialog<T extends FileEntity> extends AbstractDialog {

    private boolean isModified;

    private T entity;

    protected Text txtId;

    protected Text txtName;

    protected Text txtDescription;

    protected ModifyListener modifyListener;

    public CommonPropertiesDialog(Shell parentShell, T entity) {
        super(parentShell);
        this.entity = entity;
    }

    @Override
    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | getDefaultOrientation();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout(1, false);
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        main.setLayout(glMain);

        Composite container = new Composite(main, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblId = new Label(container, SWT.NONE);
        lblId.setText(StringConstants.ID);

        txtId = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblName = new Label(container, SWT.NONE);
        lblName.setText(StringConstants.NAME);

        txtName = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(StringConstants.DESCRIPTION);

        txtDescription = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridData descLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        descLayoutData.heightHint = 80;
        txtDescription.setLayoutData(descLayoutData);

        return container;
    }

    @Override
    protected void setInput() {
        setModified(false);
        txtId.setText(getEntity().getIdForDisplay());
        txtName.setText(getEntity().getName());
        txtDescription.setText(getEntity().getDescription());
    }

    @Override
    protected void registerControlModifyListeners() {
        modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setModified(true);
            }
        };

        txtDescription.addModifyListener(modifyListener);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }

    @Override
    protected void okPressed() {
        updateChanges();
        super.okPressed();
    }

    protected void updateChanges() {
        if (!isModified()) {
            return;
        }
        getEntity().setDescription(StringUtils.trimToEmpty(txtDescription.getText()));
    }

    public T getEntity() {
        return entity;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }

}
