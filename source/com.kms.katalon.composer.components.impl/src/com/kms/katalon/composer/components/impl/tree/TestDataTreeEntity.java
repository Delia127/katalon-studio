package com.kms.katalon.composer.components.impl.tree;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = 7158033879727783488L;

    private static final String TEST_DATA_TYPE_NAME = StringConstants.TREE_TEST_DATA_TYPE_NAME;

    public static final String KEY_WORD = StringConstants.TREE_TEST_DATA_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "description", "source name", "folder" };

    private DataFileEntity testData;

    public TestDataTreeEntity(DataFileEntity testData, ITreeEntity parentTreeEntity) {
        super(testData, parentTreeEntity);
        this.testData = testData;
    }

    @Override
    public DataFileEntity getObject() throws Exception {
        TestDataController.getInstance().reloadTestData(testData, entity);
        loadAllDescentdantEntities();
        return testData;
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
        return ImageConstants.IMG_16_TEST_DATA;
    }

    @Override
    public String getTypeName() throws Exception {
        return TEST_DATA_TYPE_NAME;
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
        return FolderType.DATAFILE.toString();
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof DataFileEntity) {
            entity = (FileEntity) object;
            testData = (DataFileEntity) object;
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
        if (testData != null) {
            if (key.equals("name")) {
                return testData.getName();
            } else if (key.equals("id")) {
                return testData.getRelativePathForUI().replace(File.separator, "/");
            } else if (key.equals("description")) {
                return testData.getDescription();
            } else if (key.equals("source name")) {
                return testData.getLocation();
            } else if (key.equals("folder")) {
                return testData.getParentFolder().getIdForDisplay();
            }
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

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        List<TooltipPropertyDescription> properties = super.getTooltipDescriptions();
        properties.add(TooltipPropertyDescription.createWithDefaultLength(StringConstants.TREE_TEST_DATA_PROP_DATA_TYPE,
                testData.getDriver().toString()));
        return properties;
    }
}
