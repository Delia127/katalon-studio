package com.kms.katalon.composer.integration.qtest.activation.dialog;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.integration.qtest.helper.ActivationPayload;

public class QTestActivationSuccessDialog extends AbstractDialog {

    private ActivationPayload activationPayload;

    private Label txtExpDate;

    private Label txtProducts;

    public QTestActivationSuccessDialog(Shell parentShell, ActivationPayload payload) {
        super(parentShell);
        this.activationPayload = payload;
    }

    @Override
    public String getDialogTitle() {
        return ComposerIntegrationQtestMessageConstants.DIA_TITLE_LICENSE_INFORMATION;
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(activationPayload.getExp().getTime()), ZoneId.systemDefault());
        txtExpDate.setText(localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        txtProducts.setText(
                GlobalStringConstants.APP_NAME + " " + ApplicationInfo.versionNo() + "." + ApplicationInfo.buildNo());
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 10;
        composite.setLayout(gridLayout);

        Label lblMessage = new Label(composite, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        lblMessage.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_MSG_ACTIVATED_SUCCESSFULLY);

        Label lblExpDate = new Label(composite, SWT.NONE);
        lblExpDate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblExpDate.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_EXPIRATION_DATE);

        txtExpDate = new Label(composite, SWT.NONE);
        txtExpDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblProduct = new Label(composite, SWT.NONE);
        lblProduct.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblProduct.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_PRODUCT);

        txtProducts = new Label(composite, SWT.NONE);
        txtProducts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, super.getInitialSize().y);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

}
