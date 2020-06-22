package com.kms.katalon.composer.mobile.objectspy.element;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.repository.MobileElementEntity.LocatorStrategy;

public interface SnapshotMobileElement<T> extends TreeMobileElement {

    void render(T element);

    MobileDriverType getMobileDriverType();

    String getTagName();
    
    String getLocator();

    LocatorStrategy getLocatorStrategy();
}
