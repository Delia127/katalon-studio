package com.kms.katalon.composer.testsuite.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.editors.VariableTestDataLinkCellEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePart;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;

public class VariableTestDataLinkColumnEditingSupport extends EditingSupport {

	private TestSuitePart testSuitePart;
	
	public VariableTestDataLinkColumnEditingSupport(ColumnViewer viewer, TestSuitePart testSuitePart) {
		super(viewer);
		this.testSuitePart = testSuitePart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element == null || !(element instanceof VariableLink)) return null;
		
		TestSuiteTestCaseLink testCaseLink = testSuitePart.getSelectedTestCaseLink();
		if (testCaseLink == null) return null;
		
		VariableLink variableLink = (VariableLink) element;
		
		String testDataLinkId = variableLink.getTestDataLinkId();
		
		TestCaseTestDataLink testDataLink =  TestSuiteController.getInstance().getTestDataLink(testDataLinkId, testCaseLink);
		
		return new VariableTestDataLinkCellEditor((Composite) getViewer().getControl(),
				testCaseLink.getTestDataLinks(), testDataLink);
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element == null || !(element instanceof VariableLink)) return false;
		VariableLink variableLink = (VariableLink) element;
		return (variableLink.getType() == VariableType.DATA_COLUMN);
	}

	@Override
	protected Object getValue(Object element) {
		if (element == null || !(element instanceof VariableLink)) return StringUtils.EMPTY;
		
		TestSuiteTestCaseLink testCaseLink = testSuitePart.getSelectedTestCaseLink();
		if (testCaseLink == null) return StringUtils.EMPTY;
		
		VariableLink variableLink = (VariableLink) element;
		
		String testDataLinkId = variableLink.getTestDataLinkId();
		if (testDataLinkId == null || testDataLinkId.isEmpty()) return StringUtils.EMPTY;
		
		TestCaseTestDataLink testDataLink = TestSuiteController.getInstance().getTestDataLink(testDataLinkId, testCaseLink);
		return (testDataLink != null) ? testDataLink.getTestDataId() : StringUtils.EMPTY;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element == null || !(element instanceof VariableLink) ||
				(value == null) || !(value instanceof TestCaseTestDataLink)) return;
		
		VariableLink variableLink = (VariableLink) element;
		TestCaseTestDataLink dataLinkTreeNode = (TestCaseTestDataLink) value;
		
		if (!dataLinkTreeNode.getId().equals(variableLink.getTestDataLinkId())) {
			variableLink.setTestDataLinkId(dataLinkTreeNode.getId());
			
			getViewer().update(element, null);
			testSuitePart.setDirty(true);
		}
	}

}
