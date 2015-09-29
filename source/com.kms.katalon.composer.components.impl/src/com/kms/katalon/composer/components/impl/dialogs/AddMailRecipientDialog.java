package com.kms.katalon.composer.components.impl.dialogs;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
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
    private static final String EMAIL_TEXT_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    
    private Set<String> emails;
    private String[] existedEmails;

    public AddMailRecipientDialog(Shell parentShell, String[] existedEmails) {
        super(parentShell);
        pattern = Pattern.compile(EMAIL_TEXT_PATTERN);
        emails = new LinkedHashSet<String>();
        
        if (existedEmails != null) {
            Arrays.sort(existedEmails);
            this.existedEmails = existedEmails;
        } else {
            this.existedEmails = new String[0]; 
        }
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
        GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_text.heightHint = 18;
        text.setLayoutData(gd_text);
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
        StringBuilder duplicatedEmailBuilder = new StringBuilder();
        
        for (String email : text.getText().replace(" ", "").split(";")) {
            String emailName = email.trim();
            if (!pattern.matcher(emailName).matches() && !emailName.isEmpty()) {
                super.getButton(OK).setEnabled(false);
                showErrorValidator();
                return;
            } else {
                
                if (Arrays.binarySearch(existedEmails, emailName) >= 0) {
                    if (duplicatedEmailBuilder.length() > 0) {
                        duplicatedEmailBuilder.append(", ");
                    }
                    duplicatedEmailBuilder.append(emailName);
                }
            }
        }
        
        String dupplicatedNames = duplicatedEmailBuilder.toString();
        if (!dupplicatedNames.isEmpty()) {
            super.getButton(OK).setEnabled(false);
            showDuplicatedValidator(dupplicatedNames);
            return;
        } else {
            super.getButton(OK).setEnabled(true);
            controlDecoration.hide();
        }
    }

    private void showErrorValidator() {
        Image imgInfo = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage();
        controlDecoration.setImage(imgInfo);
        controlDecoration.setShowHover(true);
        controlDecoration.setDescriptionText(StringConstants.DIA_DESC_INVALID_EMAIL_ADDR);
        controlDecoration.show();
    }
    
    private void showDuplicatedValidator(String dupplicatedNames) {
        Image imgInfo = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                .getImage();
        controlDecoration.setImage(imgInfo);
        controlDecoration.setShowHover(true);
        controlDecoration.setDescriptionText(MessageFormat.format(StringConstants.DIA_DESC_DUPLICATED_EMAIL_ADDR, dupplicatedNames));
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
        for (String email : text.getText().replace(" ", "").split(";")) {
            emails.add(email.trim());
        }
        super.okPressed();
    }
}
