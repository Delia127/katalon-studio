package com.kms.katalon.composer.global.dialog;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.TitleAreaDialog;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileNameDialog extends TitleAreaDialog {

    // Controls
    private Text txtName;

    private String newName;

    private List<ExecutionProfileEntity> siblingProfiles;

    private String title;

    public ExecutionProfileNameDialog(Shell parentShell, String newName, List<ExecutionProfileEntity> siblingProfiles,
            String title) {
        super(parentShell);
        this.newName = newName;
        this.siblingProfiles = siblingProfiles;
        this.title = title;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        container.setLayout(layout);

        Label lblName = new Label(container, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblName.setText(GlobalStringConstants.NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        setInput();
        registerControlModifyListeners();
        return container;
    }

    private void setInput() {
        txtName.setText(newName);
        txtName.setFocus();
        txtName.selectAll();
        setMessage(title, IMessageProvider.INFORMATION);
    }

    private void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent event) {
                newName = txtName.getText();
                checkNewName(newName);
            }
        });
    }

    @Override
    protected Point getInitialSize() {
        return getShell().computeSize(400, SWT.DEFAULT, true);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected int getShellStyle() {
        return SWT.SHELL_TRIM;
    }

    @Override
    protected void okPressed() {
        this.newName = txtName.getText();
        super.okPressed();
    }

    public String getNewName() {
        return newName;
    }

    private boolean checkNewName(String newName) {
        Button button = getButton(ExecutionProfileNameDialog.OK);
        if (StringUtils.isEmpty(newName)) {
            button.setEnabled(false);
            return false;
        }
        boolean dupplicated = siblingProfiles.stream().filter(p -> p.getName().equals(newName)).count() > 0;
        if (dupplicated) {
            setMessage(StringConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
        } else {
            setMessage(title, IMessageProvider.INFORMATION);
        }
        button.setEnabled(!dupplicated);
        return !dupplicated;
    }
}
