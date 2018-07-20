package com.kms.katalon.objectspy.element;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementXpathEntity;

public class WebElement implements XPathProvider {

    private static final String AND_OPERATOR = " and ";

    private static final String XPATH_METHOD = "xpath";

    private static final String TEXT_METHOD = "text";

    private static final String PROPERTY_TYPE = "type";

    private static final String PROPERTY_TAG = "tag";

    private static final String XPATH_FIND_BY_ATTRIBUTE = "@{0}=''{1}''";

    private static final String XPATH_FIND_BY_TEXT = TEXT_METHOD + "()=''{0}''";

    private static final String XPATH_EXPRESSION = "//{0}[{1}]";

    private String name;

    private WebElementType type = WebElementType.ELEMENT;

    private WebFrame parent;

    private List<WebElementPropertyEntity> properties = new ArrayList<>();
    
    private List<WebElementXpathEntity> xpaths = new ArrayList<>();

    private SelectorMethod selectorMethod = SelectorMethod.ATTRIBUTES;

    private Map<SelectorMethod, String> selectorCollection = new HashMap<>();
    
    private FileEntity savedEntity;

    public WebElement(String name) {
        this.name = name;
    }

    protected WebElement(String name, WebElementType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WebElementType getType() {
        return type;
    }

    public String getTag() {
        WebElementPropertyEntity tagProperty = getProperty(PROPERTY_TAG);
        if (tagProperty == null) {
            return null;
        }
        return tagProperty.getValue();
    }

    public void setTag(String tag) {
        WebElementPropertyEntity tagProperty = getProperty(PROPERTY_TAG);
        if (tagProperty == null) {
            addProperty(PROPERTY_TAG, tag);
            return;
        }
        tagProperty.setValue(tag);
    }

    public WebFrame getParent() {
        return parent;
    }

    public void setParent(WebFrame parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }
    
    public void setParentOnly(WebFrame parent) {
        this.parent = parent;
    }

    public List<WebElementPropertyEntity> getProperties() {
        return properties;
    }

    public void setProperties(List<WebElementPropertyEntity> properties) {
        this.properties = properties;
    }

    public void addProperty(WebElementPropertyEntity property) {
        properties.add(property);
    }

    public void addProperty(String name, String value) {
        addProperty(new WebElementPropertyEntity(name, value));
    }

    public WebElementPropertyEntity getProperty(String name) {
        Optional<WebElementPropertyEntity> property = properties.stream()
                .filter(prop -> prop.getName().equals(name))
                .findFirst();
        if (property.isPresent()) {
            return property.get();
        }
        return null;
    }

    public String getTypeProperty() {
        WebElementPropertyEntity typeProperty = getProperty(PROPERTY_TYPE);
        return typeProperty == null ? null : typeProperty.getValue();
    }

    public boolean hasProperty() {
        return properties != null && properties.size() > 0;
    }
    
    public List<WebElementXpathEntity> getXpaths() {
        return xpaths;
    }

    public void setXpaths(List<WebElementXpathEntity> xpaths) {
        this.xpaths = xpaths;
    }

    public void addXpath(WebElementXpathEntity xpath) {
        xpaths.add(xpath);
    }

    public void addXpath(String name, String value) {
        addXpath(new WebElementXpathEntity(name, value));
    }

    public WebElementXpathEntity getXpath(String name) {
        Optional<WebElementXpathEntity> property = xpaths.stream()
                .filter(prop -> prop.getName().equals(name))
                .findFirst();
        if (property.isPresent()) {
            return property.get();
        }
        return null;
    }

    public boolean hasXpath() {
        return xpaths != null && xpaths.size() > 0;
    }

    public boolean hasChild() {
        return false;
    }

    public String getXpath() {
        StringBuilder xpathBuilder = new StringBuilder();
        for (WebElementPropertyEntity p : properties) {
            if (!p.getIsSelected()) {
                continue;
            }
            String value = p.getValue();
            String name = p.getName();
            switch (name) {
                case XPATH_METHOD:
                    return value;
                case TEXT_METHOD:
                    appendAndOperator(xpathBuilder);
                    xpathBuilder.append(MessageFormat.format(XPATH_FIND_BY_TEXT, value));
                    break;
                default:
                    appendAndOperator(xpathBuilder);
                    xpathBuilder.append(MessageFormat.format(XPATH_FIND_BY_ATTRIBUTE, name, value));
            }
        }

        return MessageFormat.format(XPATH_EXPRESSION, StringUtils.defaultIfEmpty(getTag(), "*"),
                xpathBuilder.toString());
    }

    private void appendAndOperator(StringBuilder xpathBuilder) {
        if (StringUtils.isNotEmpty(xpathBuilder.toString())) {
            xpathBuilder.append(AND_OPERATOR);
        }
    }

    public WebElement softClone() {
        WebElement clone = new WebElement(getName(), getType());
        clone.setSelectorMethod(getSelectorMethod());
        getSelectorCollection().entrySet().forEach(entry -> {
            clone.setSelectorValue(entry.getKey(), entry.getValue());
        });
        clone.setProperties(new ArrayList<>(getProperties()));
        return clone;
    }
    
    public WebElement clone() {
        WebElement clone = softClone();
        List<WebElementPropertyEntity> cloneProperties = new ArrayList<>();
        for (WebElementPropertyEntity webElementPropertyEntity : getProperties()) {
            cloneProperties.add(webElementPropertyEntity.clone());
        }
        clone.setProperties(cloneProperties);
        return clone;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getName())
                .append(this.getType())
                .append(this.getTag())
                .append(this.getParent())
                .toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebElement)) {
            return false;
        }

        WebElement that = (WebElement) object;
        return new EqualsBuilder().append(this.getName(), that.getName())
                .append(this.getType(), that.getType())
                .append(this.getTag(), that.getTag())
                .append(this.getParent(), that.getParent())
                .append(this.hasChild(), that.hasChild())
                .append(this.hasProperty(), that.hasProperty())
                .isEquals();
    }

    public boolean isSameProperties(WebElement that) {
        if (!(that instanceof WebElement)) {
            return false;
        }

        return new EqualsBuilder().append(this.getName(), that.getName())
                .append(this.getType(), that.getType())
                .append(this.getTag(), that.getTag())
                .append(this.hasProperty(), that.hasProperty())
                .append(this.getXpath(), that.getXpath())
                .isEquals();
    }

    public enum WebElementType {
        ELEMENT, FRAME, PAGE;
    }

    public SelectorMethod getSelectorMethod() {
        return selectorMethod;
    }

    public void setSelectorMethod(SelectorMethod selectorMethod) {
        this.selectorMethod = selectorMethod;
    }

    public void setSelectorValue(SelectorMethod selectorMethod, String selectorValue) {
        selectorCollection.put(selectorMethod, selectorValue);
    }

    public Map<SelectorMethod, String> getSelectorCollection() {
        return selectorCollection;
    }
    
    public WebPage getRoot() {
        if (this instanceof WebPage) {
            return (WebPage) this;
        }
        return getParent().getRoot();
    }

    public String getScriptId() {
        WebPage root = getRoot();
        if (root == this) {
            return "Object Repository/" + getName(); 
        }

        return savedEntity != null ? savedEntity.getIdForDisplay() : root.getScriptId() + "/" + getName();
    }

    public FileEntity getSavedEntity() {
        return savedEntity;
    }

    public void setSavedEntity(FileEntity savedEntity) {
        this.savedEntity = savedEntity;
    }
}
