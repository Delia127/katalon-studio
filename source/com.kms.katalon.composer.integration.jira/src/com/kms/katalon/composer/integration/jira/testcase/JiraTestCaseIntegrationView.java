package com.kms.katalon.composer.integration.jira.testcase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class JiraTestCaseIntegrationView extends AbstractTestCaseIntegrationView implements JiraUIComponent {

    private Composite container;

    private Link lblDisplayKey;

    private Label lblDisplaySummary, lblDisplayStatus, lblDisplayDiscription;

    private JiraIssue jiraIssue;

    public JiraTestCaseIntegrationView(TestCaseEntity testCaseEntity, MPart mpart) {
        super(testCaseEntity, mpart);
        jiraIssue = JiraObjectToEntityConverter.getJiraIssue(testCaseEntity);
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public Composite createContainer(Composite parent) {
        container = new Composite(parent, SWT.BORDER);
        container.setBackground(ColorUtil.getWhiteBackgroundColor());
        container.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 10;
        gridLayout.horizontalSpacing = 15;
        container.setLayout(gridLayout);

        Label lblKey = new Label(container, SWT.NONE);
        lblKey.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblKey.setText(ComposerJiraIntegrationMessageConstant.VIEW_LBL_KEY);

        lblDisplayKey = new Link(container, SWT.NONE);
        lblDisplayKey.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDisplayKey.setToolTipText(ComposerJiraIntegrationMessageConstant.VIEW_TOOLTIP_VIEW_ISSUE_ON_JIRA);

        Label lblSummary = new Label(container, SWT.NONE);
        lblSummary.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSummary.setText(StringConstants.SUMMARY);

        lblDisplaySummary = new Label(container, SWT.WRAP);
        lblDisplaySummary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStatus = new Label(container, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblStatus.setText(StringConstants.STATUS);

        lblDisplayStatus = new Label(container, SWT.NONE);
        lblDisplayStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(StringConstants.DESCRIPTION);

        lblDisplayDiscription = new Label(container, SWT.WRAP);
        lblDisplayDiscription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setInput();

        registerControlListeners();

        return container;
    }

    private void registerControlListeners() {
        lblDisplayKey.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                try {
                    Program.launch(getHTMLLink(jiraIssue).toURL().toString());
                } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }

    private void setInput() {
        if (jiraIssue == null) {
            ControlUtils.recursiveSetEnabled(container, false);
            return;
        }
        lblDisplayKey.setText("<a>" + jiraIssue.getKey() + "</a>");

        Issue fields = jiraIssue.getFields();
        lblDisplaySummary.setText(StringUtils.defaultString(fields.getSummary()));
        lblDisplayStatus.setText(StringUtils.defaultString(fields.getStatus().getName()));
        lblDisplayDiscription.setText(StringUtils.defaultString(fields.getDescription()));
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }
    
    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.TEST_CASE_INTEGRATION_JIRA;
    }
}
