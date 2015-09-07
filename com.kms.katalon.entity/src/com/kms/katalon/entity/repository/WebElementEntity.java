package com.kms.katalon.entity.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kms.katalon.entity.file.FileEntity;

public class WebElementEntity extends FileEntity {

	private static final long serialVersionUID = 1L;
	public static final String ref_element = "ref_element";
	public static final String defaultElementGUID = "00000000-0000-0000-0000-000000000000";
	public static final String DEFAULT_EMPTY_STRING = "";

	private String elementGuidId;

	private List<WebElementPropertyEntity> webElementProperties;
	
	private String imagePath;
	
	private boolean useRalativeImagePath;

	public WebElementEntity() {
		super();
		webElementProperties = new ArrayList<WebElementPropertyEntity>(0);
		elementGuidId = defaultElementGUID;

		name = DEFAULT_EMPTY_STRING;
		description = DEFAULT_EMPTY_STRING;
	}

	public String getElementGuidId() {
		return this.elementGuidId;
	}

	public void setElementGuidId(String elementGuidId) {
		this.elementGuidId = elementGuidId;
	}

	public List<WebElementPropertyEntity> getWebElementProperties() {
		return this.webElementProperties;
	}

	public void setWebElementProperties(List<WebElementPropertyEntity> webElementProperties) {
		this.webElementProperties = webElementProperties;
	}

	public WebElementEntity clone() {
		WebElementEntity newWebElement = (WebElementEntity) super.clone();
		newWebElement.setElementGuidId(UUID.randomUUID().toString());
		return newWebElement;
	}

	public static String getWebElementFileExtension() {
		return ".rs";
	}

	@Override
	public String getFileExtension() {
		return getWebElementFileExtension();
	}

	public String getRelativePathForUI() {
		if (parentFolder != null) {
			return parentFolder.getRelativePath() + File.separator + this.name;
		}
		return "";
	}
	
    @Override
    public boolean equals(Object that) {
        boolean equals = super.equals(that);
        if (equals) {
            WebElementEntity anotherWebElement = (WebElementEntity) that;
            
            if (!getWebElementProperties().equals(anotherWebElement.getWebElementProperties())) {
                return false;
            }
        }
        return equals;
    }
    
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public boolean getUseRalativeImagePath() {
		return useRalativeImagePath;
	}

	public void setUseRalativeImagePath(boolean useRalativeImagePath) {
		this.useRalativeImagePath = useRalativeImagePath;
	}
}
