package com.kms.katalon.composer.mobile.objectspy.element;

import java.io.Serializable;
import java.util.Map;

public interface MobileElement extends Cloneable, Serializable {
    String getName();

    void setName(String name);

    String getXpath();

    Map<String, String> getAttributes();

    MobileElement clone();
}
