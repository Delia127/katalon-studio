package com.kms.katalon.composer.components.tree;

import java.io.Serializable;
import java.util.List;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

public interface ITreeEntity extends Serializable {    
	public Object getObject() throws Exception;

	public Object[] getChildren() throws Exception;

	public ITreeEntity getParent() throws Exception;

	public String getText() throws Exception;

	public Image getImage() throws Exception;

	public String getTypeName() throws Exception;
	
	public String getCopyTag() throws Exception;

	public boolean hasChildren() throws Exception;

	public boolean isRemoveable() throws Exception;

	public boolean isRenamable() throws Exception;
	
	public Transfer getEntityTransfer() throws Exception;
	
	public void setObject(Object object) throws Exception;
	
	public String getKeyWord() throws Exception;
	
	public String[] getSearchTags() throws Exception;
	
	public String getPropertyValue(String key);
	
	public Image getEntryImage() throws Exception;
	
	public void loadAllDescentdantEntities() throws Exception;
	
    public List<TooltipPropertyDescription> getTooltipDescriptions();
    
    default com.katalon.platform.api.model.Entity toPlatformEntity() {
        return null;
    }
}
