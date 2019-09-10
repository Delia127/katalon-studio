package com.kms.katalon.composer.windows.record;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TableCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public class RecordedWindowsElementLabelProvider extends TypeCheckStyleCellTableLabelProvider<CapturedWindowsElement> {

    public RecordedWindowsElementLabelProvider() {
        super(0);
    }

    @Override
    protected Class<CapturedWindowsElement> getElementType() {
        return CapturedWindowsElement.class;
    }

    @Override
    protected Image getImage(CapturedWindowsElement element) {
        return null;
    }

    @Override
    protected String getText(CapturedWindowsElement element) {
        return element.getName();
    }

    @Override
    protected String getElementToolTipText(CapturedWindowsElement element) {
        return element.getName();
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new TableCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 2;
            }
        };
    }
}
