package com.kms.katalon.composer.components.impl.tree;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.report.ReportEntity;

public class ReportTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -5738894941424885826L;

    private static final String REPORT_TYPE_NAME = StringConstants.TREE_REPORT_TYPE_NAME;

    public static final String KEY_WORD = StringConstants.TREE_REPORT_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    private ReportEntity report;

    public ReportTreeEntity(ReportEntity report, ITreeEntity parentTreeEntity) {
        super(report, parentTreeEntity);
        this.report = report;
    }

    @Override
    public ReportEntity getObject() throws Exception {
        ReportController.getInstance().reloadReport(report, entity);
        loadAllDescentdantEntities();
        return report;
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_REPORT;
    }

    @Override
    public String getTypeName() throws Exception {
        return REPORT_TYPE_NAME;
    }

    @Override
    public String getCopyTag() throws Exception {
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
        return true;
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return null;
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof ReportEntity) {
            entity = (FileEntity) object;
            report = (ReportEntity) object;
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
        if (key.equals("id")) {
            return report.getRelativePathForUI().replace(File.separator, "/");
        } else if (key.equals("name")) {
            return report.getName();
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
        return Collections.emptyList();
    }
    
    @Override
    public String getText() throws Exception {
        return report.getDisplayName();
    }
}
