package com.kms.katalon.composer.components.impl.tree;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebElementTreeEntity extends AbstractTreeEntity {
	
	private static final long serialVersionUID = -736426078298872979L;

	private static final String OBJECT_TYPE_NAME = StringConstants.TREE_OBJECT_TYPE_NAME;

	public static final String KEY_WORD = StringConstants.TREE_OBJECT_KW;
	
	public static final String[] SEARCH_TAGS = new String[] {"id", "name"};
    
	private WebElementEntity webElement;
	
	public WebElementTreeEntity(WebElementEntity webElement, ITreeEntity parentTreeEntity) {
		super(webElement, parentTreeEntity);
		this.webElement = webElement;
	}
	
	@Override
    public Object getObject() throws Exception {
        return ObjectRepositoryController.getInstance().getWebElement(webElement.getId());
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
	    if (webElement instanceof WebServiceRequestEntity) {
	        return ImageConstants.IMG_16_WS_TEST_OBJECT;
	    }
		return ImageConstants.IMG_16_TEST_OBJECT;
	}

	@Override
	public String getTypeName() throws Exception {
		return OBJECT_TYPE_NAME;
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
		return FolderType.WEBELEMENT.toString();
	}

	@Override
	public void setObject(Object object) throws Exception {
		if (object instanceof WebElementEntity) {
			entity = (Entity) object;
			webElement = (WebElementEntity) object;
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
            return webElement.getName();
        } else if (key.equals("id")) {
            return webElement.getRelativePathForUI().replace(File.separator, "/");
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
