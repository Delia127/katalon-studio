package com.kms.katalon.composer.search.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.search.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class QSearchPage extends DialogPage implements ISearchPage {
	
	public QSearchPage() {
	}
	
	private Text searchText;
	private Button chckTestCase;
	private Button chckTestObject;
	private Button chckTestSuite;
	private Button chckTestData;
	private Button chckKeyword;
	private Button chckIsWholeWord;
	private Button chckIsRegularExpression;
	private Button chckIsCaseSensitive;
	private Button chckReport;
	private Group grpScope;
	
	@Override
	public void createControl(Composite parent) {
		Composite compositeContainer = new Composite(parent, SWT.NONE);
		compositeContainer.setLayout(new GridLayout(1, false));
		
		Composite compositeSearch = new Composite(compositeContainer, SWT.NONE);
		compositeSearch.setLayout(new GridLayout(1, false));
		compositeSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Composite compositeSearchHeader = new Composite(compositeSearch, SWT.NONE);
		GridLayout glCompositeSearchHeader = new GridLayout(1, false);
		glCompositeSearchHeader.marginWidth = 0;
		glCompositeSearchHeader.marginHeight = 0;
		compositeSearchHeader.setLayout(glCompositeSearchHeader);
		compositeSearchHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Label lblContainingText = new Label(compositeSearchHeader, SWT.NONE);
		lblContainingText.setText(StringConstants.VIEW_LBL_CONTAINING_TEXT);
		
		Composite compositeSearchDetails = new Composite(compositeSearch, SWT.NONE);
		GridLayout glCompositeSearchDetails = new GridLayout(2, false);
		glCompositeSearchDetails.marginHeight = 0;
		glCompositeSearchDetails.marginWidth = 0;
		compositeSearchDetails.setLayout(glCompositeSearchDetails);
		compositeSearchDetails.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite compositeSearchText = new Composite(compositeSearchDetails, SWT.NONE);
		GridLayout glCompositeSearchText = new GridLayout(1, false);
		glCompositeSearchText.marginWidth = 0;
		glCompositeSearchText.marginHeight = 0;
		compositeSearchText.setLayout(glCompositeSearchText);
		compositeSearchText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		searchText = new Text(compositeSearchText, SWT.BORDER);
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblTips = new Label(compositeSearchText, SWT.NONE);
		lblTips.setText("( * = anything, ? = any character, \\ = escape for literals: * ? \\)");
		
		Composite compositeSearchType = new Composite(compositeSearchDetails, SWT.NONE);
		GridLayout gl_compositeSearchType = new GridLayout(1, false);
		gl_compositeSearchType.marginHeight = 0;
		compositeSearchType.setLayout(gl_compositeSearchType);
		
		chckIsCaseSensitive = new Button(compositeSearchType, SWT.CHECK);
		chckIsCaseSensitive.setText(StringConstants.VIEW_CHKBOX_CASE_SENSITIVE);
		
		chckIsRegularExpression = new Button(compositeSearchType, SWT.CHECK);
		chckIsRegularExpression.setText(StringConstants.VIEW_CHKBOX_REGULAR_EXPRESSION);
		
		chckIsWholeWord = new Button(compositeSearchType, SWT.CHECK);
		chckIsWholeWord.setText(StringConstants.VIEW_CHKBOX_WHOLE_WORD);
		chckIsWholeWord.setVisible(false);
		
		Composite compositeEntityType = new Composite(compositeContainer, SWT.NONE);
		compositeEntityType.setLayout(new GridLayout(1, true));
		compositeEntityType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		grpScope = new Group(compositeEntityType, SWT.NONE);
		grpScope.setText(StringConstants.VIEW_LBL_SEARCH_IN);
		grpScope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpScope.setLayout(new GridLayout(2, true));
		
		chckTestCase = new Button(grpScope, SWT.CHECK);
		chckTestCase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		chckTestCase.setText(StringConstants.VIEW_CHKBOX_TEST_CASE);
		
		chckTestObject = new Button(grpScope, SWT.CHECK);
		chckTestObject.setText(StringConstants.VIEW_CHKBOX_TEST_OBJ);
		
		chckTestSuite = new Button(grpScope, SWT.CHECK);
		chckTestSuite.setText(StringConstants.VIEW_CHKBOX_TEST_SUITE);
		
		chckTestData = new Button(grpScope, SWT.CHECK);
		chckTestData.setText(StringConstants.VIEW_CHKBOX_TEST_DATA);
		
		chckKeyword = new Button(grpScope, SWT.CHECK);
		chckKeyword.setText(StringConstants.VIEW_CHKBOX_KEYWORD);
		
		chckReport = new Button(grpScope, SWT.CHECK);
		chckReport.setText(StringConstants.VIEW_CHKBOX_REPORT);
		
		setControl(compositeContainer);
	}
	
	/**
	 * Returns a {@link QSearchQuery} based on user selection.
	 */
	private QSearchQuery createQuery() {
		List<String> fileNamePatterns = new ArrayList<String>();
		fileNamePatterns.add("!" + GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + GroovyConstants.GROOVY_FILE_EXTENSION);
		fileNamePatterns.add("!GlobalVariable" + GroovyConstants.GROOVY_FILE_EXTENSION);
		fileNamePatterns.add("!Temp*" + GroovyConstants.GROOVY_FILE_EXTENSION);
		
		String asterisk = "*"; 
				
		if (chckTestCase.getSelection()) {
			fileNamePatterns.add(asterisk + TestCaseEntity.getTestCaseFileExtension());
			fileNamePatterns.add("Script*" + GroovyConstants.GROOVY_FILE_EXTENSION);
		}
		
		if (chckTestObject.getSelection()) {
			fileNamePatterns.add(asterisk + WebElementEntity.getWebElementFileExtension());
		}
		
		if (chckTestData.getSelection()) {
			fileNamePatterns.add(asterisk + DataFileEntity.getTestDataFileExtension());
		}
		
		if (chckTestSuite.getSelection()) {
			fileNamePatterns.add(asterisk + TestSuiteEntity.getTestSuiteFileExtension());
		}
		
		if (chckKeyword.getSelection()) {
			if (!fileNamePatterns.contains("Script*" + GroovyConstants.GROOVY_FILE_EXTENSION)) {
				fileNamePatterns.add("!Script*" + GroovyConstants.GROOVY_FILE_EXTENSION);
			}
			fileNamePatterns.add(asterisk + GroovyConstants.GROOVY_FILE_EXTENSION);
		}
		
		if (chckReport.getSelection()) {
			fileNamePatterns.add(asterisk + ReportEntity.getReportFileExtension());
		}
		
		
		QSearchInput input = new QSearchInput(searchText.getText(),
				chckIsCaseSensitive.getSelection(), 
				chckIsRegularExpression.getSelection(),
				fileNamePatterns.toArray(new String[0]),
				GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject()));
		

		return new QSearchQuery(input.getSearchText(), input.isRegExSearch(),
				input.isCaseSensitiveSearch(), input.getScope());
	}

	@Override
	public void setContainer(ISearchPageContainer container) {
		container.getSelection();
	}

	@Override
	public boolean performAction() {
		try {
			NewSearchUI.runQueryInBackground(createQuery());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
