package com.kms.katalon.composer.search.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class QSearchQueryBuilder {
	private static final String JAVA_LEGAL_NAME_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";
	private static final String CUSTOM_KEYWORD_REGEX_PREFIX = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + "\\.\\'";
	private static final String GROOVY_FILE_FORMAT = "*.groovy";

	public static QSearchQuery getQuery(QSearchInput input) {
		return new QSearchQuery(input.getSearchText(), input.isRegExSearch(), input.isCaseSensitiveSearch(),
				input.getScope());
	}

	/**
	 * @return Array of string pattern of all test case references.
	 */
	private static String[] getTestCaseReferencesFileNamePatterns() {
		List<String> testCaseReferencesFileNamePatterns = new ArrayList<String>();
		testCaseReferencesFileNamePatterns.add(GROOVY_FILE_FORMAT);
		testCaseReferencesFileNamePatterns.add("*" + TestSuiteEntity.getTestSuiteFileExtension());
		return testCaseReferencesFileNamePatterns.toArray(new String[testCaseReferencesFileNamePatterns.size()]);
	}

	/**
	 * @return Array of string pattern of all test data references.
	 */
	private static String[] getTestDataReferencesFileNamePatterns() {
		List<String> testDataFileNameReferencePatterns = new ArrayList<String>();
		testDataFileNameReferencePatterns.add(GROOVY_FILE_FORMAT);
		testDataFileNameReferencePatterns.add("*" + TestSuiteEntity.getTestSuiteFileExtension());

		return testDataFileNameReferencePatterns.toArray(new String[testDataFileNameReferencePatterns.size()]);
	}

	/**
	 * @return Array of string pattern of all global variable references.
	 */
	private static String[] getGlobalVariableReferencesFileNamePatterns() {
		List<String> globalVariableFileNameReferencePatterns = new ArrayList<String>();
		globalVariableFileNameReferencePatterns.add(GROOVY_FILE_FORMAT);

		return globalVariableFileNameReferencePatterns.toArray(new String[globalVariableFileNameReferencePatterns
				.size()]);
	}

	/**
	 * @return Array of string pattern of all test object references.
	 */
	private static String[] getTestObjectReferencesFileNamePatterns() {
		List<String> testObjectReferencesFileNamePatterns = new ArrayList<String>();
		testObjectReferencesFileNamePatterns.add(GROOVY_FILE_FORMAT);
		// search for ref_element
		testObjectReferencesFileNamePatterns.add("*" + WebElementEntity.getWebElementFileExtension());

		return testObjectReferencesFileNamePatterns.toArray(new String[testObjectReferencesFileNamePatterns.size()]);
	}

	/**
	 * @return Array of string pattern of all keyword references.
	 */
	private static String[] getKeywordReferencesFileNamePatterns() {
		List<String> keywordFileNameReferencePatterns = new ArrayList<String>();
		keywordFileNameReferencePatterns.add(GROOVY_FILE_FORMAT);

		return keywordFileNameReferencePatterns.toArray(new String[keywordFileNameReferencePatterns.size()]);
	}

	/**
	 * Returns a {@link QSearchQuery} to show all preferences of a treeEntity on {@link QSearchResultPage}
	 * 
	 * @param treeEntity: the selected {@link ITreeEntity} that need to be show its references
	 * @param project: {@link ProjectEntity} of the given treeEntity belongs to.
	 * @return a {@link QSearchQuery} of a entity that is object of the given treeEntity.
	 * @throws Exception
	 */
	public static QSearchQuery getReferenceQueryForTreeEntity(ITreeEntity treeEntity, ProjectEntity project)
			throws Exception {
		IProject groovyProject = GroovyUtil.getGroovyProject(project);
		if (treeEntity instanceof TestCaseTreeEntity) {
			return getTestCaseReferenceQuery((TestCaseTreeEntity) treeEntity, groovyProject);
		} else if (treeEntity instanceof WebElementTreeEntity) {
			return getTestObjectReferenceQuery((WebElementTreeEntity) treeEntity, groovyProject);
		} else if (treeEntity instanceof TestDataTreeEntity) {
			return getTestDataReferenceQuery((TestDataTreeEntity) treeEntity, groovyProject);
		} else if (treeEntity instanceof KeywordTreeEntity) {
			return getKeywordReferenceQuery((KeywordTreeEntity) treeEntity, groovyProject);
		} else if (treeEntity instanceof FolderTreeEntity) {
			FolderEntity folderEntity = (FolderEntity) treeEntity.getObject();
			switch (folderEntity.getFolderType()) {
				case DATAFILE:
					return getTestDataFolderReferenceQuery(folderEntity, groovyProject);
				case KEYWORD:
					return getKeywordFolderReferenceQuery(groovyProject);
				case TESTCASE:
					return getTestCaseFolderReferenceQuery(folderEntity, groovyProject);
				case WEBELEMENT:
					return getTestObjectFolderReferenceQuery(folderEntity, groovyProject);
				default:
					break;
			}
		} else if (treeEntity instanceof PackageTreeEntity) {
			PackageTreeEntity packageTreeEntity = (PackageTreeEntity) treeEntity;
			return getPackageReferenceQuery(packageTreeEntity, groovyProject);
		}

		return null;
	}

	/**
	 * Returns a {@link QSearchQuery} for the given testCaseTreeEntity
	 * 
	 * @param testCaseTreeEntity
	 *            the selected {@link TestCaseTreeEntity} that's
	 *            {@link TestCaseEntity} needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestCaseReferenceQuery(TestCaseTreeEntity testCaseTreeEntity, IProject groovyProject)
			throws Exception {
		TestCaseEntity testCaseEntity = (TestCaseEntity) testCaseTreeEntity.getObject();
		if (testCaseEntity == null) return null;

		String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCaseEntity);

		String searchText = "((\'|\")" + testCaseId + "(\'|\"))";
		String searchTextMetaData = "(<testCaseId>" + testCaseId + "</testCaseId>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestCaseReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given folderEntity that's type is {@link FolderType.TESTCASE}
	 * 
	 * @param folderEntity
	 *            the selected {@link FolderEntity} that needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestCaseFolderReferenceQuery(FolderEntity folderEntity, IProject groovyProject)
			throws Exception {
		if (folderEntity == null) return null;

		String folderId = FolderController.getInstance().getIdForDisplay(folderEntity) + "/";

		String searchText = "((\'|\")" + folderId + ")";
		String searchTextMetaData = "(<testCaseId>" + folderId + ".+</testCaseId>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestCaseReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given testObjectTreeEntity
	 * 
	 * @param testObjectTreeEntity
	 *            the selected {@link WebElementTreeEntity} that's
	 *            {@link WebElementEntity} needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestObjectReferenceQuery(WebElementTreeEntity testObjectTreeEntity,
			IProject groovyProject) throws Exception {
		WebElementEntity testObjectEntity = (WebElementEntity) testObjectTreeEntity.getObject();
		if (testObjectEntity == null) return null;

		String testObjectId = ObjectRepositoryController.getInstance().getIdForDisplay(testObjectEntity);

		String searchText = "((\'|\")" + testObjectId + "(\'|\"))";
		String searchTextMetaData = "(<value>" + testObjectId + "</value>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestObjectReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given folderEntity that's type is {@link FolderType.WEBELEMENT}
	 * 
	 * @param folderEntity
	 *            the selected {@link FolderEntity} that needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestObjectFolderReferenceQuery(FolderEntity folderEntity, IProject groovyProject)
			throws Exception {
		if (folderEntity == null) return null;

		String folderId = FolderController.getInstance().getIdForDisplay(folderEntity) + "/";

		String searchText = "((\'|\")" + folderId + ")";
		String searchTextMetaData = "(<value>" + folderId + ".+</value>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestObjectReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given testDataTreeEntity
	 * 
	 * @param testDataTreeEntity
	 *            the selected {@link TestDataTreeEntity} that's
	 *            {@link DataFileEntity} needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestDataReferenceQuery(TestDataTreeEntity testDataTreeEntity, IProject groovyProject)
			throws Exception {
		DataFileEntity testDataEntity = (DataFileEntity) testDataTreeEntity.getObject();
		if (testDataEntity == null) return null;

		String testDataId = TestDataController.getInstance().getIdForDisplay(testDataEntity);

		String searchText = "((\'|\")" + testDataId + "(\'|\"))";
		String searchTextMetaData = "(<testDataId>" + testDataId + "</testDataId>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestDataReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given folderEntity that's type is {@link FolderType.DATAFILE}
	 * 
	 * @param folderEntity
	 *            the selected {@link DataFileEntity} that needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getTestDataFolderReferenceQuery(FolderEntity folderEntity, IProject groovyProject)
			throws Exception {
		if (folderEntity == null) return null;

		String folderId = FolderController.getInstance().getIdForDisplay(folderEntity) + "/";

		String searchText = "((\'|\")" + folderId + ")";
		String searchTextMetaData = "(<testDataId>" + folderId + ".+</testDataId>)";
		searchText = searchText + "|" + searchTextMetaData;
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getTestDataReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given globalVariable
	 * 
	 * @param globalVariable
	 *            the selected {@link GlobalVariableEntity} that needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getGlobalVariablePrefrenceQuery(GlobalVariableEntity globalVariable,
			IProject groovyProject) {
		String searchText = "GlobalVariable." + globalVariable.getName();
		boolean isCaseSensitive = true;
		boolean isRegExSearch = false;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getGlobalVariableReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given keywordTreeEntity
	 * 
	 * @param keywordTreeEntity
	 *            the selected {@link KeywordTreeEntity} that's {@link ICompilationUnit} needs to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getKeywordReferenceQuery(KeywordTreeEntity keywordTreeEntity, IProject groovyProject)
			throws Exception {
		IFile keywordFile = (IFile) ((ICompilationUnit) keywordTreeEntity.getObject()).getResource();
		if (keywordFile == null) return null;
		PackageTreeEntity parentPackageTreeEntity = (PackageTreeEntity) keywordTreeEntity.getParent();
		IPackageFragment packageFragment = (IPackageFragment) parentPackageTreeEntity.getObject();

		String packageName = packageFragment.getElementName().isEmpty() ? "" : packageFragment.getElementName() + "\\.";

		String searchText = CUSTOM_KEYWORD_REGEX_PREFIX + packageName
				+ FilenameUtils.getBaseName(keywordFile.getName()) + "(\\.)" + JAVA_LEGAL_NAME_REGEX + "\\'";
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getKeywordReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for the given packageTreeEntity
	 * 
	 * @param packageTreeEntity
	 *            the selected {@link PackageTreeEntity} that's all {@link ICompilationUnit}s need to be searched
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getPackageReferenceQuery(PackageTreeEntity packageTreeEntity, IProject groovyProject)
			throws Exception {
		if (packageTreeEntity == null) return null;
		IPackageFragment packageFragment = (IPackageFragment) packageTreeEntity.getObject();

		String packageName = packageFragment.getElementName().isEmpty() ? "" : packageFragment.getElementName() + "\\.";

		String searchText = CUSTOM_KEYWORD_REGEX_PREFIX + packageName + JAVA_LEGAL_NAME_REGEX + "(\\.)"
				+ JAVA_LEGAL_NAME_REGEX + "\\'";
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getKeywordReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}

	/**
	 * Returns a {@link QSearchQuery} for keyword root folder of the given groovyProject
	 * 
	 * @param groovyProject
	 * @return a {@link QSearchQuery}
	 * @throws Exception
	 */
	public static QSearchQuery getKeywordFolderReferenceQuery(IProject groovyProject) throws Exception {
		String searchText = CUSTOM_KEYWORD_REGEX_PREFIX + JAVA_LEGAL_NAME_REGEX + "((\\.)" + JAVA_LEGAL_NAME_REGEX
				+ ")+\\'";
		boolean isCaseSensitive = true;
		boolean isRegExSearch = true;

		QSearchInput input = new QSearchInput(searchText, isCaseSensitive, isRegExSearch,
				getKeywordReferencesFileNamePatterns(), groovyProject);
		return getQuery(input);
	}
}
