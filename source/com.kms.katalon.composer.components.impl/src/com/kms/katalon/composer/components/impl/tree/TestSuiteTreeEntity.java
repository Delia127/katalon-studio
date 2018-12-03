package com.kms.katalon.composer.components.impl.tree;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -8388373715110244233L;

    private static final String TEST_SUITE_TYPE_NAME = StringConstants.TREE_TEST_SUITE_TYPE_NAME;

    public static final String KEY_WORD = StringConstants.TREE_TEST_SUITE_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "description", "folder" };

    private TestSuiteEntity testSuite;

    public TestSuiteTreeEntity(TestSuiteEntity testSuite, ITreeEntity parentTreeEntity) {
        super(testSuite, parentTreeEntity);
        this.testSuite = testSuite;
    }

    @Override
    public TestSuiteEntity getObject() throws Exception {
        return TestSuiteController.getInstance().getTestSuite(entity.getId());
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public boolean hasChildren() throws Exception {
        return false;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_TEST_SUITE;
    }

    @Override
    public String getTypeName() throws Exception {
        return TEST_SUITE_TYPE_NAME;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        return true;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return true;
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return TreeEntityTransfer.getInstance();
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.TESTSUITE.toString();
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof TestSuiteEntity) {
            entity = (FileEntity) object;
            testSuite = (TestSuiteEntity) object;
        }
    }

    @Override
    public String getKeyWord() throws Exception {
        return KEY_WORD;
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if (key.equals("name")) {
            return testSuite.getName();
        } else if (key.equals("id")) {
            return testSuite.getRelativePathForUI().replace(File.separator, "/");
        } else if (key.equals("description")) {
            return testSuite.getDescription();
        } else if (key.equals("folder")) {
            return testSuite.getParentFolder().getIdForDisplay();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
    }
}
