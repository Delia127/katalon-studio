package com.kms.katalon.composer.project.sample;

import org.apache.commons.lang3.StringUtils;

public enum SampleProjectType {
    WEBUI, MOBILE, WS, MIXED;

    private SampleProjectType() {

    }

    public static SampleProjectType fromString(String type) {
        if (StringUtils.isEmpty(type)) {
            return SampleProjectType.MIXED;
        }
        switch (type.toLowerCase()) {
            case "webui":
                return WEBUI;
            case "mobile":
                return MOBILE;
            case "ws":
                return WS;
            default:
                return MIXED;
        }
    }
}
