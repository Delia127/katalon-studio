package com.kms.katalon.plugin.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.tracking.service.Trackings;

public class FirstTimeUseDialog extends MessageDialog {

    private Composite leaveReasonComposite;

    private Text txtReason;

    private boolean willContinueToUse = true;

    public FirstTimeUseDialog(Shell parentShell) {
        super(parentShell, StringConstants.TITLE_DIALOG_WILL_CONTINUE_TO_USE, null,
                StringConstants.MSG_QUESTION_WILL_CONTINUE_TO_USE, MessageDialog.QUESTION, 0, StringConstants.BTN_YES,
                StringConstants.BTN_NO);
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        leaveReasonComposite = createLeaveReasonComposite(parent);
        return leaveReasonComposite;
    }

    private Composite createLeaveReasonComposite(Composite parent) {
        Composite leaveReasonComposite = new Composite(parent, SWT.NONE);
        GridLayout glLeaveReason = new GridLayout(1, true);
        leaveReasonComposite.setLayout(glLeaveReason);
        GridData gdLeaveReason = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gdLeaveReason.exclude = true;
        leaveReasonComposite.setLayoutData(gdLeaveReason);

        Label lblInputReason = new Label(leaveReasonComposite, SWT.WRAP);
        GridData gdLblInputReason = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblInputReason.setLayoutData(gdLblInputReason);
        lblInputReason.setText(StringConstants.MSG_QUIT_USING_KATALON_REASON);

        txtReason = new Text(leaveReasonComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        GridData gdInputReason = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdInputReason.minimumHeight = 100;
        txtReason.setLayoutData(gdInputReason);
        txtReason.setMessage(StringConstants.MSG_QUIT_USING_KATALON_REASON_PLACEHOLDER);

        return leaveReasonComposite;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == MessageDialog.OK) {
            if (willContinueToUse) { // BTN_YES
                Trackings.trackInAppSurveyWillContinueToUse(true, null);
            } else { // BTN_SEND
                if (StringUtils.isBlank(txtReason.getText())) {
                    return;
                }
                Trackings.trackInAppSurveyWillContinueToUse(false, txtReason.getText());
            }
            ApplicationInfo.setBooleanAppProperty(ApplicationStringConstants.DONE_FIRST_TIME_USE_SURVEY_PROP_NAME, true,
                    true);
            close();
        } else {
            willContinueToUse = false;
            showReasonComposite();
        }
    }

    private void hideReasonComposite() {
        leaveReasonComposite.setVisible(false);
        GridData gdLeaveReason = (GridData) leaveReasonComposite.getLayoutData();
        gdLeaveReason.exclude = true;
        updateDialogLayout();
    }

    private void showReasonComposite() {
        leaveReasonComposite.setVisible(true);
        GridData gdLeaveReason = (GridData) leaveReasonComposite.getLayoutData();
        gdLeaveReason.exclude = false;
        updateDialogLayout();
    }
    
    private void updateDialogLayout() {
        updateButtons();
        leaveReasonComposite.requestLayout();
        leaveReasonComposite.getParent().pack();
        leaveReasonComposite.getParent().getParent().pack();
    }

    private void updateButtons() {
        boolean isShowReasonInput = !willContinueToUse;
        Button btnOK = getButton(0);
        Button btnCancel = getButton(1);
        if (isShowReasonInput) {
            btnOK.setText(StringConstants.BTN_SEND);
             ((GridData) btnCancel.getLayoutData()).exclude = true;
            updateButtonsLayout(1);
        } else {
            btnOK.setText(StringConstants.BTN_YES);
             ((GridData) btnCancel.getLayoutData()).exclude = false;
            updateButtonsLayout(2);
        }
    }

    private void updateButtonsLayout(int numButtons) {
        Composite buttonContainer = getButton(0).getParent();
        ((GridLayout) buttonContainer.getLayout()).numColumns = numButtons;
        buttonContainer.pack();
        buttonContainer.getParent().pack();
    }

    protected boolean isResizable() {
        return true;
    }
}
