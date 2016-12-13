package com.kms.katalon.composer.mobile.objectspy.element.impl;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.mobile.keyword.internal.IOSProperties;

public class IosSnapshotMobileElement extends RenderedTreeSnapshotMobileElement<JSONObject> {
    private static final long serialVersionUID = 1462755224775883146L;
    private static final String CHILDREN_JSON_PROPERTY_NAME = "children";
    private static final String INTEGER_PATTERN_FORMAT_STRING = "#####";
    
    public IosSnapshotMobileElement() {
        super();
    }

    public IosSnapshotMobileElement(IosSnapshotMobileElement parentElement) {
        super(parentElement);
    }

    @Override
    public void render(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        convertJsonObjectToWebElementForIos(jsonObject);
        // Create child-Node
        if (jsonObject.has(CHILDREN_JSON_PROPERTY_NAME)) {
            JSONArray childrens = jsonObject.getJSONArray(CHILDREN_JSON_PROPERTY_NAME);
            for (int i = 0; i < childrens.length(); i++) {
                JSONObject child = childrens.getJSONObject(i);
                IosSnapshotMobileElement childNode = new IosSnapshotMobileElement(this);
                getChildrenElement().add(childNode);
                childNode.render(child);
            }
        }
    }


    //TODO: For guys who wrote this or are going to re-factor this, please re-factor this.
    public void convertJsonObjectToWebElementForIos(JSONObject jsonObject) {
        // Extract web element property info
        Map<String, String> properties = getAttributes();

        String propType = jsonObject.getString(IOSProperties.IOS_TYPE);
        properties.put(IOSProperties.IOS_TYPE, propType);

        String propName = null;
        if (jsonObject.has(IOSProperties.IOS_NAME) && (jsonObject.getString(IOSProperties.IOS_NAME).length() > 0)) {
            properties.put(IOSProperties.IOS_NAME, propName = jsonObject.getString(IOSProperties.IOS_NAME));
        }

        String propLabel = null;
        if (jsonObject.has(IOSProperties.IOS_LABEL) && (jsonObject.getString(IOSProperties.IOS_LABEL).length() > 0)) {
            properties.put(IOSProperties.IOS_LABEL, propLabel = jsonObject.getString(IOSProperties.IOS_LABEL));
        }

        String propValue = null;
        if (jsonObject.has(IOSProperties.IOS_VALUE)
                && (String.valueOf(jsonObject.get(IOSProperties.IOS_VALUE)).length() > 0)) {
            properties.put(IOSProperties.IOS_VALUE, propValue = String.valueOf(jsonObject.get(IOSProperties.IOS_VALUE)));
        }

        if (jsonObject.has(IOSProperties.IOS_HINT) && (jsonObject.getString(IOSProperties.IOS_HINT).length() > 0)) {
            properties.put(IOSProperties.IOS_HINT, jsonObject.getString(IOSProperties.IOS_HINT));
        }

        if (jsonObject.has(IOSProperties.IOS_RECT)) {
            DecimalFormat formatter = new DecimalFormat(INTEGER_PATTERN_FORMAT_STRING);
            JSONObject rect = jsonObject.getJSONObject(IOSProperties.IOS_RECT);
            if (rect.has(IOSProperties.IOS_ORIGIN)) {
                JSONObject origin = rect.getJSONObject(IOSProperties.IOS_ORIGIN);
                if (origin.has(GUIObject.X)) {
                    double x = origin.getDouble(GUIObject.X);
                    properties.put(GUIObject.X, String.valueOf(x % 1 > 0 ? x : formatter.format(x)));
                }
                if (origin.has(GUIObject.Y)) {
                    double y = origin.getDouble(GUIObject.Y);
                    properties.put(GUIObject.Y, String.valueOf(y % 1 > 0 ? y : formatter.format(y)));
                }
            }
            if (rect.has(IOSProperties.IOS_SIZE)) {
                JSONObject size = rect.getJSONObject(IOSProperties.IOS_SIZE);
                if (size.has(GUIObject.WIDTH)) {
                    double width = size.getDouble(GUIObject.WIDTH);
                    properties.put(GUIObject.WIDTH, String.valueOf(width % 1 > 0 ? width : formatter.format(width)));
                }
                if (size.has(GUIObject.HEIGHT)) {
                    double height = size.getDouble(GUIObject.HEIGHT);
                    properties.put(GUIObject.HEIGHT, String.valueOf(height % 1 > 0 ? height : formatter.format(height)));
                }
            }
        }

        if (jsonObject.has(IOSProperties.IOS_ENABLED)) {
            properties.put(IOSProperties.IOS_ENABLED, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_ENABLED)));
        }

        if (jsonObject.has(IOSProperties.IOS_VALID)) {
            properties.put(IOSProperties.IOS_VALID, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_VALID)));
        }

        if (jsonObject.has(IOSProperties.IOS_VISIBLE)) {
            properties.put(IOSProperties.IOS_VISIBLE, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_VISIBLE)));
        }

        String guiName = propType;
        if (propName != null) {
            guiName = guiName + " - " + propName;
        } else {
            if (propLabel != null) {
                guiName = guiName + " - " + propLabel;
            } else {
                if (propValue != null) {
                    guiName = guiName + " - " + propValue;
                }
            }
        }

        getAttributes().put(IOSProperties.XPATH, makeXpath());

        setName(guiName);
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
