package com.kms.katalon.composer.testsuite.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.parts.TestSuitePart;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_NO_INDEX = 0;
	private static final int COLUMN_NAME_INDEX = 1;
	private static final int COLUMN_DEFAULT_VALUE_INDEX = 2;
	private static final int COLUMN_TYPE_INDEX = 3;
	private static final int COLUMN_TEST_DATA_ID_INDEX = 4;
	private static final int COLUMN_VALUE_INDEX = 5;

	private static TestSuiteController testSuiteController = TestSuiteController.getInstance();

	private TableViewer viewer;
	private TestSuitePart testSuitePart;

	public VariableTableLabelProvider(TableViewer viewer, TestSuitePart testSuitePart) {
		this.viewer = viewer;
		this.testSuitePart = testSuitePart;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null || !(element instanceof VariableLink) || columnIndex < 0
				|| columnIndex >= viewer.getTable().getColumnCount()) return StringUtils.EMPTY;

		TestSuiteTestCaseLink testCaseLink = testSuitePart.getSelectedTestCaseLink();
		if (testCaseLink == null) {
			return StringUtils.EMPTY;
		}

		VariableLink variableLink = (VariableLink) element;

		try {
			VariableEntity variableEntity = testSuiteController.getVariable(testCaseLink, variableLink);
			if (variableEntity == null) return StringUtils.EMPTY;

			switch (columnIndex) {
				case COLUMN_NO_INDEX:
					return Integer
							.toString(testSuitePart.getSelectedTestCaseLink().getVariableLinks().indexOf(element) + 1);
				case COLUMN_NAME_INDEX:
					return variableEntity.getName();
				case COLUMN_DEFAULT_VALUE_INDEX:
					return variableEntity.getDefaultValue();
				case COLUMN_TYPE_INDEX:
					return variableLink.getType().getDisplayName();
				case COLUMN_TEST_DATA_ID_INDEX:
					if (!variableLink.getTestDataLinkId().isEmpty()) {
						TestCaseTestDataLink testDataLink = testSuiteController.getTestDataLink(
								variableLink.getTestDataLinkId(), testCaseLink);
						if (testDataLink != null) {
							String treeId = testSuitePart.getTestDataContentProvider().getTreeNode(testDataLink)
									.getId();
							return treeId + " - " + testDataLink.getTestDataId();
						}
					}
					return StringUtils.EMPTY;
				case COLUMN_VALUE_INDEX:
					return variableLink.getValue();
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return StringUtils.EMPTY;
	}

}
