package com.kms.katalon.composer.components.impl.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.util.GroovyUtil;

public class FolderTreeEntity extends AbstractTreeEntity {

	private static final long serialVersionUID = 2019661366633516087L;

	private static final String FOLDER_TYPE_NAME = StringConstants.TREE_FOLDER_TYPE_NAME;

	private static final Image FOLDER_ICON = ImageConstants.IMG_16_FOLDER;

	private static final Image FOLDER_TEST_CASE_ICON = ImageConstants.IMG_16_FOLDER_TEST_CASE;

	private static final Image FOLDER_TEST_SUITE_ICON = ImageConstants.IMG_16_FOLDER_TEST_SUITE;

	private static final Image FOLDER_KEYWORD_ICON = ImageConstants.IMG_16_FOLDER_KEYWORD;

	private static final Image FOLDER_DATA_ICON = ImageConstants.IMG_16_FOLDER_DATA;

	private static final Image FOLDER_OBJECT_ICON = ImageConstants.IMG_16_FOLDER_OBJECT;

	private static final Image FOLDER_REPORT_ICON = ImageConstants.IMG_16_FOLDER_REPORT;

	private FolderEntity folder;

	public FolderTreeEntity(FolderEntity folder, ITreeEntity parentTreeEntity) {
		super(folder, parentTreeEntity);
		this.folder = folder;
	}

	@Override
	public Object[] getChildren() throws Exception {
		if (folder.getFolderType() == FolderType.KEYWORD) {
			List<Object> childrenEntities = new ArrayList<Object>();

			for (IPackageFragment packageFragment : GroovyUtil.getAllPackageInKeywordFolder(folder.getProject())) {
				if (packageFragment.exists()) {
					childrenEntities.add(new PackageTreeEntity(packageFragment, this));
				}
			}
			return childrenEntities.toArray();
		}
		
		return TreeEntityUtil.getChildren(this);
	}

	@Override
	public boolean hasChildren() throws Exception {
		return true;
	}

	@Override
	public Image getImage() throws Exception {
		if (isRootFolder(FolderController.getInstance().getTestCaseRoot(folder.getProject()))) {
			return FOLDER_TEST_CASE_ICON;
		} else if (isRootFolder(FolderController.getInstance().getTestSuiteRoot(folder.getProject()))) {
			return FOLDER_TEST_SUITE_ICON;
		} else if (isRootFolder(FolderController.getInstance().getObjectRepositoryRoot(folder.getProject()))) {
			return FOLDER_OBJECT_ICON;
		} else if (isRootFolder(FolderController.getInstance().getTestDataRoot(folder.getProject()))) {
			return FOLDER_DATA_ICON;
		} else if (isRootFolder(FolderController.getInstance().getKeywordRoot(folder.getProject()))) {
			return FOLDER_KEYWORD_ICON;
		} else if (isRootFolder(FolderController.getInstance().getReportRoot(folder.getProject()))) {
			return FOLDER_REPORT_ICON;
		} else {
			return FOLDER_ICON;
		}
	}

	@Override
	public String getTypeName() throws Exception {
		return FOLDER_TYPE_NAME;
	}

	@Override
	public boolean isRemoveable() throws Exception {
		if (isRootFolder(FolderController.getInstance().getTestCaseRoot(folder.getProject()))
				|| isRootFolder(FolderController.getInstance().getTestSuiteRoot(folder.getProject()))
				|| isRootFolder(FolderController.getInstance().getObjectRepositoryRoot(folder.getProject()))
				|| isRootFolder(FolderController.getInstance().getTestDataRoot(folder.getProject()))
				|| isRootFolder(FolderController.getInstance().getReportRoot(folder.getProject()))
				|| isRootFolder(FolderController.getInstance().getKeywordRoot(folder.getProject()))) {
			return false;
		}
		return true;
	}

	private boolean isRootFolder(FolderEntity rootFolder) throws Exception {
		if (folder.equals(rootFolder)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getObject() throws Exception {
		return folder;
	}

	@Override
	public boolean isRenamable() throws Exception {
	    if (folder.getFolderType() == FolderType.REPORT) {
	        return false;
	    }
		return isRemoveable();
	}

	@Override
	public Transfer getEntityTransfer() throws Exception {
		return TreeEntityTransfer.getInstance();
	}

	@Override
	public String getCopyTag() throws Exception {
		return folder.getFolderType().toString();
	}

	@Override
	public void setObject(Object object) throws Exception {
		if (object instanceof FolderEntity) {
			entity = (Entity) object;
			folder = (FolderEntity) object;
		}
	}

	@Override
	public String getKeyWord() throws Exception {
		if (folder.getFolderType() != null) {
			switch (folder.getFolderType()) {
			case TESTCASE:
				return TestCaseTreeEntity.KEY_WORD;
			case TESTSUITE:
				return TestSuiteTreeEntity.KEY_WORD;
			case DATAFILE:
				return TestDataTreeEntity.KEY_WORD;
			case KEYWORD:
				return KeywordTreeEntity.KEY_WORD;
			case WEBELEMENT:
				return WebElementTreeEntity.KEY_WORD;
			case REPORT:
				return ReportTreeEntity.KEY_WORD;
			}
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String[] getSearchTags() throws Exception {
		switch (folder.getFolderType()) {
		case TESTCASE:
			return TestCaseTreeEntity.SEARCH_TAGS;
		case TESTSUITE:
			return TestSuiteTreeEntity.SEARCH_TAGS;
		case DATAFILE:
			return TestDataTreeEntity.SEARCH_TAGS;
		case KEYWORD:
			return KeywordTreeEntity.SEARCH_TAGS;
		case WEBELEMENT:
			return WebElementTreeEntity.SEARCH_TAGS;
		case REPORT:
			return ReportTreeEntity.SEARCH_TAGS;
		}
		return null;
	}

	@Override
	public String getPropertyValue(String key) {
		if (key.equals("name")) {
			return folder.getName();
		}
		return StringUtils.EMPTY;
	}

	@Override
	public Image getEntryImage() throws Exception {
		switch (folder.getFolderType()) {
		case TESTCASE:
			return ImageConstants.IMG_16_TEST_CASE;
		case TESTSUITE:
			return ImageConstants.IMG_16_TEST_SUITE;
		case DATAFILE:
			return ImageConstants.IMG_16_TEST_DATA;
		case KEYWORD:
			return ImageConstants.IMG_16_KEYWORD;
		case WEBELEMENT:
			return ImageConstants.IMG_16_TEST_OBJECT;
		case REPORT:
			return ImageConstants.IMG_16_REPORT;
		}
		return null;
	}

	public void loadAllDescentdantEntities() throws Exception {
		FolderController.getInstance().loadAllDescentdantEntities(folder);
	}

}
