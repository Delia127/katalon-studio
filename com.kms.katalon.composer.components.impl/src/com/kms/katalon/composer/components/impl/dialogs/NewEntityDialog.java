package com.kms.katalon.composer.components.impl.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewEntityDialog extends TitleAreaDialog {

    private String name;

    private String lblName = StringConstants.DIA_LBL_NAME;

    private String dialogTitle = "";

    private String dialogMsg = StringConstants.DIA_LBL_CREATE_NEW;

    private Text txtName;

    private Composite container;

    private FolderEntity parentFolder;

    private boolean isFileCreating = true;

    public NewEntityDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        this.parentFolder = parentFolder;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Set window title for dialog
        if (getShell() != null) getShell().setText(StringConstants.DIA_WINDOW_TITLE_NEW);

        Composite area = (Composite) super.createDialogArea(parent);
        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(2, false));
        Label labelName = new Label(container, SWT.NONE);
        labelName.setText(getLblName());

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtName.setText(getName());
        txtName.selectAll();

        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setName(((Text) e.getSource()).getText());
                updateStatus();
            }
        });

        // Build the separator line
        Label separator = new Label(area, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }

    private void updateStatus() {
        super.getButton(OK).setEnabled(isValidEntityName());
    }

    private boolean isValidEntityName() {
        String entityName = getName();
        try {
            if (StringUtils.isBlank(entityName)) {
                throw new InvalidNameException(StringConstants.DIA_NAME_CANNOT_BE_BLANK_OR_EMPTY);
            }
            
            EntityNameController.getInstance().validateName(entityName);
            
            if (!StringUtils.equalsIgnoreCase(EntityNameController.getInstance().getAvailableName(entityName,
                    parentFolder, !isFileCreating()), entityName)) {
                throw new InvalidNameException(StringConstants.DIA_NAME_EXISTED);
            }
            
            setErrorMessage(null);
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return false;
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        updateStatus();
    }

    @Override
    protected Point getInitialSize() {
        Point initSize = super.getInitialSize();
        return new Point(initSize.x, 250);
    }

    @Override
    public void create() {
        super.create();
        setTitle(getDialogTitle());
        setMessage(getDialogMsg(), IMessageProvider.INFORMATION);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            // trim and replace multiple space by single one
            name = name.trim().replaceAll("\\s+", " ");
        }
        this.name = name;
    }

    public String getLblName() {
        return lblName;
    }

    public void setLblName(String lblName) {
        this.lblName = lblName;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getDialogMsg() {
        return dialogMsg;
    }

    public void setDialogMsg(String dialogMsg) {
        this.dialogMsg = dialogMsg;
    }

    public boolean isFileCreating() {
        return isFileCreating;
    }

    public void setFileCreating(boolean isFileCreating) {
        this.isFileCreating = isFileCreating;
    }

}
