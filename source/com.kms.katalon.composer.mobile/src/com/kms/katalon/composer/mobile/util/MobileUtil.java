package com.kms.katalon.composer.mobile.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class MobileUtil {

    public static boolean detectAppiumAndNodeJs(Shell activeShell) {
        try {
            String appiumDir = PreferenceStoreManager.getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER)
                    .getString(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY);

            String nodeEnvPath = detectNodeInstallation();
            if (StringUtils.isEmpty(appiumDir) && StringUtils.isEmpty(nodeEnvPath)) {
                WarningDialog.showWarning(activeShell, StringConstants.MSG_NO_APPIUM_AND_NODEJS);
                return false;
            }
            if (StringUtils.isEmpty(appiumDir)) {
                WarningDialog.showWarning(activeShell, StringConstants.MSG_NO_APPIUM);
                return false;
            }
            if (StringUtils.isEmpty(nodeEnvPath)) {
                WarningDialog.showWarning(activeShell, StringConstants.MSG_NO_NODEJS);
                return false;
            }
        } catch (IOException | InterruptedException ex) {
            MessageDialog.openWarning(activeShell, StringConstants.WARNING_TITLE,
                    StringConstants.MSG_FAILED_DETECT_NODEJS);
            return false;
        }
        return true;
    }

    private static String detectNodeInstallation() throws IOException, InterruptedException {
        String cmd = "";
        if (StringUtils.equals(Platform.getOS(), Platform.OS_WIN32)) {
            cmd = "where node";
        } else if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)
                || StringUtils.equals(Platform.getOS(), Platform.OS_LINUX)) {
            // Detect default NODE installation location first
            File nodeJS = new File(StringConstants.MAC_DEFAULT_NODEJS_LOCATION);
            if (nodeJS.exists() && nodeJS.isFile()) {
                return StringConstants.MAC_DEFAULT_NODEJS_LOCATION;
            }
            cmd = "which node";
        }
        Process proc = Runtime.getRuntime().exec(cmd);
        int exitVal = proc.waitFor();
        StringBuilder sb = new StringBuilder();
        if (exitVal == 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static final class WarningDialog extends MessageDialog {

        public WarningDialog(Shell parentShell, String dialogTitle, String dialogMessage) {
            super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.WARNING,
                    new String[] { IDialogConstants.OK_LABEL }, 0);
            int style = SWT.NONE;
            style &= SWT.SHEET;
            setShellStyle(getShellStyle() | style);
        }

        @Override
        public Control createMessageArea(Composite composite) {

            Image image = getImage();
            if (image != null) {
                imageLabel = new Label(composite, SWT.NULL);
                image.setBackground(imageLabel.getBackground());
                imageLabel.setImage(image);
                justify(imageLabel);
            }

            if (message != null) {
                messageLabel = new Label(composite, getMessageLabelStyle());
                messageLabel.setText(message);
                justify(messageLabel, true, false);
            }

            // Empty image
            imageLabel = new Label(composite, SWT.NULL);
            imageLabel.setImage(null);
            justify(imageLabel);

            final StyledText styledText = new StyledText(composite, getMessageLabelStyle());
            styledText.setText(StringConstants.LINK);
            styledText.setBackground(imageLabel.getBackground());

            StyleRange style = new StyleRange();
            style.underline = true;
            style.underlineStyle = SWT.UNDERLINE_LINK;

            int[] ranges = { 0, StringConstants.LINK.length() };
            StyleRange[] styles = { style };
            styledText.setStyleRanges(ranges, styles);

            styledText.addListener(SWT.MouseDown, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    try {
                        int offset = styledText.getOffsetAtLocation(new Point(event.x, event.y));
                        StyleRange styleRange = styledText.getStyleRangeAtOffset(offset);
                        if (styleRange != null && styleRange.underline
                                && styleRange.underlineStyle == SWT.UNDERLINE_LINK) {
                            openUri(new URL(StringConstants.LINK).toURI());
                        }
                    } catch (URISyntaxException | IOException e) {}
                }
            });

            justify(styledText, true, false);

            return composite;
        }

        private void justify(Control control, boolean grabHorizontal, boolean grabVertical) {
            GridDataFactory.fillDefaults()
                    .align(SWT.FILL, SWT.BEGINNING)
                    .grab(grabHorizontal, grabVertical)
                    .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                    .applyTo(control);
        }

        private void justify(Control control) {
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(control);
        }

        private static void openUri(URI uri) throws IOException {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
                return;
            }

            if (Platform.OS_WIN32.equals(Platform.getOS())) {
                openDefaultBrowserOnWindows(uri);
            }
        }

        private static void openDefaultBrowserOnWindows(URI uri) throws IOException {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "\"\"", "\"" + uri.toString() + "\"");
            builder.start();
        }

        public static void showWarning(Shell parentShell, String dialogMessage) {
            WarningDialog dialog = new WarningDialog(parentShell, StringConstants.WARNING_TITLE, dialogMessage);
            dialog.open();
        }
    }
}
