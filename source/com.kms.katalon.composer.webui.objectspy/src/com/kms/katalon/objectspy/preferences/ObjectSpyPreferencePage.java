package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT;
import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.gson.Gson;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.util.KeyUtils;
import com.kms.katalon.objectspy.websocket.AddonHotKeyConfig;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferencePage extends PreferencePageWithHelp {
    private Combo cbbDefaultBrowsers;

    private List<String> browserOptions;

    private ScopedPreferenceStore preferenceStore;

    private JTextField loadDomMapTextField, captureObjectTextField;

    private AddonHotKeyConfig hotKeyCaptureObject, hotKeyLoadDomMap;

    private List<Integer> acceptTableKeycodes;

    public ObjectSpyPreferencePage() {
        browserOptions = getDefaultBrowserOptions();

        preferenceStore = PreferenceStoreManager
                .getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);

        initAcceptableKeycode();
    }

    /**
     * Accept key from a..z, 0..9 and ~
     */
    private void initAcceptableKeycode() {
        acceptTableKeycodes = new ArrayList<>();
        for (int index = KeyEvent.VK_0; index <= KeyEvent.VK_9; index++) {
            acceptTableKeycodes.add(index);
        }
        for (int index = KeyEvent.VK_A; index <= KeyEvent.VK_Z; index++) {
            acceptTableKeycodes.add(index);
        }
        acceptTableKeycodes.add(KeyEvent.VK_BACK_QUOTE);
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

        Composite swtAwtComposite = new Composite(grpHotKeys, SWT.EMBEDDED);
        swtAwtComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        Frame frame = SWT_AWT.new_Frame(swtAwtComposite);

        JPanel panel = new JPanel();
        panel.setFont(getCurrentSystemFont());
        panel.setLayout(new java.awt.GridBagLayout());
        panel.add(new java.awt.Label(ObjectspyMessageConstants.PREF_LBL_CAPTURE_OBJECT),
                createGridBagConstrains(0, 0, new Insets(10, 0, 0, 0)));

        captureObjectTextField = createTextField();
        panel.add(captureObjectTextField,
                createGridBagConstrains(1, 0, 1.0, 0.5, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0)));

        panel.add(new java.awt.Label(ObjectspyMessageConstants.PREF_LBL_LOAD_DOM_MAP),
                createGridBagConstrains(0, 1, new Insets(10, 0, 0, 0)));

        loadDomMapTextField = createTextField();
        panel.add(loadDomMapTextField,
                createGridBagConstrains(1, 1, 1.0, 0.5, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0)));

        frame.add(panel);
    }

    private static GridBagConstraints createGridBagConstrains(int gridX, int gridY, Insets insets) {
        return createGridBagConstrains(gridX, gridY, 0, 0, GridBagConstraints.NONE, insets);
    }

    private static GridBagConstraints createGridBagConstrains(int gridX, int gridY, double weightX, double weightY,
            int fill, Insets insets) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = weightX;
        gridBagConstraints.weighty = weightY;
        gridBagConstraints.fill = fill;
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }

    private static java.awt.Font getCurrentSystemFont() {
        Font currentSystemFont = Display.getDefault().getSystemFont();
        FontData fontData = currentSystemFont.getFontData()[0];

        int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
        int awtFontSize = (int) Math.round((double) fontData.getHeight() * resolution / 72.0);
        return new java.awt.Font(fontData.getName(), fontData.getStyle(), awtFontSize);
    }

    private static JTextField createTextField() {
        final JTextField textField = new JTextField();
        textField.setBorder(
                BorderFactory.createCompoundBorder(textField.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        return textField;
    }

    private void addControlModifyListeners() {
        captureObjectTextField.addKeyListener(
                new KeyCaptureAdapter(captureObjectTextField, hotKeyCaptureObject, acceptTableKeycodes, this));

        loadDomMapTextField.addKeyListener(
                new KeyCaptureAdapter(loadDomMapTextField, hotKeyLoadDomMap, acceptTableKeycodes, this));
    }

    private static class KeyCaptureAdapter extends KeyAdapter {
        private JTextField textField;

        private AddonHotKeyConfig hotkeyConfig;

        private List<Integer> acceptableKeycodes;

        private ObjectSpyPreferencePage parentPage;

        public KeyCaptureAdapter(JTextField textField, AddonHotKeyConfig hotkeyConfig, List<Integer> acceptableKeycodes,
                ObjectSpyPreferencePage parentPage) {
            this.textField = textField;
            this.hotkeyConfig = hotkeyConfig;
            this.acceptableKeycodes = acceptableKeycodes;
            this.parentPage = parentPage;
        }

        @Override
        public void keyPressed(java.awt.event.KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            int modifiers = keyEvent.getModifiers();
            if (modifiers == java.awt.event.KeyEvent.VK_UNDEFINED || KeyUtils.isModifier(keyCode)
                    || acceptableKeycodes.indexOf(keyCode) == -1) {
                setParentPageMessage(ObjectspyMessageConstants.ObjectSpyPreferencePage_WARN_MSG_INVALID_KEY_COMBINATION,
                        WARNING);
                return;
            }
            textField.setText(getTextForKeyStroke(keyCode, modifiers));
            hotkeyConfig.setKeyCode(keyCode);
            hotkeyConfig.setModifiers(modifiers);
            keyEvent.consume();
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

        @Override
        public void keyTyped(java.awt.event.KeyEvent keyEvent) {
            keyEvent.consume();
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

        // set input for Hotkeys group
        captureObjectTextField.setText(getTextForKeyStroke(hotKeyCaptureObject));

        loadDomMapTextField.setText(getTextForKeyStroke(hotKeyLoadDomMap));
    }

    private static String getTextForKeyStroke(AddonHotKeyConfig addonHotKeyConfig) {
        return getTextForKeyStroke(addonHotKeyConfig.getKeyCode(), addonHotKeyConfig.getModifiers());
    }

    private static String getTextForKeyStroke(int keycode, int modifiers) {
        return java.awt.event.KeyEvent.getKeyModifiersText(modifiers) + "+" + KeyUtils.getKeyText(keycode);
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
        captureObjectTextField.setText(getTextForKeyStroke(hotKeyCaptureObject));

        loadDomMapTextField.setText(getTextForKeyStroke(hotKeyLoadDomMap));
    }
}
