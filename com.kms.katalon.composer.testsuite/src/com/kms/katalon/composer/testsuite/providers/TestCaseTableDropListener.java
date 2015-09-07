package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTableDropListener extends ViewerDropAdapter {

	private TestCaseTableViewer viewer;

	public TestCaseTableDropListener(Viewer viewer) {
		super(viewer);
		this.viewer = (TestCaseTableViewer) viewer;
	}

	public void drop(DropTargetEvent event) {
		try {
			viewer.getTable().deselectAll();
			viewer.getTable().forceFocus();
			if (event.data != null && event.data instanceof ITreeEntity[]) {
				ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
				for (int i = 0; i < treeEntities.length; i++) {
					if (treeEntities[i] instanceof TestCaseTreeEntity) {
						viewer.addTestCase((TestCaseEntity) ((TestCaseTreeEntity) treeEntities[i]).getObject());
					} else if (treeEntities[i] instanceof FolderTreeEntity) {
						for (TestCaseEntity testCase : getTestCasesFromFolderTree((FolderTreeEntity) treeEntities[i])) {
							viewer.addTestCase(testCase);
						}
					}
				}
			}
			TableItem[] selections = viewer.getTable().getSelection();
			viewer.getTable().forceFocus();
			viewer.getTable().setSelection(selections);

		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	@Override
	public boolean performDrop(Object data) {
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
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
