package com.kms.katalon.composer.initializer;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class DefaultTextFontInitializer implements ApplicationInitializer {

    private static final String DF_FONT_ON_WIN = "Consolas";

    private static final String DF_FONT_ON_MAC = "Monaco";

    private static final String FALLBACK_FONT = "Courier New";

    @Override
    public void setup() {
        ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.WORKBENCH_WINDOW_ID);

        if (store.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED)) {
            return;
        }

        FontDescriptor fontDescriptor = null;
        switch (Platform.getOS()) {
            case Platform.OS_MACOSX:
                fontDescriptor = FontDescriptor.createFrom(DF_FONT_ON_MAC, 11, SWT.NORMAL);
                break;
            case Platform.OS_WIN32:
                fontDescriptor = FontDescriptor.createFrom(DF_FONT_ON_WIN, 10, SWT.NORMAL);
                break;
        }
        if (fontDescriptor == null) {
            fontDescriptor = FontDescriptor.createFrom(FALLBACK_FONT, 12, SWT.NORMAL);
        }
        FontData defaultFont = fontDescriptor.getFontData()[0];
        store.setValue(JFaceResources.TEXT_FONT, defaultFont.toString());
        store.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, true);
        try {
            store.save();
        } catch (IOException e) {
            logError(e);
        }
    }

}
