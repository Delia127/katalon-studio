package com.kms.katalon.composer.explorer.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.control.HiDPISupportedImage;
import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	// AdvancedSearchDialog
	public static final Image IMG_16_ADVANCED_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/advanced_search_16.png");
	
	// SearchDropDownBox
	public static final Image IMG_16_ARROW_DOWN = HiDPISupportedImage.loadImage(currentBundle, "/icons/arrow_down_6x6.png");
	public static final Image IMG_16_SEARCH_ALL = HiDPISupportedImage.loadImage(currentBundle, "/icons/all_19x16.png");
	
	// ExplorerPart
	public static final Image IMG_16_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/search_16.png");
	public static final Image IMG_16_CLOSE_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/close_search_16.png");
	
	// Other icon is using in fragment.e4xmi: project_16.png
}
