package com.kms.katalon.composer.integration.qtest.view.testsuite.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.integration.qtest.dialog.provider.QTestSuiteParentTreeLabelProvider;
import com.kms.katalon.integration.qtest.entity.QTestCycle;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;

public class QTestSuiteTableLabelProvider extends LabelProvider implements ITableLabelProvider {	
	private static final int CLMN_PARENT_NAME_IDX = 0;
	private static final int CLMN_PARENT_TYPE_IDX = 1;
	private static final int CLMN_PARENT_IS_DEFAULT_IDX = 2;

	public static final Image IMG_CHECK = ImageConstants.IMG_16_CHECKED;
	public static final Image IMG_UNCHECK = ImageConstants.IMG_16_UNCHECKED;
	
	public static final Image IMG_UPLOADED = ImageUtil.loadImage(
			FrameworkUtil.getBundle(QTestSuiteParentTreeLabelProvider.class), "icons/uploaded.png");
	public static final Image IMG_UPLOADING = ImageUtil.loadImage(
			FrameworkUtil.getBundle(QTestSuiteParentTreeLabelProvider.class), "icons/uploading.png");

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element != null && element instanceof QTestSuite) {
			QTestSuite qTestSuite = (QTestSuite) element;
			switch (columnIndex) {
			case CLMN_PARENT_NAME_IDX:
				if (qTestSuite.getId() > 0) {
					return IMG_UPLOADED;
				} else {
					return IMG_UPLOADING;
				}
			case CLMN_PARENT_IS_DEFAULT_IDX:
				if (qTestSuite.isSelected()) {
					return IMG_CHECK;
				} else {
					return IMG_UNCHECK;
				}
			}
			
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null 
				|| !(element instanceof QTestSuite) 
				|| columnIndex < CLMN_PARENT_NAME_IDX
				|| columnIndex > CLMN_PARENT_TYPE_IDX)
			return "";

		QTestSuite qTestSuite = (QTestSuite) element;
		switch (columnIndex) {
		case CLMN_PARENT_NAME_IDX:
			QTestSuiteParent parent = qTestSuite.getParent();			
			if (parent instanceof QTestCycle && parent.getParent() != null) {
				String parentName = parent.getParent().getName();
				if (parent != null && !parentName.isEmpty()) {
					return parent.getParent().getName() + " / " + parent.getName();
				} else {
					return parent.getName();
				}
				
			} else {
				return parent.getName();
			}
			
		case CLMN_PARENT_TYPE_IDX:
			return qTestSuite.getParent().getTypeName();
		}
		return "";
	}

}
