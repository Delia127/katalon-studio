package com.kms.katalon.activation.dialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.ServerAPICommunicationUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.util.collections.Pair;

public class SignupSurveyDialog extends AbstractDialog {

    private static final String[] USER_ROLE_OPTIONS = new String[] { 
            "Project manager",
            "Automation test expert",
            "Automation tester",
            "Manual tester",
            "Software engineer", 
            "Other"
        };

    private static final String[] DOWNLOAD_PURPOSE_OPTIONS = new String[] {
            "Tool research for my project",
            "Learn automation test",
            "Tool trial",
            "Other"
        };

    private List<String> signupAnwsers = new ArrayList<>(2);

    private CCombo cbxFirstQuestion, cbxSecondQuestion;

    public SignupSurveyDialog(Shell parentShell) {
        super(parentShell, false);
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout();
        glContainer.verticalSpacing = 15;
        container.setLayout(glContainer);

        Label lblQuestionMsg = new Label(container, SWT.NONE);
        lblQuestionMsg.setText(MessageConstants.SignupSurveyDialog_LBL_SURVEY_HEADLINE);
        lblQuestionMsg.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        Composite questionComposite = new Composite(container, SWT.NONE);
        questionComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
        questionComposite.setLayout(new GridLayout(2, false));

        Label lblFirstQuestion = new Label(questionComposite, SWT.NONE);
        lblFirstQuestion.setText(MessageConstants.SignupSurveyDialog_LBL_QUESTION_FOR_USER_ROLE);
        lblFirstQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        cbxFirstQuestion = new CCombo(questionComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData gdCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdCombo.heightHint = 22;
        cbxFirstQuestion.setLayoutData(gdCombo);
        cbxFirstQuestion.setItems(USER_ROLE_OPTIONS);
        cbxFirstQuestion.setText(MessageConstants.SignupSurveyDialog_CBX_DEFAULT_OPTION);

        Label lblSecondQuestion = new Label(questionComposite, SWT.NONE);
        lblSecondQuestion.setText(MessageConstants.SignupSurveyDialog_LBL_QUESTION_FOR_DOWNLOAD_PURPOSE);
        lblSecondQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        cbxSecondQuestion = new CCombo(questionComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cbxSecondQuestion.setLayoutData(gdCombo);
        cbxSecondQuestion.setItems(DOWNLOAD_PURPOSE_OPTIONS);
        cbxSecondQuestion.setText(MessageConstants.SignupSurveyDialog_CBX_DEFAULT_OPTION);

        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite buttonBar = new Composite(parent, SWT.NONE);
        buttonBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        buttonBar.setLayout(new GridLayout());

        createButtonsForButtonBar(buttonBar);
        return buttonBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, MessageConstants.SignupSurveyDialog_BTN_EXPLORE_KATALON, true);
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.SignupSurveyDialog_DIA_TITLE;
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y);
    }

    public List<String> getSignupAnwser() {
        return signupAnwsers;
    }

    @Override
    protected void okPressed() {
        sendSignupAnwsers();

        super.okPressed();
    }

    public String getUrlEncodedSignupAnwser(String title, String role) throws UnsupportedEncodingException {
        List<Pair<String, String>> formDataInfo = Arrays.asList(
                Pair.of("email", ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL)),
                Pair.of("event", "Katalon Studio Download Survey Submitted"), Pair.of("properties[title]", title),
                Pair.of("properties[downloadPurpose]", role));

        StringBuilder urlEncodedBuilder = new StringBuilder();
        String charset = "utf-8";
        for (Pair<String, String> p : formDataInfo) {
            if (urlEncodedBuilder.length() > 0) {
                urlEncodedBuilder.append("&");
            }
            urlEncodedBuilder.append(String.format("%s=%s", URLEncoder.encode(p.getLeft(), charset),
                    URLEncoder.encode(p.getRight(), charset)));
        }
        return urlEncodedBuilder.toString();
    }

    private void sendSignupAnwsers() {
        String title = cbxFirstQuestion.getSelectionIndex() >= 0 ? cbxFirstQuestion.getText() : StringUtils.EMPTY;
        String role = cbxSecondQuestion.getSelectionIndex() >= 0 ? cbxSecondQuestion.getText() : StringUtils.EMPTY;
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(role)) {
            return;
        }
        try {
            String data = getUrlEncodedSignupAnwser(title, role);

            Executors.newSingleThreadExecutor().submit(() -> {
                ServerAPICommunicationUtil.invokeFormEncoded(ServerAPICommunicationUtil.getAPIUrl() + "/user/event",
                        "POST", data);
                return null;
            });
        } catch (UnsupportedEncodingException e) {
            LoggerSingleton.logError(e);
        }
    }

}
