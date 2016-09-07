package com.kms.katalon.composer.components.impl.tree;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = 4448626929311619089L;

    private static final String TEST_CASE_TYPE_NAME = StringConstants.TREE_TEST_CASE_TYPE_NAME;

    public static final String KEY_WORD = StringConstants.TREE_TEST_CASE_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "tag", "comment", "description" };

    private TestCaseEntity testCase;

    public TestCaseTreeEntity(TestCaseEntity testCase, ITreeEntity parentTreeEntity) {
        super(testCase, parentTreeEntity);
        this.testCase = testCase;
    }

    @Override
    public TestCaseEntity getObject() throws Exception {
        TestCaseController.getInstance().reloadTestCase(testCase, entity);
        loadAllDescentdantEntities();
        return testCase;
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
        return ImageConstants.IMG_16_TEST_CASE;
    }

    @Override
    public String getTypeName() throws Exception {
        return TEST_CASE_TYPE_NAME;
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
        return FolderType.TESTCASE.toString();
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
        if (key.equals("id")) {
            return testCase.getIdForDisplay();
        } else if (key.equals("name")) {
            return testCase.getName();
        } else if (key.equals("tag")) {
            return testCase.getTag();
        } else if (key.equals("comment")) {
            return testCase.getComment();
        } else if (key.equals("description")) {
            return testCase.getDescription();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof TestCaseEntity) {
            entity = (FileEntity) object;
            testCase = (TestCaseEntity) object;
        }
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        TestCaseController.getInstance().loadAllDescentdantEntities(testCase);
    }

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        List<TooltipPropertyDescription> properties = super.getTooltipDescriptions();
        properties.add(TooltipPropertyDescription.createWithDefaultLength(StringConstants.TAG, testCase.getTag()));
        return properties;
    }
}
