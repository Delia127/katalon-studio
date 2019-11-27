package com.kms.katalon.activation.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.handlers.DeactivateHandler;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.StringConstants;

public class WarningReactivateDialog extends AbstractDialog {

    private String message;

    public WarningReactivateDialog(Shell parentShell, String message) {
        super(parentShell);
        this.message = message;
    }

    @Override
    protected void registerControlModifyListeners() {

    }

    @Override
    protected void setInput() {
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 10;
        gridLayout.marginHeight = 10;
        gridLayout.marginLeft = 5;
        gridLayout.marginRight = 5;
        composite.setLayout(gridLayout);

        Label lblImg = new Label(composite, SWT.NONE);
        Image img = ImageManager.getImage(IImageKeys.WARNING_20);
        lblImg.setImage(img);

        Label lblText = new Label(composite, SWT.WRAP);
        lblText.setText(message);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, StringConstants.BTN_REACTIVATE, true);
        createButton(parent, IDialogConstants.CANCEL_ID, StringConstants.BTN_CANCEL, false);
    }

    @Override
    protected void okPressed() {
        super.close();
        new DeactivateHandler().execute();
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.TITLE_WARNING;
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y);
    }
}
