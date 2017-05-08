package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.websocket.AddonHotKeyConfig;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferencePage extends PreferencePageWithHelp {
    private Combo cbbDefaultBrowsers;

    private List<String> browserOptions;

    private ScopedPreferenceStore preferenceStore;

    private Text txtCaptureObject, txtLoadDOMMap;

    private AddonHotKeyConfig hotKeyCaptureObject, hotKeyLoadDomMap;

    private List<Integer> acceptTableKeycodes;

    public ObjectSpyPreferencePage() {
        browserOptions = getDefaultBrowserOptions();

        preferenceStore = PreferenceStoreManager
                .getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);

        initAcceptableKeycode();
    }

    /**
     * Accept key from a..z and ~
     */
    private void initAcceptableKeycode() {
        acceptTableKeycodes = new ArrayList<>();
        for (int index = 'a'; index <= 'z'; index++) {
            acceptTableKeycodes.add(index);
        }
        acceptTableKeycodes.add((int) '`');
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

        createHotKeyComposite(composite);

        initializeInput();
        addControlModifyListeners();

        return composite;
    }

    protected void createHotKeyComposite(Composite composite) {
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
    }

    private void addControlModifyListeners() {
        txtCaptureObject.addKeyListener(
                new KeyCaptureAdapter(txtCaptureObject, hotKeyCaptureObject, acceptTableKeycodes, this));

        txtLoadDOMMap.addKeyListener(new KeyCaptureAdapter(txtLoadDOMMap, hotKeyLoadDomMap, acceptTableKeycodes, this));
    }

    private static class KeyCaptureAdapter extends org.eclipse.swt.events.KeyAdapter {
        private Text text;

        private AddonHotKeyConfig hotkeyConfig;

        private List<Integer> acceptableKeycodes;

        private ObjectSpyPreferencePage parentPage;

        public KeyCaptureAdapter(Text text, AddonHotKeyConfig hotkeyConfig, List<Integer> acceptableKeycodes,
                ObjectSpyPreferencePage parentPage) {
            this.text = text;
            this.hotkeyConfig = hotkeyConfig;
            this.acceptableKeycodes = acceptableKeycodes;
            this.parentPage = parentPage;
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            keyEvent.doit = false;
            int keyCode = keyEvent.keyCode;
            int modifiers = keyEvent.stateMask;
            if (modifiers == SWT.NONE || (modifiers & SWT.COMMAND) != 0 || acceptableKeycodes.indexOf(keyCode) == -1) {
                setParentPageMessage(ObjectspyMessageConstants.WARN_MSG_INVALID_KEY_COMBINATION, WARNING);
                return;
            }
            text.setText(KeyStroke.getInstance(modifiers, keyCode).format());
            hotkeyConfig.setKeyCode(keyCode);
            hotkeyConfig.setModifiers(modifiers);
            setParentPageMessage(null, INFORMATION);
        }

        protected void setParentPageMessage(final String message, final int type) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    parentPage.setMessage(message, type);
                }
            });
        }
    }

    private void initializeInput() {
        // set input for default browser
        int defaultBrowser = browserOptions.indexOf(preferenceStore.getString(WEBUI_OBJECTSPY_DEFAULT_BROWSER));
        cbbDefaultBrowsers.select(defaultBrowser);

        Gson gson = new Gson();
        hotKeyCaptureObject = gson.fromJson(preferenceStore.getString(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT),
                AddonHotKeyConfig.class);
        hotKeyLoadDomMap = gson.fromJson(preferenceStore.getString(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP),
                AddonHotKeyConfig.class);

        txtCaptureObject.setText(
                KeyStroke.getInstance(hotKeyCaptureObject.getModifiers(), hotKeyCaptureObject.getKeyCode()).format());

        txtLoadDOMMap.setText(
                KeyStroke.getInstance(hotKeyLoadDomMap.getModifiers(), hotKeyLoadDomMap.getKeyCode()).format());
    }

    @Override
    protected String getDefaultButtonLabel() {
        return "Restore Defaults";
    }

    @Override
    protected String getApplyButtonLabel() {
        return "Apply";
    }

    @Override
    public boolean performOk() {
        if (hotKeyCaptureObject.equals(hotKeyLoadDomMap)) {
            setErrorMessage(ObjectspyMessageConstants.ERR_MSG_DUPLICATED_HOTKEYS);
            return false;
        }
        String selectedDefaultBrowser = browserOptions.get(cbbDefaultBrowsers.getSelectionIndex());
        preferenceStore.setValue(WEBUI_OBJECTSPY_DEFAULT_BROWSER, selectedDefaultBrowser);

        Gson gson = new Gson();
        preferenceStore.setValue(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT, gson.toJson(hotKeyCaptureObject));

        preferenceStore.setValue(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP, gson.toJson(hotKeyLoadDomMap));
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

        Gson gson = new Gson();
        hotKeyCaptureObject = gson.fromJson(preferenceStore.getDefaultString(WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT),
                AddonHotKeyConfig.class);
        hotKeyLoadDomMap = gson.fromJson(preferenceStore.getDefaultString(WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP),
                AddonHotKeyConfig.class);

        // set input for Hotkeys group
        txtCaptureObject.setText(
                KeyStroke.getInstance(hotKeyCaptureObject.getModifiers(), hotKeyCaptureObject.getKeyCode()).format());

        txtLoadDOMMap.setText(
                KeyStroke.getInstance(hotKeyLoadDomMap.getModifiers(), hotKeyLoadDomMap.getKeyCode()).format());
    }
}
