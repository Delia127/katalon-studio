package com.kms.katalon.composer.components.impl.tree;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.report.ReportCollectionEntity;

public class ReportCollectionTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -1512445235457520390L;
    private static final String REPORT_TYPE_NAME = StringConstants.TREE_REPORT_TYPE_NAME;
    public static final String KEY_WORD = StringConstants.TREE_REPORT_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    private ReportCollectionEntity reportCollection;
    
    public ReportCollectionTreeEntity(ReportCollectionEntity entity, FolderTreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
        reportCollection = entity;
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_REPORT_COLLECTION;
    }

    @Override
    public String getTypeName() throws Exception {
        return REPORT_TYPE_NAME;
    }

    @Override
    public String getCopyTag() throws Exception {
        //Copy is not allowed for Report
        return FolderType.REPORT.toString();
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
        return false;
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return null;
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
        if ("id".equals(key)) {
            return reportCollection.getIdForDisplay();
        }

        if ("name".equals(key)) {
            return reportCollection.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        // No children to load here
    }

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        return Collections.emptyList();
    }
}
