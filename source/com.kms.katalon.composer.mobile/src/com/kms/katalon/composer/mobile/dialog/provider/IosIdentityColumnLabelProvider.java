package com.kms.katalon.composer.mobile.dialog.provider;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.execution.mobile.identity.IosIdentityInfo;

public class IosIdentityColumnLabelProvider extends TypeCheckStyleCellTableLabelProvider<IosIdentityInfo> {

    public IosIdentityColumnLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected Class<IosIdentityInfo> getElementType() {
        return IosIdentityInfo.class;
    }

    @Override
    protected Image getImage(IosIdentityInfo element) {
        return ImageConstants.IMG_16_APPLE;
    }

    @Override
    protected String getText(IosIdentityInfo element) {
        return element.getName();
    }
}
