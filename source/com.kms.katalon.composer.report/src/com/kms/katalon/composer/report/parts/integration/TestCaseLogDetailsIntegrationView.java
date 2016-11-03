package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.kms.katalon.composer.report.parts.ReportPart;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestCaseLogDetailsIntegrationView {
    protected ReportEntity reportEntity;

    protected MPart mpart;

    public TestCaseLogDetailsIntegrationView(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    public abstract Composite createContainer(Composite parent);

    public abstract void changeTestCase(TestCaseLogRecord testCaseLogRecord);

    public abstract void createTableContextMenu(Menu parentMenu, ISelection selection);

    public void setDirty(boolean dirty) {
        ReportPart integrationPart = (ReportPart) mpart.getObject();
        integrationPart.setDirty(dirty);
    }
}
