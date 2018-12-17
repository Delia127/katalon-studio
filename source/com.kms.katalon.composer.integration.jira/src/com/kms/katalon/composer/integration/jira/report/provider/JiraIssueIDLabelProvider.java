package com.kms.katalon.composer.integration.jira.report.provider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class JiraIssueIDLabelProvider extends HyperLinkColumnLabelProvider<JiraIssue> implements JiraUIComponent {

    public JiraIssueIDLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        JiraIssue jiraIssue = (JiraIssue) cell.getElement();
        try {
            Program.launch(getHTMLLink(jiraIssue).toURL().toString());
        } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    protected Class<JiraIssue> getElementType() {
        return JiraIssue.class;
    }

    @Override
    protected Image getImage(JiraIssue element) {
        return null;
    }

    @Override
    protected String getText(JiraIssue element) {
        return element.getKey();
    }

    @Override
    protected String getElementToolTipText(JiraIssue element) {
        try {
            return getHTMLLink(element).toString();
        } catch (URISyntaxException | IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
