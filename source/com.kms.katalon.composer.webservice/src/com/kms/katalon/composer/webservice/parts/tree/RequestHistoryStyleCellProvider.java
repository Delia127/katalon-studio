package com.kms.katalon.composer.webservice.parts.tree;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

public class RequestHistoryStyleCellProvider extends TypeCheckedStyleCellLabelProvider<IRequestHistoryItem> {

    private CellLayoutInfo cellLayoutInfo = new CellLayoutInfo() {

        @Override
        public int getSpace() {
            return 5;
        }

        @Override
        public int getRightMargin() {
            return 0;
        }

        @Override
        public int getLeftMargin() {
            if (Platform.OS_MACOSX.equals(Platform.getOS())) {
                return 5;
            } else {
                return 2;
            }
        }
    };

    public RequestHistoryStyleCellProvider() {
        super(0);
    }

    @Override
    protected Class<IRequestHistoryItem> getElementType() {
        return IRequestHistoryItem.class;
    }

    @Override
    protected Image getImage(IRequestHistoryItem element) {
        return element.getImage();
    }

    @Override
    protected String getText(IRequestHistoryItem element) {
        return element.getName();
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return cellLayoutInfo;
    }

    @Override
    protected String getElementToolTipText(IRequestHistoryItem element) {
        return getText(element);
    }

    @Override
    protected void measure(Event event, Object element) {
        super.measure(event, element);
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            event.width += 1;
        }
        if (canNotDrawSafely(element)) {
            return;
        }
        ViewerCell cell = getOwnedViewerCell(event);

        if (isCellNotExisted(cell)) {
            return;
        }
        Image image = cell.getImage();
        if (image == null) {
            return;
        }
        event.height = Math.max(event.height, image.getBounds().height + 6);
    }
}
