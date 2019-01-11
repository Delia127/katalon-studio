package com.kms.katalon.platform.internal.report.viewer;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.report.TestStepRecord;
import com.katalon.platform.ui.viewer.HoveredImageColumnLabelProvider;
import com.kms.katalon.core.logging.model.AbstractLogRecord;
import com.kms.katalon.platform.internal.report.TestStepRecordImpl;

public class TestStepCellDecorator extends HoveredImageColumnLabelProvider<AbstractLogRecord> {

    private CellDecorator<TestStepRecord> decorator;

    public TestStepCellDecorator(int columnIndex, CellDecorator<TestStepRecord> decorator) {
        super(columnIndex);
        this.decorator = decorator;
    }

    @Override
    protected Class<AbstractLogRecord> getElementType() {
        return AbstractLogRecord.class;
    }

    @Override
    protected Image getImage(AbstractLogRecord element) {
        return decorator != null ? decorator.getImage(new TestStepRecordImpl(element)) : null;
    }

    @Override
    protected String getText(AbstractLogRecord element) {
        return decorator != null ? decorator.getText(new TestStepRecordImpl(element)) : "";
    }

    @Override
    protected Image getHoveredImage(AbstractLogRecord element) {
        return decorator != null ? decorator.getHoveredImage(new TestStepRecordImpl(element)) : null;
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        if (decorator != null) {
            decorator.onMouseDownEvent(e, getTestStepRecord((AbstractLogRecord) cell.getElement()));
        }
    }

    private TestStepRecord getTestStepRecord(AbstractLogRecord element) {
        return new TestStepRecordImpl(element);
    }

    @Override
    protected String getElementToolTipText(AbstractLogRecord element) {
        if (decorator != null) {
            return decorator.getToolTip(getTestStepRecord(element));
        }
        return "";
    }

    public void setCellDecorator(CellDecorator<TestStepRecord> cellDecorator) {
        this.decorator = cellDecorator;
    }

    @Override
    protected boolean shouldShowCursor(ViewerCell cell, Point currentMouseLocation) {
        boolean intercept = super.shouldShowCursor(cell, currentMouseLocation);
        if (!intercept) {
            return false;
        }
        if (decorator != null) {
            return decorator.showCursorOnHover(getTestStepRecord((AbstractLogRecord) cell.getElement()));
        }
        return true;
    }
}
