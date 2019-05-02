package com.kms.katalon.platform.internal.entity.testobject;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.testobject.WebElementProperty;
import com.kms.katalon.entity.repository.WebElementEntity;

public class WebElementEntityImpl implements com.katalon.platform.api.model.testobject.WebElementEntity {

    private final WebElementEntity source;

    public WebElementEntityImpl(WebElementEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return new File(source.getId()).getAbsolutePath();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getLocation();
    }

    @Override
    public String getId() {
        return source.getIdForDisplay();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getImagePath() {
        return source.getImagePath();
    }

    @Override
    public String getParentElementId() {
        if (hasParentElement()) {
            return StringUtils.EMPTY;
        }
        return source.getWebElementProperties()
                .stream()
                .filter(prop -> WebElementEntity.ref_element.equals(prop.getName()))
                .findFirst()
                .get()
                .getValue();
    }

    @Override
    public Map<com.katalon.platform.api.model.testobject.WebElementSelectorMethod, String> getSelectorCollection() {
        if (source.getSelectorCollection() == null) {
            return Collections.emptyMap();
        }
        Map<com.katalon.platform.api.model.testobject.WebElementSelectorMethod, String> selectorCollections = new HashMap<>();
        for (Entry<com.kms.katalon.entity.repository.WebElementSelectorMethod, String> entry : source
                .getSelectorCollection().entrySet()) {
            selectorCollections.put(
                    com.katalon.platform.api.model.testobject.WebElementSelectorMethod.valueOf(entry.getKey().name()),
                    entry.getValue());
        }
        return Collections.unmodifiableMap(selectorCollections);
    }

    @Override
    public com.katalon.platform.api.model.testobject.WebElementSelectorMethod getSelectorMethod() {
        return com.katalon.platform.api.model.testobject.WebElementSelectorMethod
                .valueOf(source.getSelectorMethod().name());
    }

    @Override
    public List<WebElementProperty> getWebElementProperties() {
        if (source.getWebElementProperties() == null) {
            return Collections.emptyList();
        }
        return source.getWebElementProperties()
                .stream()
                .map(prop -> new WebElementPropertyImpl(prop))
                .collect(Collectors.toList());
    }

    @Override
    public List<WebElementProperty> getXpathElementProperties() {
        if (source.getWebElementProperties() == null) {
            return Collections.emptyList();
        }
        return source.getWebElementXpaths()
                .stream()
                .map(prop -> new WebElementPropertyImpl(prop))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasParentElement() {
        return source.getWebElementProperties()
                .stream()
                .filter(prop -> WebElementEntity.ref_element.equals(prop.getName()))
                .findFirst()
                .isPresent();
    }

    @Override
    public boolean isRelativeImagePath() {
        return source.getUseRalativeImagePath();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

}
