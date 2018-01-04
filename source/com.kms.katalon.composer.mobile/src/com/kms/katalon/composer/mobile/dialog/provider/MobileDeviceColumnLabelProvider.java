package com.kms.katalon.composer.mobile.dialog.provider;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public class MobileDeviceColumnLabelProvider extends TypeCheckStyleCellTableLabelProvider<MobileDeviceInfo> {

    public MobileDeviceColumnLabelProvider(int columnIndex) {
        super(columnIndex);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Class<MobileDeviceInfo> getElementType() {
        return MobileDeviceInfo.class;
    }

    @Override
    protected Image getImage(MobileDeviceInfo element) {
        if (element instanceof AndroidDeviceInfo) {
            return ImageConstants.IMG_16_ANDROID;
        }
        if (element instanceof IosDeviceInfo) {
            return ImageConstants.IMG_16_APPLE;
        }
        return null;
    }

    @Override
    protected String getText(MobileDeviceInfo element) {
        return element.getDisplayName();
    }

}
