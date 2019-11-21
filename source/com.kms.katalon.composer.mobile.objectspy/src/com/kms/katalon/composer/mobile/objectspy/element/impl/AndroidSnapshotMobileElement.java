package com.kms.katalon.composer.mobile.objectspy.element.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;

public class AndroidSnapshotMobileElement extends RenderedTreeSnapshotMobileElement<Element> {
    private static final long serialVersionUID = -8005661770483917366L;

    public AndroidSnapshotMobileElement() {
        super();
    }
    
    public AndroidSnapshotMobileElement(AndroidSnapshotMobileElement parent) {
        super(parent);
    }

    @Override
    public void render(Element xmlElement) {
        if (xmlElement == null) {
            return;
        }
        convertXMLElementToWebElementForAndroid(xmlElement);
        // Create child-Node
        if (!xmlElement.hasChildNodes()) {
            return;
        }
        NodeList childElementNodes = xmlElement.getChildNodes();
        int count = childElementNodes.getLength();
        for (int i = 0; i < count; i++) {
            Node node = childElementNodes.item(i);
            if (node instanceof Element) {
                AndroidSnapshotMobileElement childNode = new AndroidSnapshotMobileElement(this);
                getChildrenElement().add(childNode);
                childNode.render((Element) node);
            }
        }
    }

    //TODO: For guys who wrote this or are going to re-factor this, please re-factor this.
    public void convertXMLElementToWebElementForAndroid(Element xmlElement) {

        Map<String, String> htmlMobileElementProps = getAttributes();

        if (StringUtils.isNotEmpty(xmlElement.getAttribute(AndroidProperties.ANDROID_CLASS))) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_CLASS,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CLASS));
        }

        String instance = "0";
        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_INSTANCE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_INSTANCE).length() > 0)) {
            instance = xmlElement.getAttribute(AndroidProperties.ANDROID_INSTANCE);
            htmlMobileElementProps.put(AndroidProperties.ANDROID_INSTANCE, instance);
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_TEXT)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_TEXT).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_TEXT,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_TEXT));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_RESOURCE_ID)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_RESOURCE_ID).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_RESOURCE_ID,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_RESOURCE_ID));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_PACKAGE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_PACKAGE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_PACKAGE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_PACKAGE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CONTENT_DESC)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CONTENT_DESC).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_CONTENT_DESC,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CONTENT_DESC));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CHECKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKABLE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_CHECKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CHECKED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKED).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_CHECKED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CLICKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CLICKABLE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_CLICKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CLICKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_ENABLED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_ENABLED).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_ENABLED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_ENABLED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_FOCUSABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSABLE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_FOCUSABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_FOCUSED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSED).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_FOCUSED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_SCROLLABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_SCROLLABLE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_SCROLLABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_SCROLLABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_LONG_CLICKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_PASSWORD)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_PASSWORD).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_PASSWORD,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_PASSWORD));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_SELECTED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_SELECTED).length() > 0)) {
            htmlMobileElementProps.put(AndroidProperties.ANDROID_SELECTED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_SELECTED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_BOUNDS)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_BOUNDS).length() > 0)) {
            String bounds = xmlElement.getAttribute(AndroidProperties.ANDROID_BOUNDS);
            int left = Integer.parseInt(bounds.substring(1, bounds.indexOf(',')));
            int top = Integer.parseInt(bounds.substring(bounds.indexOf(',') + 1, bounds.indexOf(']')));
            int right = Integer.parseInt(bounds.substring(bounds.lastIndexOf('[') + 1, bounds.lastIndexOf(',')));
            int bottom = Integer.parseInt(bounds.substring(bounds.lastIndexOf(',') + 1, bounds.lastIndexOf(']')));

            htmlMobileElementProps.put(GUIObject.X, String.valueOf(left));
            htmlMobileElementProps.put(GUIObject.Y, String.valueOf(top));
            htmlMobileElementProps.put(GUIObject.WIDTH, String.valueOf(right - left));
            htmlMobileElementProps.put(GUIObject.HEIGHT, String.valueOf(bottom - top));
        }

        NamedNodeMap attributes = xmlElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            String attrName = attributes.item(i).getNodeName();
            String attrValue = attributes.item(i).getNodeValue();
            if (!htmlMobileElementProps.containsKey(attrName)) {
                htmlMobileElementProps.put(attrName, attrValue);
            }
        }

        String guiName = htmlMobileElementProps.get(AndroidProperties.ANDROID_CLASS);
        if (StringUtils.isNotEmpty(guiName)) {
            guiName += instance;
            if (htmlMobileElementProps.get(AndroidProperties.ANDROID_TEXT) != null) {
                guiName += " - " + htmlMobileElementProps.get(AndroidProperties.ANDROID_TEXT);
            }
            if (guiName.contains("\n")) {
                guiName = guiName.replace("\n", "");
            }
        } else {
            guiName = xmlElement.getTagName();
            guiName += instance;
        }
        setName(guiName);
        
        htmlMobileElementProps.put(AndroidProperties.XPATH, makeXpath());
    }

    @Override
    public MobileDriverType getMobileDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }

    @Override
    public String getXpath() {
        return getAttributes().get(AndroidProperties.XPATH);
    }

    @Override
    public String getTagName() {
        return getAttributes().get(AndroidProperties.ANDROID_CLASS);
    }
}
