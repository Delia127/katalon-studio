package com.kms.katalon.composer.components.impl.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;

public class AddMailRecipientDialog extends Dialog {
    private Text text;
    private ControlDecoration controlDecoration;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    
    private List<String> emails;

    public AddMailRecipientDialog(Shell parentShell) {
        super(parentShell);
        pattern = Pattern.compile(EMAIL_PATTERN);
        emails = new ArrayList<String>();
    }

    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.horizontalSpacing = 20;
        gridLayout.numColumns = 2;

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText(StringConstants.DIA_LBL_EMAIL);

        text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                // TODO Auto-generated method stub
                validateEmail();
            }
        });

        controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
        Image imgInfo = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
        controlDecoration.setImage(imgInfo);
        controlDecoration.show();
        controlDecoration.setShowHover(true);
        controlDecoration.setDescriptionText(StringConstants.DIA_DESC_PLS_ENTER_EMAIL_ADDR);

        return container;
    }

    private void validateEmail() {
        for (String email : text.getText().split(";")) {
            if (!pattern.matcher(email.trim()).matches() && !email.trim().isEmpty()) {
                super.getButton(OK).setEnabled(false);
                showErrorValidator();
                return;
            }
        }
        super.getButton(OK).setEnabled(true);
        controlDecoration.hide();
    }

    private void showErrorValidator() {
        Image imgInfo = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage();
        controlDecoration.setImage(imgInfo);
        controlDecoration.setShowHover(true);
        controlDecoration.setDescriptionText(StringConstants.DIA_DESC_INVALID_EMAIL_ADDR);
        controlDecoration.show();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 150);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_ADD_EMAIL_RECIPIENT);
    }
    
    public String[] getEmails() {
        return emails.toArray(new String[0]);
    }
    
    protected void okPressed() {
        for (String email : text.getText().split(";")) {
            emails.add(email.trim());
        }
        super.okPressed();
    }
}
