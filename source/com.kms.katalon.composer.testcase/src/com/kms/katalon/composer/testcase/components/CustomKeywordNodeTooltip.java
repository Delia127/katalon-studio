package com.kms.katalon.composer.testcase.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.log.LoggerSingleton;

@SuppressWarnings("restriction")
public class CustomKeywordNodeTooltip extends AbstractKeywordNodeTooltip implements IPropertyChangeListener {

    private BrowserInformationControl iControl;
    private String fgStyleSheet;

    public CustomKeywordNodeTooltip(Control control) {
        this.control = control;
    }

    @Override
    protected void initComponents(Composite parent) {
        String font = PreferenceConstants.APPEARANCE_JAVADOC_FONT;

        iControl = new BrowserInformationControl(tip, font, "") {
            @Override
            public IInformationControlCreator getInformationPresenterControlCreator() {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
                IWorkbenchPage activePage = activeWindow.getActivePage();
                IWorkbenchPart activePart = activePage.getActivePart();
                return new JavadocHover.PresenterControlCreator(activePart.getSite());
            }

            @Override
            public boolean isResizable() {
                return true;
            }
        };

        iControl.setInput(getHoverInfo());

        iControl.setLocation(location);

        iControl.setVisible(true);

        Point size = getBestSizeForKeywordDescriptionPopup();
        iControl.setSize(size.x, size.y);

        JFaceResources.getColorRegistry().addListener(this);
    }

    private BrowserInformationControlInput getHoverInfo() {
        StringBuffer buffer = new StringBuffer();

        if (StringUtils.isNotBlank(keywordName)) {
            HTMLPrinter.addSmallHeader(buffer, keywordName);
            buffer.append("<br>"); //$NON-NLS-1$
        }

        Reader reader = null;

        String content = text;
        if (content != null) {
            reader = new StringReader(content);
            HTMLPrinter.addParagraph(buffer, reader);
        }

        if (buffer.length() > 0) {
            ColorRegistry registry = JFaceResources.getColorRegistry();
            RGB fgRGB = registry.getRGB("org.eclipse.jdt.ui.Javadoc.foregroundColor"); //$NON-NLS-1$
            RGB bgRGB = registry.getRGB("org.eclipse.jdt.ui.Javadoc.backgroundColor"); //$NON-NLS-1$

            HTMLPrinter.insertPageProlog(buffer, 0, fgRGB, bgRGB, getStyleSheet());
            HTMLPrinter.addPageEpilog(buffer);
            String javadoc = buffer.toString();
            return new BrowserInformationControlInput(null) {

                @Override
                public String getHtml() {
                    return javadoc;
                }

                @Override
                public Object getInputElement() {
                    return javadoc;
                }

                @Override
                public String getInputName() {
                    return "";
                }

                @Override
                public int getLeadingImageWidth() {
                    return 20;
                }
            };
        }

        return null;
    }

    private String getStyleSheet() {
        if (fgStyleSheet == null) {
            fgStyleSheet = loadStyleSheet("/JavadocHoverStyleSheet.css"); //$NON-NLS-1$
        }
        String css = fgStyleSheet;
        if (css != null) {
            FontData fontData = JFaceResources.getFontRegistry()
                    .getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
            css = HTMLPrinter.convertTopLevelFont(css, fontData);
        }

        return css;
    }

    public static String loadStyleSheet(String styleSheetName) {
        Bundle bundle = Platform.getBundle(JavaPlugin.getPluginId());
        URL styleSheetURL = bundle.getEntry(styleSheetName);
        if (styleSheetURL == null) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
            StringBuilder buffer = new StringBuilder(1500);
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append('\n');
                line = reader.readLine();
            }

            FontData fontData = JFaceResources.getFontRegistry()
                    .getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
            return HTMLPrinter.convertTopLevelFont(buffer.toString(), fontData);
        } catch (IOException ex) {
            LoggerSingleton.logError(ex);
            return ""; //$NON-NLS-1$
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void setHoverColors() {
        ColorRegistry registry = JFaceResources.getColorRegistry();
        Color fgRGB = registry.get("org.eclipse.jdt.ui.Javadoc.foregroundColor"); //$NON-NLS-1$
        Color bgRGB = registry.get("org.eclipse.jdt.ui.Javadoc.backgroundColor"); //$NON-NLS-1$
        iControl.setForegroundColor(fgRGB);
        iControl.setBackgroundColor(bgRGB);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (iControl != null && (property.equals("org.eclipse.jdt.ui.Javadoc.foregroundColor") //$NON-NLS-1$
                || property.equals("org.eclipse.jdt.ui.Javadoc.backgroundColor"))) { //$NON-NLS-1$
            setHoverColors();
        }
    }
}
