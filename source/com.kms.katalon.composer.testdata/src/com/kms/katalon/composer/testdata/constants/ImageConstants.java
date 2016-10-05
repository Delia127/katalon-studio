package com.kms.katalon.composer.testdata.constants;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants extends com.kms.katalon.composer.components.impl.constants.ImageConstants {

    private static final Bundle sharedBundle = FrameworkUtil
            .getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);
	
	// OpenTestDataHandler
	public static final String URL_16_TEST_DATA = ImageUtil.getImageUrl(sharedBundle, "/icons/test_data_16.png");
	
    public static final Image IMG_16_WARN_TABLE_ITEM = PlatformUI.getWorkbench().getSharedImages()
            .getImage(ISharedImages.IMG_OBJS_WARN_TSK);
}
