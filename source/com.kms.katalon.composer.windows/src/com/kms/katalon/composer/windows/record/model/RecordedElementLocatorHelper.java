package com.kms.katalon.composer.windows.record.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

public class RecordedElementLocatorHelper {

    private WindowsRecordedElement recordedElement;

    private WindowsRecordedPayload payload;

    private LocatorStrategy locatorStrategy;
    
    private String locator;

    public RecordedElementLocatorHelper(WindowsRecordedPayload payload) {
        this.payload = payload;
        this.recordedElement = payload.getElement();
    }
    
    public CapturedWindowsElement getCapturedElement() {
        CapturedWindowsElement element = new CapturedWindowsElement();
        element.setName(getCapitalizedName(recordedElement.getType()));
        element.setProperties(recordedElement.getAttributes());
        
        buildLocator();
        
        element.setLocator(locator);
        element.setLocatorStrategy(locatorStrategy);
        
        return element;
    }

    private String getCapitalizedName(String name) {
        return StringUtils.capitalize(name).replace("item", "Item").replace(" ", "");
    }

    private void buildLocator() {
        Map<String, String> attributes = recordedElement.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            locatorStrategy = LocatorStrategy.XPATH;
            locator = "";
            return;
        }

        String automationId = attributes.get("AutomationId");
        if (StringUtils.isNotEmpty(automationId)) {
            locatorStrategy = LocatorStrategy.ACCESSIBILITY_ID;
            locator = automationId;
            return;
        }
        
        locatorStrategy = LocatorStrategy.XPATH;
        locator = buildXPath();
    }

    private String buildXPath() {
        StringBuilder sb = new StringBuilder();
        List<WindowsRecordedElement> elements = payload.getParent();
        for (int i = 0; i < elements.size(); i++) {
            WindowsRecordedElement p = elements.get(i);
            if (p.getAttributes() == null || p.getAttributes().isEmpty()) {
                continue;
            }
            sb.append(buildPartialXPath(p, i == 1));
            sb.append("/");
        }
        sb.append(buildPartialXPath(recordedElement, false));
        return sb.toString();
    }
    
    private String buildPartialXPath(WindowsRecordedElement e, boolean isMainWindow) {
        String type = getCapitalizedName(e.getType());
        if (isMainWindow) {
            return type;
        }
        String automationId = e.getAttributes().get("AutomationId");
        String className = e.getAttributes().get("ClassName");
        String name = e.getAttributes().get("Name");
        
        if (StringUtils.isEmpty(automationId) && StringUtils.isEmpty(className) && StringUtils.isEmpty(name)) {
            return type;
        }
        
        if (StringUtils.isNotEmpty(automationId)) {
            return String.format("%s[@AutomationId = \"%s\"]", type, automationId);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(type + "[");
        String predicate = "";
        
        if (StringUtils.isNotEmpty(className)) {
            predicate = String.format("@ClassName = \"%s\"", className);
            sb.append(predicate);
        }
        
        if (StringUtils.isNotEmpty(name)) {
            if (StringUtils.isNotEmpty(predicate)) {
                sb.append(" && ");
            }
            sb.append(String.format("@Name = \"%s\"", name));
        }

        sb.append("]");
        return sb.toString();
    }
}
