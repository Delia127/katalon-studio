package com.kms.katalon.composer.windows.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

import io.appium.java_client.windows.WindowsDriver;

public class SnapshotWindowsElement implements TreeWindowsElement {

    private SnapshotWindowsElement parent;

    private List<TreeWindowsElement> children = new ArrayList<>();

    private String name;
    
    private String tagName;

    private Map<String, String> properties = new HashMap<>();

    private CapturedWindowsElement capturedWindowsElement;

    private String locator;

    private WindowsElementEntity.LocatorStrategy locatorStrategy;

    private String xpath = "";

    private boolean isXpathBuilt;

    private String optionalName = "";

    public SnapshotWindowsElement() {
        this(null);
    }

    public SnapshotWindowsElement(SnapshotWindowsElement parent) {
        this.parent = parent;
    }

    public void render(Element xmlElement) {
        if (xmlElement == null) {
            return;
        }
        convertXMLElementToWindowsElement(xmlElement);
        // Create child-Node
        if (!xmlElement.hasChildNodes()) {
            return;
        }
        NodeList childElementNodes = xmlElement.getChildNodes();
        int count = childElementNodes.getLength();
        for (int i = 0; i < count; i++) {
            Node node = childElementNodes.item(i);
            if (node instanceof Element) {
                SnapshotWindowsElement childNode = new SnapshotWindowsElement(this);
                getChildren().add(childNode);
                childNode.render((Element) node);
            }
        }
    }

    private void convertXMLElementToWindowsElement(Element xmlElement) {
        tagName = xmlElement.getTagName();
        name = tagName;
        for (int i = 0; i < xmlElement.getAttributes().getLength(); i++) {
            Node node = xmlElement.getAttributes().item(i);
            properties.put(node.getNodeName(), node.getNodeValue());
        }

        optionalName = getPropertyValue("Name");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public TreeWindowsElement getParent() {
        return this.parent;
    }

    @Override
    public List<TreeWindowsElement> getChildren() {
        return children;
    }

    @Override
    public CapturedWindowsElement getCapturedElement() {
        return this.capturedWindowsElement;
    }

    @Override
    public CapturedWindowsElement newCapturedElement(WindowsDriver<WebElement> windowsDriver) {
        capturedWindowsElement = new CapturedWindowsElement();
        capturedWindowsElement.setLink(this);
        capturedWindowsElement.setChecked(true);
        capturedWindowsElement.setName(name);
        capturedWindowsElement.setProperties(new HashMap<>(properties));
        capturedWindowsElement.setSnapshotWindowsElement(this);

        buildLocator(windowsDriver);
        capturedWindowsElement.setLocator(locator);
        capturedWindowsElement.setLocatorStrategy(locatorStrategy);
        return capturedWindowsElement;
    }

    private void buildLocator(WindowsDriver<WebElement> windowsDriver) {
        String name = getPropertyValue("Name");
        if (StringUtils.isNotEmpty(name)) {
            List<WebElement> elements = windowsDriver.findElementsByName(name);
            if (elements != null && elements.size() == 1) {
                this.locator = name;
                this.locatorStrategy = LocatorStrategy.NAME;
                return;
            }
        }

        String className = getPropertyValue("ClassName");
        if (StringUtils.isNotEmpty(className)) {
            List<WebElement> elements = windowsDriver.findElementsByClassName(className);
            if (elements != null && elements.size() == 1) {
                this.locator = className;
                this.locatorStrategy = LocatorStrategy.CLASS_NAME;
                return;
            }
        }

        String tagName = this.name;
        if (StringUtils.isNotEmpty(tagName)) {
            List<WebElement> elements = windowsDriver.findElementsByTagName(tagName);
            if (elements != null && elements.size() == 1) {
                this.locator = tagName;
                this.locatorStrategy = LocatorStrategy.TAG_NAME;
                return;
            }
        }


        this.locator = getXPath();
        this.locatorStrategy = LocatorStrategy.XPATH;
        return;
    }

    public String getXPath() {
        if (isXpathBuilt) {
            return xpath;
        }
        xpath = makeXPath();
        isXpathBuilt = true;
        return xpath;
    }

    private String makeXPath() {
        int index = getIndexPropertyForElement(tagName);
        String xpath = StringUtils.isEmpty(tagName) ? "//*" : ("/" + tagName);
        if (index > 0) {
            xpath += "[" + index + "]";
        }
        if (parent == null) {
            // top node, add "/" to select all
            xpath = "/" + xpath;
            return xpath;
        }

        String parentXpath = parent.getXPath();
        xpath = (StringUtils.isEmpty(parentXpath) ? "//*" : parentXpath) + xpath;
        return xpath;
    }

    private int getIndexPropertyForElement(String tagName) {
        if (StringUtils.isEmpty(tagName) || parent == null) {
            return 0;
        }
        int index = 1;
        for (TreeWindowsElement sibling : parent.getChildren()) {
            if (sibling == this) {
                break;
            }
            if (tagName.equals(sibling.getName())) {
                index += 1;
            }
        }
        return index;
    }

    public String getPropertyValue(String propertyName) {
        Optional<String> optProperty = properties.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(propertyName))
                .map(e -> e.getValue())
                .findFirst();
        return optProperty.isPresent() ? optProperty.get() : "";
    }

    @Override
    public void setCapturedElement(CapturedWindowsElement object) {
        this.capturedWindowsElement = object;
    }

    @Override
    public TreeWindowsElement findBestMatch(CapturedWindowsElement needToVerify) {
        return null;
    }

    public void setParent(SnapshotWindowsElement parent) {
        this.parent = parent;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String getOptinalName() {
        return optionalName;
    }
}
