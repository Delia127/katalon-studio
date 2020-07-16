package com.kms.katalon.composer.components.impl.tree;

import java.util.List;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;

public class TestOpsTreeEntity implements ITreeEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TestOpsAction action;
	private String displayName;
	private ITreeEntity parentTreeEntity;
	private Image image;
	
	public TestOpsTreeEntity(String displayName, TestOpsAction action, Image image, ITreeEntity parentTreeEntity) {
		this.displayName = displayName;
		this.action = action;
		this.parentTreeEntity = parentTreeEntity;
		this.image = image;
	}
	
	public TestOpsAction getTesOpsAction() {
		return action;
	}
	
	public void setTesOpsAction(TestOpsAction action) {
		this.action = action;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	@Override
	public Object getObject() throws Exception {
		return this;
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
		return displayName;
	}

	@Override
	public Image getImage() throws Exception {
		return this.image;
	}

	@Override
	public String getTypeName() throws Exception {
		return StringConstants.TREE_FOLDER_TYPE_NAME;
	}

	@Override
	public String getCopyTag() throws Exception {
		return null;
	}

	@Override
	public boolean hasChildren() throws Exception {
		return false;
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
	public void setObject(Object object) throws Exception {
		
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
		return null;
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
		return null;
	}

	public enum TestOpsAction {
		OPEN_EXECUTION
	}

	@Override
	public boolean equals(Object object) {
		return this == object;
	}
	
}
