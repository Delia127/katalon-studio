package com.kms.katalon.composer.windows.element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.composer.mobile.objectspy.element.Converter;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class CapturedWindowsElementConverter implements Converter<CapturedWindowsElement, WindowsElementEntity> {

    @Override
    public WindowsElementEntity convert(CapturedWindowsElement capturedElement) {
        if (capturedElement == null) {
            return null;
        }
        WindowsElementEntity windowsElementEntity = new WindowsElementEntity();
        windowsElementEntity.setName(capturedElement.getName());
        windowsElementEntity.setLocator(capturedElement.getLocator());
        windowsElementEntity.setLocatorStrategy(capturedElement.getLocatorStrategy());

        List<WebElementPropertyEntity> properties = capturedElement.getProperties().entrySet().stream().map(e -> {
            WebElementPropertyEntity property = new WebElementPropertyEntity();
            property.setName(e.getKey());
            property.setValue(e.getValue());
            return property;
        }).collect(Collectors.toList());

        windowsElementEntity.setProperties(properties);
        return windowsElementEntity;
    }

    @Override
    public CapturedWindowsElement revert(WindowsElementEntity windowsElement) {
        if (windowsElement == null) {
            return null;
        }
        CapturedWindowsElement capturedWindowsElement = new CapturedWindowsElement();
        capturedWindowsElement.setName(windowsElement.getName());
        capturedWindowsElement.setLocator(windowsElement.getLocator());
        capturedWindowsElement.setLocatorStrategy(windowsElement.getLocatorStrategy());

        Map<String, String> properties = new HashMap<>(windowsElement.getProperties().stream().collect(
                Collectors.toMap(WebElementPropertyEntity::getName, WebElementPropertyEntity::getValue)));

        capturedWindowsElement.setProperties(properties);

        return capturedWindowsElement;
    }

}
