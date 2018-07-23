package com.kms.katalon.entity.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity.MATCH_CONDITION;

public class WebElementEntity extends FileEntity {

    private static final long serialVersionUID = 1L;

    public static final String ref_element = "ref_element";

    public static final String defaultElementGUID = "00000000-0000-0000-0000-000000000000";

    public static final String DEFAULT_EMPTY_STRING = "";

    public static final String REF_ELEMENT_IS_SHADOW_ROOT = "ref_element_is_shadow_root";

    private String elementGuidId;

    private List<WebElementPropertyEntity> webElementProperties;
    
    private List<WebElementXpathEntity> webElementXpaths;

    private String imagePath;

    private boolean useRalativeImagePath;

    private WebElementSelectorMethod selectorMethod = WebElementSelectorMethod.ATTRIBUTES;

    private Map<WebElementSelectorMethod, String> selectorCollection;

    public WebElementEntity() {
        super();
        webElementProperties = new ArrayList<WebElementPropertyEntity>(0);
        webElementXpaths = new ArrayList<WebElementXpathEntity>(0);
        setSelectorCollection(new HashMap<>());
        elementGuidId = defaultElementGUID;

        name = DEFAULT_EMPTY_STRING;
        description = DEFAULT_EMPTY_STRING;
        selectorCollection = new HashMap<>();
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
    
    public List<WebElementXpathEntity> getWebElementXpaths() {
        return this.webElementXpaths;
    }

    public void setWebElementXpaths(List<WebElementXpathEntity> webElementXpaths) {
        this.webElementXpaths = webElementXpaths;
    }

    @Override
    public WebElementEntity clone() {
        WebElementEntity newWebElement = (WebElementEntity) super.clone();
        newWebElement.setElementGuidId(UUID.randomUUID().toString());
        newWebElement.setName(getName());
        newWebElement.setParentFolder(getParentFolder());
        newWebElement.setProject(getProject());
        newWebElement.setDescription(getDescription());

        newWebElement.getWebElementProperties().clear();
        for (WebElementPropertyEntity webElementProperty : getWebElementProperties()) {
            newWebElement.getWebElementProperties().add(webElementProperty.clone());
        }
        
        newWebElement.getWebElementXpaths().clear();
        for (WebElementXpathEntity webElementXpath : getWebElementXpaths()) {
            newWebElement.getWebElementXpaths().add(webElementXpath.clone());
        }

        newWebElement.setImagePath(getImagePath());
        newWebElement.setUseRalativeImagePath(getUseRalativeImagePath());
        newWebElement.setSelectorMethod(getSelectorMethod());
        newWebElement.setSelectorCollection(new HashMap<>(getSelectorCollection()));
        return newWebElement;
    }

    public static String getWebElementFileExtension() {
        return ".rs";
    }

    @Override
    public String getFileExtension() {
        return getWebElementFileExtension();
    }

    @Override
    public String getRelativePathForUI() {
        if (parentFolder != null) {
            return parentFolder.getRelativePath() + File.separator + this.name;
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEquals = super.equals(obj);
        if (!(obj instanceof WebElementEntity)) {
            return false;
        }
        WebElementEntity that = (WebElementEntity) obj;
        isEquals = isEquals && new EqualsBuilder()
                .append(this.getWebElementProperties(), that.getWebElementProperties()).isEquals()
                && new EqualsBuilder()
                .append(this.getWebElementXpaths(), that.getWebElementXpaths()).isEquals();
        return isEquals;
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

    public WebElementPropertyEntity getProperty(String propertyName) {
        Optional<WebElementPropertyEntity> optResult = getWebElementProperties().stream()
                .filter(p -> p.getName().equals(propertyName)
                        && MATCH_CONDITION.EQUAL.toString().equals(p.getMatchCondition()))
                .findFirst();
        return optResult.isPresent() ? optResult.get() : null;
    }
    
    public String getPropertyValue(String propertyName) {
        WebElementPropertyEntity prop = getProperty(propertyName);
        return prop != null ? prop.getValue() : StringUtils.EMPTY;
    }
    
    public WebElementXpathEntity getXpath(String xpathName) {
        Optional<WebElementXpathEntity> optResult = getWebElementXpaths().stream()
                .filter(p -> p.getName().equals(xpathName)
                        && MATCH_CONDITION.EQUAL.toString().equals(p.getMatchCondition()))
                .findFirst();
        return optResult.isPresent() ? optResult.get() : null;
    }

    public String getXpathValue(String xpathName) {
        WebElementXpathEntity prop = getXpath(xpathName);
        return prop != null ? prop.getValue() : StringUtils.EMPTY;
    }


    public WebElementSelectorMethod getSelectorMethod() {
        return selectorMethod;
    }

    public void setSelectorMethod(WebElementSelectorMethod selectorMethod) {
        this.selectorMethod = selectorMethod;
    }

    public Map<WebElementSelectorMethod, String> getSelectorCollection() {
        return selectorCollection;
    }

    public void setSelectorCollection(Map<WebElementSelectorMethod, String> selectorCollection) {
        this.selectorCollection = selectorCollection;
    }

    public void setSelectorValue(WebElementSelectorMethod selectorMethod, String selectorValue) {
        selectorCollection.put(selectorMethod, selectorValue);
    }
}
