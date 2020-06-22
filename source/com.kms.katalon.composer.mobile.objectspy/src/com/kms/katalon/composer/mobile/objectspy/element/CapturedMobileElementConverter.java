package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.internal.IOSProperties;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class CapturedMobileElementConverter implements Converter<CapturedMobileElement, WebElementEntity> {
    @Override
    public WebElementEntity convert(CapturedMobileElement element) {
        WebElementEntity newWebElement = new WebElementEntity();
        if (element == null) {
            return null;
        }
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

    public WebElementEntity convert(CapturedMobileElement element, FolderEntity folder, MobileDriverType mobileDriverType)
            throws Exception {
        WebElementEntity newWebElement = convert(element);
        newWebElement.setName(ObjectRepositoryController.getInstance().getAvailableWebElementName(folder,
                ObjectRepositoryController.toValidFileName(StringUtils.trim(element.getName()))));
        newWebElement.setParentFolder(folder);
        newWebElement.setProject(folder.getProject());
        autoSelectObjectProperties(newWebElement, mobileDriverType);
        return newWebElement;
    }

    @Override
    public CapturedMobileElement revert(WebElementEntity webElement) {
        CapturedMobileElement mobileElement = new CapturedMobileElement();
        mobileElement.setName(webElement.getName());
        Map<String, String> attributes = mobileElement.getAttributes();
        for (WebElementPropertyEntity propertyEntity : webElement.getWebElementProperties()) {
            attributes.put(propertyEntity.getName(), propertyEntity.getValue());
        }
        return mobileElement;
    }

    private void autoSelectObjectProperties(WebElementEntity entity, MobileDriverType mobileDriverType) {
        List<String> typicalProps = new ArrayList<>();
        if (mobileDriverType == MobileDriverType.ANDROID_DRIVER) {
            typicalProps.addAll(Arrays.asList(AndroidProperties.ANDROID_TYPICAL_PROPERTIES));
        } else if (mobileDriverType == MobileDriverType.IOS_DRIVER) {
            typicalProps.addAll(Arrays.asList(IOSProperties.IOS_TYPICAL_PROPERTIES));
        }
        for (WebElementPropertyEntity prop : entity.getWebElementProperties()) {
            prop.setIsSelected(typicalProps.contains(prop.getName()));
        }
    }
}
