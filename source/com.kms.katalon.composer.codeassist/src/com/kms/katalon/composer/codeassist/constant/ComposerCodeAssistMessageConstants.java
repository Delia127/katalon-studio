package com.kms.katalon.composer.codeassist.constant;

import org.eclipse.osgi.util.NLS;

public class ComposerCodeAssistMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.codeassist.constant.composerCodeAssistMessages";

    public static String KEYWORD_DESC_PATH;

    public static String KEYWORD_DESC_BUTTON_TOOLTIP;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerCodeAssistMessageConstants.class);
    }

    private ComposerCodeAssistMessageConstants() {
    }
}
