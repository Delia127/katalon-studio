package com.kms.katalon.composer.integration.cucumber.dialog;

import java.util.List;

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
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.entity.file.FeatureEntity;

public class RenameFeatureEntityDialog extends TitleAreaDialog {

    private Text txtName;

    private List<FeatureEntity> siblingFeatures;

    private String newName;

    private FeatureEntity currentFeature;

    public RenameFeatureEntityDialog(Shell parentShell, FeatureEntity listenerEntity,
            List<FeatureEntity> siblingListeners) {
        super(parentShell);
        this.siblingFeatures = siblingListeners;
        this.currentFeature = listenerEntity;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite nameComposite = new Composite(container, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        nameComposite.setLayout(layout);

        Label lblName = new Label(nameComposite, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblName.setText(GlobalMessageConstants.NAME);

        txtName = new Text(nameComposite, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtName.minimumWidth = 200;
        txtName.setLayoutData(gdTxtName);

        setInput();
        registerControlModifyListeners();

        parent.pack();
        return container;
    }

    private void setInput() {
        txtName.setText(currentFeature.getName());
        txtName.selectAll();
        txtName.forceFocus();
        setMessage("Rename Feature file", IMessageProvider.INFORMATION);
    }

    private boolean isNameDupplicated(String newName) {
        return this.siblingFeatures.parallelStream().filter(l -> l.getName().equals(newName)).findAny().isPresent();
    }

    private void checkNewName(String newName) {
        if (isNameDupplicated(newName)) {
            setMessage(StringConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
            return;
        }

        try {
            EntityNameController.getInstance().validateName(newName);
            setMessage("Rename Feature file", IMessageProvider.INFORMATION);
            getButton(OK).setEnabled(true);
        } catch (Exception e) {
            setMessage(e.getMessage(), IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
        }
    }

    protected void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                checkNewName(txtName.getText());
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Rename Feature File");
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

    @Override
    protected Point getInitialSize() {
       return new Point(400, 200);
    }
    
    public String getNewName() {
        return newName;
    }
}
