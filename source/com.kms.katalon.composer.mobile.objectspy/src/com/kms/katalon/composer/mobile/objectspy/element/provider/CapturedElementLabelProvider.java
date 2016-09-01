package com.kms.katalon.composer.mobile.objectspy.element.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TableCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.constant.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

public class CapturedElementLabelProvider extends TypeCheckStyleCellTableLabelProvider<CapturedMobileElement> {

    public static final int SELECTION_COLUMN_IDX = 0;

    public static final int ELEMENT_COLUMN_IDX = 1;

    public CapturedElementLabelProvider(int columnIdx) {
        super(columnIdx);
    }

    @Override
    protected Class<CapturedMobileElement> getElementType() {
        return CapturedMobileElement.class;
    }

    @Override
    protected Image getImage(CapturedMobileElement element) {
        switch (columnIndex) {
            case SELECTION_COLUMN_IDX: {
                return element.isChecked() ? ImageConstants.IMG_16_CHECKED : ImageConstants.IMG_16_UNCHECKED;
            }
            case ELEMENT_COLUMN_IDX: {
                return element.getLink() != null ? ImageConstants.IMG_16_ACTIVE : ImageConstants.IMG_16_INACTIVE;
            }
        }
        return null;
    }

    @Override
    protected String getText(CapturedMobileElement element) {
        if (columnIndex == ELEMENT_COLUMN_IDX) {
            return element.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected String getElementToolTipText(CapturedMobileElement element) {
        switch (columnIndex) {
            case ELEMENT_COLUMN_IDX: {
                if (element.getLink() != null) {
                    return StringConstants.CELL_TOOLTIP_ACTIVE;
                }
                return StringConstants.CELL_TOOLTIP_INACTIVE;
            }
            default:
                return StringUtils.EMPTY;
        }
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
