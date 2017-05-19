package com.kms.katalon.composer.mobile.recorder.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.composer.mobile.objectspy.element.Converter;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.BasicMobileElement;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.internal.IOSProperties;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public class MobileElementConverter implements Converter<MobileElement, WebElementEntity> {

    @Override
    public WebElementEntity convert(MobileElement element) {
        WebElementEntity newWebElement = new WebElementEntity();
        newWebElement.setName(element.getName());
        List<WebElementPropertyEntity> webElementProperties = new ArrayList<>();

        Map<String, String> attributes = element.getAttributes();
        for (Entry<String, String> entry : attributes.entrySet()) {
            WebElementPropertyEntity webElementPropertyEntity = new WebElementPropertyEntity(entry.getKey(),
                    entry.getValue());
            webElementProperties.add(webElementPropertyEntity);
        }
        newWebElement.setWebElementProperties(webElementProperties);
        return newWebElement;
    }

    public WebElementEntity convert(MobileElement element, FolderEntity folder, MobileDeviceInfo deviceInfo) {
        WebElementEntity newWebElement = convert(element);
        newWebElement.setParentFolder(folder);
        newWebElement.setProject(folder.getProject());
        autoSelectObjectProperties(newWebElement, deviceInfo);
        return newWebElement;
    }

    @Override
    public MobileElement revert(WebElementEntity webElement) {
        MobileElement mobileElement = new BasicMobileElement();
        mobileElement.setName(webElement.getName());
        Map<String, String> attributes = mobileElement.getAttributes();
        for (WebElementPropertyEntity propertyEntity : webElement.getWebElementProperties()) {
            attributes.put(propertyEntity.getName(), propertyEntity.getValue());
        }
        return mobileElement;
    }

    private void autoSelectObjectProperties(WebElementEntity entity, MobileDeviceInfo deviceInfo) {
        List<String> typicalProps = new ArrayList<>();
        if (isMobileDriverTypeOf(MobileDriverType.ANDROID_DRIVER, deviceInfo)) {
            typicalProps.addAll(Arrays.asList(AndroidProperties.ANDROID_TYPICAL_PROPERTIES));
        } else if (isMobileDriverTypeOf(MobileDriverType.IOS_DRIVER, deviceInfo)) {
            typicalProps.addAll(Arrays.asList(IOSProperties.IOS_TYPICAL_PROPERTIES));
        }
        for (WebElementPropertyEntity prop : entity.getWebElementProperties()) {
            prop.setIsSelected(typicalProps.contains(prop.getName()));
        }
    }

    private boolean isMobileDriverTypeOf(MobileDriverType type, MobileDeviceInfo deviceInfo) {
        return MobileInspectorController.getMobileDriverType(deviceInfo) == type;
    }
}
