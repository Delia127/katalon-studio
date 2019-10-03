package com.kms.katalon.composer.search.view;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.search.internal.ui.text.FileSearchPage;
import org.eclipse.search.internal.ui.text.LineElement;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

import com.kms.katalon.composer.search.action.OpenEditorAction;
import com.kms.katalon.composer.search.view.provider.SearchResultPageLabelProvider;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class QSearchResultPage extends FileSearchPage {

	/**
	 * Handles showMatch by using {@link #showEditor(IFile, int, int)}
	 */
	@Override
	protected void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		IFile file = (IFile) match.getElement();
		if (!showEditor(file, offset, length)) {
			super.showMatch(match, offset, length, activate);
		}
	}

	/**
	 * @see {@link OpenEditorAction}
	 * 
	 * @param file
	 *            {@link File}
	 * @param offset
	 * @param length
	 * @return true if system can open {@link IEntity} of the given file,  otherwise false.
	 */
	private boolean showEditor(IFile file, int offset, int length) {
		String fileName = FilenameUtils.getBaseName(file.getFullPath().toString());
		String fileExtension = "." + file.getFileExtension();

		ProjectEntity project = ProjectController.getInstance().getCurrentProject();
		try {
			if (fileExtension.equals(GroovyConstants.GROOVY_FILE_EXTENSION)) {
				TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(fileName);
				if (testCase != null) {
					OpenEditorAction.openEditor(testCase, offset, length);
					return true;
				}
			} else if (fileExtension.equals(TestCaseEntity.getTestCaseFileExtension())) {
				String testCaseId = file.getFullPath().toString()
						.replace(file.getProject().getFullPath().toString() + "/", "")
						.replace(TestCaseEntity.getTestCaseFileExtension(), "");
				TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
				OpenEditorAction.openTestCase(testCase);
				return true;
			} else if (fileExtension.equals(WebElementEntity.getWebElementFileExtension())) {
				String testObjectId = file.getFullPath().toString()
						.replace(file.getProject().getFullPath().toString() + "/", "")
						.replace(WebElementEntity.getWebElementFileExtension(), "");
				WebElementEntity testObject = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(
						testObjectId);
				OpenEditorAction.openTestObject(testObject);
				return true;
			} else if (fileExtension.equals(TestSuiteEntity.getTestSuiteFileExtension())) {
				String testSuiteId = file.getFullPath().toString()
						.replace(file.getProject().getFullPath().toString() + "/", "")
						.replace(TestSuiteEntity.getTestSuiteFileExtension(), "");
				TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteId,
						project);
				OpenEditorAction.openTestSuite(testSuite);
				return true;
			} else if (fileExtension.equals(DataFileEntity.getTestDataFileExtension())) {
				String testDataId = file.getFullPath().toString()
						.replace(file.getProject().getFullPath().toString() + "/", "")
						.replace(DataFileEntity.getTestDataFileExtension(), "");
				DataFileEntity testData = TestDataController.getInstance().getTestDataByDisplayId(testDataId);
				OpenEditorAction.openTestData(testData);
				return true;
			} else if (fileExtension.equals(ReportEntity.getReportFileExtension())) {
				String reportDisplayId = file.getParent().getProjectRelativePath().toString();
				ReportEntity testData = ReportController.getInstance().getReportEntityByDisplayId(reportDisplayId,
						project);
                OpenEditorAction.openReport(testData);
                return true;
            } else if (fileExtension.equals(ExecutionProfileEntity.getGlobalVariableFileExtension())) {
                String profileName = file.getName()
                        .replace(ExecutionProfileEntity.getGlobalVariableFileExtension(), "");
                ExecutionProfileEntity profile = GlobalVariableController.getInstance().getExecutionProfile(profileName,
                        project);
                OpenEditorAction.openExecutionProfile(profile);
                return true;
            }
		} catch (Exception e) {
			// An error occurs when open by IDE, open file by default navigator.
		}
		return false;
	}

	/**
	 * Changes the {@link LabelProvider} to {@link SearchResultPageLabelProvider}
	 */
	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
		super.configureTreeViewer(viewer);
		viewer.setLabelProvider(new SearchResultPageLabelProvider(new FileLabelProvider(this,
				FileLabelProvider.SHOW_LABEL)));
	}
	
	/**
	 * Handles {@link OpenEvent} by using {@link #showEditor(IFile, int, int)}
	 */
	@Override
	protected void handleOpen(OpenEvent event) {
		Object firstElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (firstElement instanceof IFile) {
			if (getDisplayedMatchCount(firstElement) == 0) {
				if (!showEditor((IFile) firstElement, 0, 0)) {
					super.handleOpen(event);
				}
			}
		} else if (firstElement instanceof LineElement) {
			super.handleOpen(event);
		}
	}
}
