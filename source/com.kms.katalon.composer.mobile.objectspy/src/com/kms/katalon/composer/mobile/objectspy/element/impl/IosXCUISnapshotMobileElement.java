package com.kms.katalon.composer.mobile.objectspy.element.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.mobile.keyword.internal.IOSProperties;

public class IosXCUISnapshotMobileElement extends RenderedTreeSnapshotMobileElement<Element> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IosXCUISnapshotMobileElement() {
        super();
    }

    public IosXCUISnapshotMobileElement(IosXCUISnapshotMobileElement parent) {
        super(parent);
    }

    @Override
    public void render(Element xmlElement) {
        if (xmlElement == null) {
            return;
        }
        convertXMLElementToWebElementForXCUITestElement(xmlElement);
        // Create child-Node
        if (!xmlElement.hasChildNodes()) {
            return;
        }
        NodeList childElementNodes = xmlElement.getChildNodes();
        int count = childElementNodes.getLength();
        for (int i = 0; i < count; i++) {
            Node node = childElementNodes.item(i);
            if (node instanceof Element) {
                IosXCUISnapshotMobileElement childNode = new IosXCUISnapshotMobileElement(this);
                getChildrenElement().add(childNode);
                childNode.render((Element) node);
            }
        }
    }

    public void convertXMLElementToWebElementForXCUITestElement(Element xmlElement) {
        Map<String, String> htmlMobileElementProps = getAttributes();
        NamedNodeMap attributesNodeMap = xmlElement.getAttributes();
        htmlMobileElementProps.put(IOSProperties.IOS_TYPE, xmlElement.getTagName());

        for (int index = 0; index < attributesNodeMap.getLength(); index++) {
            Node atrributeNode = attributesNodeMap.item(index);
            if (atrributeNode.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            String nodeValue = atrributeNode.getNodeValue();
            if (StringUtils.isEmpty(nodeValue)) {
                continue;
            }
            htmlMobileElementProps.put(atrributeNode.getNodeName(), nodeValue);
        }
        getAttributes().put(IOSProperties.XPATH, makeXpath());
        setName(getNameForElement(htmlMobileElementProps));
        doubleDimensionValues(htmlMobileElementProps);
    }

    private String getNameForElement(Map<String, String> htmlMobileElementProps) {
        String guiName = htmlMobileElementProps.get(IOSProperties.IOS_TYPE);;
        String propName = htmlMobileElementProps.get(IOSProperties.IOS_NAME);
        if (propName != null) {
            return guiName + " - " + propName;
        }
        String propLabel = htmlMobileElementProps.get(IOSProperties.IOS_LABEL);
        if (propLabel != null) {
            return guiName + " - " + propLabel;
        }
        String propValue = htmlMobileElementProps.get(IOSProperties.IOS_VALUE);
        if (propValue != null) {
            return guiName + " - " + propValue;
        }
        return guiName;
    }

    private void doubleDimensionValues(Map<String, String> htmlMobileElementProps) {
        doubleValue(htmlMobileElementProps, GUIObject.X);
        doubleValue(htmlMobileElementProps, GUIObject.Y);
        doubleValue(htmlMobileElementProps, GUIObject.HEIGHT);
        doubleValue(htmlMobileElementProps, GUIObject.WIDTH);
    }

    private void doubleValue(Map<String, String> htmlMobileElementProps, String propertyName) {
        String elementProperty = htmlMobileElementProps.get(propertyName);
        if (elementProperty == null) {
            return;
        }
        Double propertyDoubleValue = Double.parseDouble(elementProperty);
        htmlMobileElementProps.put(propertyName, String.valueOf(propertyDoubleValue * 2));
    }
    
    @Override
    public MobileDriverType getMobileDriverType() {
        return MobileDriverType.IOS_DRIVER;
    }

    @Override
    public String getTagName() {
        return getAttributes().get(IOSProperties.IOS_TYPE);
    }

}
