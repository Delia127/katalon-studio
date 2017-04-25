package com.kms.katalon.objectspy.preferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP;

public class ObjectSpyPreferencePage extends PreferencePageWithHelp {
    private Combo cbbDefaultBrowsers;

    private List<String> browserOptions;

    private ScopedPreferenceStore preferenceStore;

    private Text txtCaptureObject, txtLoadDOMMap;

    public ObjectSpyPreferencePage() {
        browserOptions = getDefaultBrowserOptions();

        preferenceStore = PreferenceStoreManager
                .getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }

    private List<String> getDefaultBrowserOptions() {
        return Arrays.asList(ObjectSpyPreferenceDefaultValueInitializer.SUPPORTED_BROWSERS)
                .stream()
                .filter(browser -> !WebUIDriverType.IE_DRIVER.toString().equals(browser)
                        || Platform.OS_WIN32.equals(Platform.getOS()))
                .collect(Collectors.toList());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.horizontalSpacing = 15;
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblDefaultBrowser = new Label(composite, SWT.NONE);
        lblDefaultBrowser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDefaultBrowser.setText(StringConstants.PREF_LBL_DEFAULT_BROWSER);

        cbbDefaultBrowsers = new Combo(composite, SWT.READ_ONLY);
        cbbDefaultBrowsers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbDefaultBrowsers.setItems(browserOptions.toArray(new String[0]));

        Group grpHotKeys = new Group(composite, SWT.NONE);
        grpHotKeys.setText(ObjectspyMessageConstants.PREF_LBL_HOTKEYS);
        GridLayout glGrpHotKeys = new GridLayout(2, false);
        glGrpHotKeys.horizontalSpacing = 15;
        grpHotKeys.setLayout(glGrpHotKeys);
        grpHotKeys.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));

        Label lblCaptureObject = new Label(grpHotKeys, SWT.NONE);
        lblCaptureObject.setText(ObjectspyMessageConstants.PREF_LBL_CAPTURE_OBJECT);

        txtCaptureObject = new Text(grpHotKeys, SWT.BORDER);
        txtCaptureObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblLoadDOMMap = new Label(grpHotKeys, SWT.NONE);
        lblLoadDOMMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblLoadDOMMap.setText(ObjectspyMessageConstants.PREF_LBL_LOAD_DOM_MAP);

        txtLoadDOMMap = new Text(grpHotKeys, SWT.BORDER);
        txtLoadDOMMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        initializeInput();
        addControlModifyListeners();

        return composite;
    }

    private void addControlModifyListeners() {
        txtCaptureObject.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keys = KeyStroke.getInstance(e.stateMask, e.keyCode).format();
                txtCaptureObject.setText(keys);
                e.doit = false;
            }
        });

        txtLoadDOMMap.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keys = KeyStroke.getInstance(e.stateMask, e.keyCode).format();
                txtLoadDOMMap.setText(keys);
                e.doit = false;
            }
        });
    }

    private void initializeInput() {
        // set input for default browser
        int defaultBrowser = browserOptions.indexOf(preferenceStore.getString(WEBUI_OBJECTSPY_DEFAULT_BROWSER));
        cbbDefaultBrowsers.select(defaultBrowser);

        // set input for Hotkeys group
        txtCaptureObject.setText(preferenceStore.getString(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT));

        txtLoadDOMMap.setText(preferenceStore.getString(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP));
    }

    @Override
    public boolean performOk() {
        String selectedDefaultBrowser = browserOptions.get(cbbDefaultBrowsers.getSelectionIndex());
        preferenceStore.setValue(WEBUI_OBJECTSPY_DEFAULT_BROWSER, selectedDefaultBrowser);

        preferenceStore.setValue(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT, txtCaptureObject.getText());

        preferenceStore.setValue(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP, txtLoadDOMMap.getText());
        try {
            preferenceStore.save();
            return true;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Override
    protected void performDefaults() {
        // set default input for default browser
        int defaultBrowser = browserOptions.indexOf(preferenceStore.getDefaultString(WEBUI_OBJECTSPY_DEFAULT_BROWSER));
        cbbDefaultBrowsers.select(defaultBrowser);

        // set default input for Hotkeys group
        txtCaptureObject.setText(preferenceStore.getDefaultString(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT));

        txtLoadDOMMap.setText(preferenceStore.getDefaultString(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP));
    }
}
