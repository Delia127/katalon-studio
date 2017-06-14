package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;

public class MissingMobileDriverWarningDialog extends MessageDialog {

    public MissingMobileDriverWarningDialog(Shell parentShell, String dialogTitle, String dialogMessage) {
        super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.WARNING,
                new String[] { IDialogConstants.OK_LABEL }, 0);
        int style = SWT.NONE;
        style &= SWT.SHEET;
        setShellStyle(getShellStyle() | style);
    }

    @Override
    public Control createMessageArea(Composite composite) {

        Image image = getImage();
        if (image != null) {
            imageLabel = new Label(composite, SWT.NULL);
            image.setBackground(imageLabel.getBackground());
            imageLabel.setImage(image);
            justify(imageLabel);
        }

        if (message != null) {
            messageLabel = new Label(composite, getMessageLabelStyle());
            messageLabel.setText(message + " " + ComposerComponentsImplMessageConstants.SETUP_LINK);
            justify(messageLabel, true, false);
        }

        // Empty image
        imageLabel = new Label(composite, SWT.NULL);
        imageLabel.setImage(null);
        justify(imageLabel);

        final String howToInstallAppiumUrl = ComposerComponentsImplMessageConstants.LINK;
        Link link = new Link(composite, SWT.NONE);
        link.setText("<a href=\"" + howToInstallAppiumUrl + "\">" + howToInstallAppiumUrl + "</a>");
        link.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(howToInstallAppiumUrl);
            }
        });

        return composite;
    }

    private void justify(Control control, boolean grabHorizontal, boolean grabVertical) {
        GridDataFactory.fillDefaults()
                .align(SWT.FILL, SWT.BEGINNING)
                .grab(grabHorizontal, grabVertical)
                .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                .applyTo(control);
    }

    private void justify(Control control) {
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(control);
    }

    public static void showWarning(Shell parentShell, String dialogMessage) {
        MissingMobileDriverWarningDialog dialog = new MissingMobileDriverWarningDialog(parentShell,
                StringConstants.WARN, dialogMessage);
        dialog.open();
    }
}
