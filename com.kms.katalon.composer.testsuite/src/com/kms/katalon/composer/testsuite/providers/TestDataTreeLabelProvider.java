package com.kms.katalon.composer.testsuite.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTreeLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_NO_INDEX = 0;
	private static final int COLUMN_ID_INDEX = 1;
	private static final int COLUMN_ITERATION_INDEX = 2;
	private static final int COLUMN_COMBINATION_INDEX = 3;

	public TestDataTreeLabelProvider() {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element == null || !(element instanceof TestDataLinkTreeNode) || columnIndex != COLUMN_ID_INDEX) {
			return null;
		}

		TestDataLinkTreeNode linkNode = (TestDataLinkTreeNode) element;
		TestCaseTestDataLink link = linkNode.getTestDataLink();
		switch (link.getCombinationType()) {
		case MANY:
			return ImageConstants.IMG_16_DATA_CROSS;
		case ONE:
			return ImageConstants.IMG_16_DATA_ONE_ONE;
		default:
			return null;			
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null || !(element instanceof TestDataLinkTreeNode)) {
			return StringUtils.EMPTY;
		}

		TestDataLinkTreeNode linkNode = (TestDataLinkTreeNode) element;
		TestCaseTestDataLink link = linkNode.getTestDataLink();
		switch (columnIndex) {
		case COLUMN_NO_INDEX:
			return linkNode.getId();
		case COLUMN_ID_INDEX:
			return link.getTestDataId();
		case COLUMN_ITERATION_INDEX:
			switch (link.getIterationEntity().getIterationType()) {
			case ALL:
				return link.getIterationEntity().getIterationType().name().toLowerCase();
			default:
				return link.getIterationEntity().getValue();
			}
		case COLUMN_COMBINATION_INDEX:
			return link.getCombinationType().name();
			
		}
		return StringUtils.EMPTY;
	}

}
