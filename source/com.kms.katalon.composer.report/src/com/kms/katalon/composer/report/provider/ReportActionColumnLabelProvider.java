package com.kms.katalon.composer.report.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.DefaultCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportItemDescription;

public class ReportActionColumnLabelProvider extends HyperLinkColumnLabelProvider<ReportItemDescription>
        implements ReportItemDescriptionLabelProvider {

    private static final int DF_TABLE_CELL_MARGIN = 5;
    
    public ReportActionColumnLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected String getElementToolTipText(ReportItemDescription element) {
        if (StringUtils.isNotEmpty(getText(element))) {
            return StringConstants.PROVIDER_TOOLTIP_CLICK_TO_SEE_DETAILS;
        }
        return super.getElementToolTipText(element);
    }

    @Override
    protected String getText(ReportItemDescription element) {
        TestSuiteLogRecord logRecord = getTestSuiteLogRecord(element.getReportLocation());
        if (logRecord != null) {
            return StringConstants.PROVIDER_TOOLTIP_SHOW_DETAILS;
        }
        return StringUtils.EMPTY;
    }

    private void openReport(ReportItemDescription element) {
        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.post(EventConstants.REPORT_OPEN, getReport(element.getReportLocation()));
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        openReport((ReportItemDescription) cell.getElement());

    }

    @Override
    protected Class<ReportItemDescription> getElementType() {
        return ReportItemDescription.class;
    }

    @Override
    protected Image getImage(ReportItemDescription element) {
        return null;
    }
    
    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return DF_TABLE_CELL_MARGIN;
            }
        };
    }
}
