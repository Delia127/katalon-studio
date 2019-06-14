package com.kms.katalon.activation.dialog;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class SignupSurveyDialog extends AbstractDialog {
    
    private static final String HUBSPOT_STAGING_API_KEY = "852e9ada-878f-4b06-8026-de8b1879ec27";
    
    private static final String HUBSPOT_PRODUCTION_API_KEY = "1e2da5ad-ed21-43c4-86d8-ccda6ee6f7a6";

    private static final String[] USER_ROLE_OPTIONS = new String[] { 
            "Manual tester",
            "Automation tester",
            "Software engineer",
            "QA manager",
            "Project manager"
        };

    private static final String[] DOWNLOAD_PURPOSE_OPTIONS = new String[] {
            "Learn automation testing",
            "Use as a required automation tool",
            "Check out and evaluate the tool"
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

        Label lblQuestionMsg = new Label(container, SWT.WRAP);
        lblQuestionMsg.setText(MessageConstants.SignupSurveyDialog_LBL_SURVEY_HEADLINE);
        lblQuestionMsg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite questionComposite = new Composite(container, SWT.NONE);
        questionComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
        return new Point(Math.max(400, initialSize.x), initialSize.y);
    }

    public List<String> getSignupAnwser() {
        return signupAnwsers;
    }

    @Override
    protected void okPressed() {
        sendSignupAnswers();

        super.okPressed();
    }
    
    private void sendSignupAnswers() {
        try {
            String title = cbxFirstQuestion.getSelectionIndex() >= 0 ? cbxFirstQuestion.getText() : StringUtils.EMPTY;
            String downloadPurpose = cbxSecondQuestion.getSelectionIndex() >= 0 ? cbxSecondQuestion.getText() : StringUtils.EMPTY;
            String content = getHubspotUpdateAccountContent(title, downloadPurpose);
            
            String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            String url = getHubspotAccountUpdateUrl(email);
            HttpPost post = new HttpPost(url);
            StringEntity requestEntity = new StringEntity(content);
            post.setEntity(requestEntity);
            post.setHeader("Content-type", "application/json");
            
            CloseableHttpClient client = getHttpClient();
            CloseableHttpResponse response = client.execute(post);
            LoggerSingleton.logInfo("Signup Survey Response Status Code: " + response.getStatusLine().getStatusCode());
            LoggerSingleton.logInfo("Signup Survey Response Reason: " + response.getStatusLine().getReasonPhrase());
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseContent = EntityUtils.toString(responseEntity);
                LoggerSingleton.logInfo("Singup Survey Response Content: " + responseContent);
            }
            
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(response);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
    
    private String getHubspotUpdateAccountContent(String title, String downloadPurpose) {
        List<HubspotAccountProperty> properties = new ArrayList<>();
        
        HubspotAccountProperty jobTitleProperty = new HubspotAccountProperty();
        jobTitleProperty.setProperty("jobtitle");
        jobTitleProperty.setValue(title);
        properties.add(jobTitleProperty);
        
        HubspotAccountProperty downloadPurposeProperty = new HubspotAccountProperty();
        downloadPurposeProperty.setProperty("download_purpose");
        downloadPurposeProperty.setValue(downloadPurpose);
        properties.add(downloadPurposeProperty);
        
        HubspotUpdateAccountPayload payload = new HubspotUpdateAccountPayload();
        payload.setProperties(properties);
        
        String data = JsonUtil.toJson(payload);
        return data;
    }
    
    private CloseableHttpClient getHttpClient() throws URISyntaxException, IOException, GeneralSecurityException {
        return HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation()).getClientBuilder().build();
    }
    
    private String getHubspotAccountUpdateUrl(String email) {
        return String.format("https://api.hubapi.com/contacts/v1/contact/createOrUpdate/email/%s/?hapikey=%s", email,
                getHubspotApiKey());
    }

    private String getHubspotApiKey() {
        if (VersionUtil.isStagingBuild() || VersionUtil.isDevelopmentBuild()) {
            return HUBSPOT_STAGING_API_KEY;
        } else {
            return HUBSPOT_PRODUCTION_API_KEY;
        }
    }
    
    private class HubspotUpdateAccountPayload {
        private List<HubspotAccountProperty> properties;

        public List<HubspotAccountProperty> getProperties() {
            return properties;
        }

        public void setProperties(List<HubspotAccountProperty> properties) {
            this.properties = properties;
        } 
    }
    
    private class HubspotAccountProperty {
        private String property;
        
        private String value;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
