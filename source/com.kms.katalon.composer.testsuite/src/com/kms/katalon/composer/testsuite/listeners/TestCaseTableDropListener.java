package com.kms.katalon.composer.testsuite.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransferData;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestCaseTableDropListener extends TableDropTargetEffect {

	private TestCaseTableViewer viewer;
	private TestSuiteEntity parentTestSuite;

	public TestCaseTableDropListener(TestCaseTableViewer viewer, TestSuiteEntity parentTestSuite) {
		super(viewer.getTable());
		this.viewer = viewer;
		this.parentTestSuite = parentTestSuite;
	}

	@Override
	public void drop(DropTargetEvent event) {
		try {
			event.detail = DND.DROP_COPY;
			Point pt = Display.getCurrent().map(null, viewer.getTable(), event.x, event.y);
			TableItem tableItem = viewer.getTable().getItem(pt);
			TestSuiteTestCaseLink selectedItem = (tableItem != null && tableItem.getData() instanceof TestSuiteTestCaseLink) ? (TestSuiteTestCaseLink) tableItem
					.getData() : null;
			int selectedIndex = (selectedItem != null) ? viewer.getIndex(selectedItem) : viewer.getInput().size();

			if (event.data instanceof ITreeEntity[]) {
				ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
				for (int i = treeEntities.length - 1; i >= 0; i--) {
					if (treeEntities[i] instanceof TestCaseTreeEntity) {
						viewer.insertTestCase((TestCaseEntity) ((TestCaseTreeEntity) treeEntities[i]).getObject(),
								selectedIndex);
					} else if (treeEntities[i] instanceof FolderTreeEntity) {
						for (TestCaseEntity testCase : getTestCasesFromFolderTree((FolderTreeEntity) treeEntities[i])) {
							viewer.insertTestCase(testCase, selectedIndex);
						}
					}
				}
			} else if (event.data instanceof TestSuiteTestCaseLinkTransferData[]) {
				TestSuiteTestCaseLinkTransferData[] testSuiteTestCaseLinkTransferDatas = (TestSuiteTestCaseLinkTransferData[]) event.data;
				if (testSuiteTestCaseLinkTransferDatas.length == 0) {
					return;
				}
				if (testSuiteTestCaseLinkTransferDatas[0].getTestSuite().getId().equals(parentTestSuite.getId())) {
					List<TestSuiteTestCaseLink> removeTestSuiteTestCaseLinks = new ArrayList<TestSuiteTestCaseLink>();
					for (TestSuiteTestCaseLinkTransferData transferData : testSuiteTestCaseLinkTransferDatas) {
						removeTestSuiteTestCaseLinks.add(transferData.getTestSuiteTestCaseLink());
					}
					viewer.removeTestCases(removeTestSuiteTestCaseLinks);
				}
				for (int i = testSuiteTestCaseLinkTransferDatas.length - 1; i >= 0; i--) {
					TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
							testSuiteTestCaseLinkTransferDatas[i].getTestSuiteTestCaseLink().getTestCaseId());
					if (testCase != null) {
						viewer.insertTestCase(testCase, selectedIndex);
					}
				}
			}

		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private List<TestCaseEntity> getTestCasesFromFolderTree(FolderTreeEntity folderTree) {
		List<TestCaseEntity> lstTestCases = new ArrayList<TestCaseEntity>();
		try {
			for (Object child : folderTree.getChildren()) {
				if (child instanceof TestCaseTreeEntity) {
					lstTestCases.add((TestCaseEntity) ((TestCaseTreeEntity) child).getObject());
				} else if (child instanceof FolderTreeEntity) {
					lstTestCases.addAll(getTestCasesFromFolderTree((FolderTreeEntity) child));
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return lstTestCases;
	}
}
