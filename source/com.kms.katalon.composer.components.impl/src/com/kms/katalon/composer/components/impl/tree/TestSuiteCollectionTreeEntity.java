package com.kms.katalon.composer.components.impl.tree;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionTreeEntity extends AbstractTreeEntity {
    private static final long serialVersionUID = -1729536951389032359L;

    private static final String TEST_SUITE_COLLECTION_TYPE_NAME = GlobalStringConstants.TEST_SUITE_COLLECTION;

    public static final String KEY_WORD = GlobalStringConstants.ENTITY_KW_TEST_SUITE;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "description", "tag", "folder" };

    private TestSuiteCollectionEntity testSuiteCollection;

    public TestSuiteCollectionTreeEntity(TestSuiteCollectionEntity testSuiteCollection,
            FolderTreeEntity parentTreeEntity) {
        super(testSuiteCollection, parentTreeEntity);
        this.testSuiteCollection = testSuiteCollection;
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_TEST_SUITE_COLLECTION;
    }

    @Override
    public String getTypeName() throws Exception {
        return TEST_SUITE_COLLECTION_TYPE_NAME;
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.TESTSUITE.toString();
    }

    @Override
    public boolean hasChildren() throws Exception {
        return false;
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
    public String getKeyWord() throws Exception {
        return KEY_WORD;
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }

        switch (key) {
            case "id":
                return testSuiteCollection.getIdForDisplay();
            case "name":
                return testSuiteCollection.getName();
            case "description":
                return testSuiteCollection.getDescription();
            case "tag":
                return testSuiteCollection.getTag();
            case "folder":
                return testSuiteCollection.getParentFolder().getIdForDisplay();
            default:
                return StringUtils.EMPTY;
        }
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        return;
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof TestSuiteCollectionEntity) {
            testSuiteCollection = (TestSuiteCollectionEntity) object;
        }
    }

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        List<TooltipPropertyDescription> properties = super.getTooltipDescriptions();
        properties.add(TooltipPropertyDescription.createWithDefaultLength(StringConstants.TAG, testSuiteCollection.getTag()));
        return properties;
    }
}
