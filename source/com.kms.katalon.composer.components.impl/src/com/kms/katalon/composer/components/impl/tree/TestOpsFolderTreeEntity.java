package com.kms.katalon.composer.components.impl.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;

public class TestOpsFolderTreeEntity extends AbstractTreeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String TESTOPS_DISPLAY = StringConstants.TREE_TESTOPS;

	private List<ITreeEntity> children;
	
	public TestOpsFolderTreeEntity(ITreeEntity parentTreeEntity) {
		super(null, parentTreeEntity);
		children = new ArrayList<>();
	}
	
	public void addChild(ITreeEntity children) {
		this.children.add(children);
	}
	
	@Override
	public Object[] getChildren() throws Exception {
		return children.toArray();
	}

	@Override
	public Image getImage() throws Exception {
		return ImageConstants.IMG_16_KATALON_TESTOPS;
	}

	@Override
	public String getTypeName() throws Exception {
		return StringConstants.TREE_FOLDER_TYPE_NAME;
	}

	@Override
	public String getCopyTag() throws Exception {
		return TESTOPS_DISPLAY;
	}

	@Override
	public boolean hasChildren() throws Exception {
		return true;
	}

	@Override
	public boolean isRemoveable() throws Exception {
		return false;
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
		return null;
	}

	@Override
	public String[] getSearchTags() throws Exception {
		return null;
	}

	@Override
	public String getPropertyValue(String key) {
		return TESTOPS_DISPLAY;
	}

	@Override
	public Image getEntryImage() throws Exception {
		return getImage();
	}

	@Override
	public void loadAllDescentdantEntities() throws Exception {

	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).append(TESTOPS_DISPLAY.hashCode()).toHashCode();
    }
	
	@Override
    public String getText() throws Exception {
        return TESTOPS_DISPLAY;
    }
	
	@Override
    public boolean equals(Object object) {
        return object == this;
    }
	
}
