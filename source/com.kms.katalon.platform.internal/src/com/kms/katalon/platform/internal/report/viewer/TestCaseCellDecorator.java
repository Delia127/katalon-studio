package com.kms.katalon.platform.internal.report.viewer;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.report.TestCaseRecord;
import com.kms.katalon.composer.components.impl.providers.HoveredImageColumnLabelProvider;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.platform.internal.report.TestCaseRecordImpl;

public class TestCaseCellDecorator extends HoveredImageColumnLabelProvider<TestCaseLogRecord> {

    private CellDecorator<TestCaseRecord> decorator;

    public TestCaseCellDecorator(int index, CellDecorator<TestCaseRecord> decorator) {
        super(index);
        this.decorator = decorator;
    }

    @Override
    protected Class<TestCaseLogRecord> getElementType() {
        return TestCaseLogRecord.class;
    }

    @Override
    protected Image getImage(TestCaseLogRecord element) {
        return decorator.getImage(new TestCaseRecordImpl(element));
    }

    @Override
    protected String getText(TestCaseLogRecord element) {
        return decorator.getText(new TestCaseRecordImpl(element));
    }

    @Override
    protected Image getHoveredImage(TestCaseLogRecord element) {
        return decorator.getHoveredImage(new TestCaseRecordImpl(element));
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        decorator.onMouseDownEvent(e, new TestCaseRecordImpl((TestCaseLogRecord) cell.getElement()));
    }

    @Override
    protected String getElementToolTipText(TestCaseLogRecord element) {
        return decorator.getToolTip(getTestCaseRecord(element));
    }

    private TestCaseRecord getTestCaseRecord(TestCaseLogRecord element) {
        return new TestCaseRecordImpl((TestCaseLogRecord) element);
    }

    @Override
    protected boolean shouldShowCursor(ViewerCell cell, Point currentMouseLocation) {
        boolean intercept = super.shouldShowCursor(cell, currentMouseLocation);
        if (!intercept) {
            return false;
        }
        if (decorator != null) {
            return decorator.showCursorOnHover(getTestCaseRecord((TestCaseLogRecord) cell.getElement()));
        }
        return true;
    }
}
