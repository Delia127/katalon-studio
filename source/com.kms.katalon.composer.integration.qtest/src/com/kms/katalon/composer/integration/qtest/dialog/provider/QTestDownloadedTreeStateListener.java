package com.kms.katalon.composer.integration.qtest.dialog.provider;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

import com.kms.katalon.composer.integration.qtest.dialog.TestCaseTreeDownloadedPreviewDialog;
import com.kms.katalon.composer.integration.qtest.dialog.model.ModuleDownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.model.DownloadedPreviewTreeNode;

public class QTestDownloadedTreeStateListener implements ICheckStateListener {

	private TestCaseTreeDownloadedPreviewDialog dialog;

	public QTestDownloadedTreeStateListener(TestCaseTreeDownloadedPreviewDialog dialog) {
		super();
		this.dialog = dialog;
	}

	private void checkRecursively(DownloadedPreviewTreeNode previewTree, boolean isChecked,
			CheckboxTreeViewer checkboxTreeViewer, QTestDownloadedTreeContentProvider contentProvider) {
		ModuleDownloadedPreviewTreeNode parentTree = previewTree.getParent();
		if (isChecked) {
			if (parentTree.getParent() != null) {
				boolean isAllSiblingSelected = true;
				for (Object childTree : contentProvider.getChildren(parentTree)) {
					if (!checkboxTreeViewer.getChecked(childTree)) {
						isAllSiblingSelected = false;
						break;
					}
				}

				if (isAllSiblingSelected) {
					checkboxTreeViewer.setGrayChecked(parentTree, false);
					checkboxTreeViewer.setChecked(parentTree, true);
					
				} else {
					checkboxTreeViewer.setGrayChecked(parentTree, true);
				}

				checkRecursively(parentTree, isAllSiblingSelected, checkboxTreeViewer, contentProvider);
			}
		} else {
			if (parentTree.getParent() != null) {
				boolean isAllSibblingNotSelected = true;
				for (Object childTree : contentProvider.getChildren(parentTree)) {
					if (checkboxTreeViewer.getChecked(childTree)) {
						isAllSibblingNotSelected = false;
						break;
					}
				}

				if (isAllSibblingNotSelected) {
					checkboxTreeViewer.setGrayChecked(parentTree, false);
					checkboxTreeViewer.setChecked(parentTree, false);					
				} else {
					checkboxTreeViewer.setGrayChecked(parentTree, true);
				}
				checkRecursively(parentTree, !isAllSibblingNotSelected, checkboxTreeViewer, contentProvider);
			}
		}
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		// TODO Auto-generated method stub
		if (event.getElement() instanceof DownloadedPreviewTreeNode) {
			boolean isChecked = event.getChecked();
			DownloadedPreviewTreeNode previewTree = (DownloadedPreviewTreeNode) event.getElement();

			CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer) event.getSource();
			checkboxTreeViewer.setGrayed(previewTree, false);
			checkboxTreeViewer.setChecked(previewTree, isChecked);
			previewTree.setSelected(isChecked);

			QTestDownloadedTreeContentProvider contentProvider = (QTestDownloadedTreeContentProvider) checkboxTreeViewer
					.getContentProvider();

			if (event.getElement() instanceof ModuleDownloadedPreviewTreeNode) {
				ModuleDownloadedPreviewTreeNode moduleTree = (ModuleDownloadedPreviewTreeNode) event.getElement();
				checkboxTreeViewer.setSubtreeChecked(moduleTree, isChecked);
			}

			checkRecursively(previewTree, isChecked, checkboxTreeViewer, contentProvider);

			dialog.updateStatus();
		}
	}
}
