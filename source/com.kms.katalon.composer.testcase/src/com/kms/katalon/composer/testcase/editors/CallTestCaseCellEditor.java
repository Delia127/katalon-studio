package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class CallTestCaseCellEditor extends AbstractDialogCellEditor {

	private String selectedTestCasePk;

	public CallTestCaseCellEditor(Composite parent, String defaultContent, String selectedTestCasePk) {
		super(parent, defaultContent);
		this.selectedTestCasePk = selectedTestCasePk;
	}

	protected void updateContents(Object value) {
		if (value instanceof TestCaseEntity) {
			try {
				getDefaultLabel().setText(TestCaseController.getInstance().getIdForDisplay((TestCaseEntity) value));
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		} else if (defaultContent != null) {
			super.updateContents(defaultContent);
		} else {
			super.updateContents(value);
		}
	}

	private FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
		if (folderEntity == null || folderEntity.equals(rootFolder)) {
			return null;
		}
		return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
				rootFolder));
	}

	protected Object openDialogBox(Control cellEditorWindow) {
		try {
			EntityProvider entityProvider = new EntityProvider();
			TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(Display.getCurrent().getActiveShell(),
					new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(entityProvider));

			dialog.setAllowMultiple(false);
			dialog.setTitle(StringConstants.EDI_TITLE_TEST_CASE_BROWSER);
			ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
			if (currentProject != null) {
				FolderEntity rootFolder = FolderController.getInstance().getTestCaseRoot(currentProject);
				dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
				if (selectedTestCasePk != null) {
					TestCaseEntity selectedTestCase = TestCaseController.getInstance().getTestCaseByDisplayId(
							selectedTestCasePk);
					if (selectedTestCase != null) {
						dialog.setInitialSelection(new TestCaseTreeEntity(selectedTestCase,
								createSelectedTreeEntityHierachy(selectedTestCase.getParentFolder(), rootFolder)));
					}
				}
			}
			if (dialog.open() == Window.OK) {
				return dialog.getFirstResult();
			} else {
				return null;
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					StringConstants.EDI_ERROR_MSG_CANNOT_OPEN_DIALOG);
			LoggerSingleton.logError(e);
			return null;
		}

	}
}
