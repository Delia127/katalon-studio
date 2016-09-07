package com.kms.katalon.composer.components.impl.tree;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class KeywordTreeEntity implements ITreeEntity {
	private static final long serialVersionUID = 8273934119859892617L;

	private static final String KEYWORD_TYPE_NAME = StringConstants.TREE_KEYWORD_TYPE_NAME;

	public static final String KEY_WORD = StringConstants.TREE_KEYWORD_KW;

	public static final String[] SEARCH_TAGS = new String[] { "name" };

	private ITreeEntity parentTreeEntity;
	private ICompilationUnit keywordFile;

	public KeywordTreeEntity(ICompilationUnit keywordFile, ITreeEntity parentTreeEntity) {
		this.parentTreeEntity = parentTreeEntity;
		this.keywordFile = keywordFile;
	}

	@Override
	public Object getObject() throws Exception {
		return keywordFile;
	}

	@Override
	public Object[] getChildren() throws Exception {
		return null;
	}

	@Override
	public ITreeEntity getParent() throws Exception {
		return parentTreeEntity;
	}

	@Override
	public String getText() throws Exception {
		return keywordFile.getElementName();
	}

	@Override
	public Image getImage() throws Exception {
		return ImageConstants.IMG_16_KEYWORD;
	}

	@Override
	public String getTypeName() throws Exception {
		return KEYWORD_TYPE_NAME;
	}

	@Override
	public String getCopyTag() throws Exception {
		return FolderType.KEYWORD.toString();
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
		return FileTransfer.getInstance();
	}

	@Override
	public void setObject(Object object) throws Exception {
		if (object instanceof ICompilationUnit) {
			this.keywordFile = (ICompilationUnit) object;
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
			return keywordFile.getElementName();
		}
		return StringUtils.EMPTY;
	}

    @Override
    public boolean equals(Object object) {
        try {
            if (!(object instanceof KeywordTreeEntity)) {
                return false;
            }
            KeywordTreeEntity anotherKeywordTreeEntity = (KeywordTreeEntity) object;
            if (!(anotherKeywordTreeEntity.getObject() instanceof ICompilationUnit)) {
                return false;
            }
            ICompilationUnit anotherKeywordFile = (ICompilationUnit) anotherKeywordTreeEntity.getObject();
            return StringUtils.equalsIgnoreCase(anotherKeywordFile.getPath().toString(), keywordFile.getPath()
                    .toString());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(keywordFile.getPath().toString()).toHashCode();
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
}
