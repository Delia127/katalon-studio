package com.kms.katalon.composer.testcase.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.OpenBrowserUtil;
import org.eclipse.jdt.internal.ui.infoviews.JavadocView;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.util.TypeUtil;
import com.kms.katalon.util.jdt.JDTUtil;

@SuppressWarnings("restriction")
public class CustomKeywordNodeTooltip extends AbstractKeywordNodeTooltip implements IPropertyChangeListener {

    private static final long LABEL_FLAGS = JavaElementLabels.ALL_FULLY_QUALIFIED | JavaElementLabels.M_PRE_RETURNTYPE
            | JavaElementLabels.M_PARAMETER_ANNOTATIONS | JavaElementLabels.M_PARAMETER_TYPES
            | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_EXCEPTIONS
            | JavaElementLabels.F_PRE_TYPE_SIGNATURE | JavaElementLabels.M_PRE_TYPE_PARAMETERS
            | JavaElementLabels.T_TYPE_PARAMETERS | JavaElementLabels.USE_RESOLVED;

    private BrowserInformationControl iControl;

    private String fgStyleSheet;

    private String keywordClass;

    private String keywordName;

    private String[] parameterTypes;
    
    public CustomKeywordNodeTooltip(Control control, String keywordName, String[] parameterTypes) {
        this.control = control;
        
        int lastDotIdx = keywordName.lastIndexOf(".");
        this.keywordClass = keywordName.substring(0, lastDotIdx);
        this.keywordName = keywordName.substring(lastDotIdx + 1);
        this.parameterTypes = parameterTypes;
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
        
        addLinkListener(iControl);
    }

    private BrowserInformationControlInput getHoverInfo() {
        StringBuffer buffer = new StringBuffer();

        IMethod method = getKeywordMethod();
        if (method != null) {
            HTMLPrinter.addSmallHeader(buffer, getInfoText(method, true));
            buffer.append("<br>"); //$NON-NLS-1$
        }

        Reader reader = null;

        String content;
        try {
            content = JavadocContentAccess2.getHTMLContent(method, true);
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            content = "";
        }
        
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
            return new JavadocBrowserInformationControlInput(null, method, javadoc, 200) {

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
    
    private static void addLinkListener(final BrowserInformationControl browserInfoControl) {
        browserInfoControl.addLocationListener(JavaElementLinks.createLocationListener(new JavaElementLinks.ILinkHandler() {
            @Override
            public void handleJavadocViewLink(IJavaElement linkTarget) {
                browserInfoControl.notifyDelayedInputChange(null);
                browserInfoControl.setVisible(false);
                browserInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
                try {
                    JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
                    view.setInput(linkTarget);
                } catch (PartInitException e) {
                    JavaPlugin.log(e);
                }
            }

            @Override
            public void handleInlineJavadocLink(IJavaElement linkTarget) {
                JavadocBrowserInformationControlInput hoverInfo= JavadocHover.getHoverInfo(new IJavaElement[] { linkTarget }, null, null, (JavadocBrowserInformationControlInput) (browserInfoControl.getInput()));
                if (browserInfoControl.hasDelayedInputChangeListener())
                    browserInfoControl.notifyDelayedInputChange(hoverInfo);
                else
                    browserInfoControl.setInput(hoverInfo);
            }

            @Override
            public void handleDeclarationLink(IJavaElement linkTarget) {
                browserInfoControl.notifyDelayedInputChange(null);
                browserInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
                try {
                    //FIXME: add hover location to editor navigation history?
                    openDeclaration(linkTarget);
                } catch (PartInitException e) {
                    JavaPlugin.log(e);
                } catch (JavaModelException e) {
                    JavaPlugin.log(e);
                }
            }

            @Override
            public boolean handleExternalLink(URL url, Display display) {
                browserInfoControl.notifyDelayedInputChange(null);
                browserInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose

                // Open attached Javadoc links
                OpenBrowserUtil.open(url, display);

                return true;
            }

            @Override
            public void handleTextSet() {
            }
        }));
    }
    
    public static IEditorPart openDeclaration(IJavaElement element) throws PartInitException, JavaModelException {
        if (!(element instanceof IPackageFragment)) {
            return JavaUI.openInEditor(element);
        }

        IPackageFragment packageFragment= (IPackageFragment) element;
        ITypeRoot typeRoot;
        IPackageFragmentRoot root= (IPackageFragmentRoot) packageFragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
            typeRoot= packageFragment.getClassFile(JavaModelUtil.PACKAGE_INFO_CLASS);
        } else {
            typeRoot= packageFragment.getCompilationUnit(JavaModelUtil.PACKAGE_INFO_JAVA);
        }

        // open the package-info file in editor if one exists
        if (typeRoot.exists())
            return JavaUI.openInEditor(typeRoot);

        // open the package.html file in editor if one exists
        Object[] nonJavaResources= packageFragment.getNonJavaResources();
        for (Object nonJavaResource : nonJavaResources) {
            if (nonJavaResource instanceof IFile) {
                IFile file= (IFile) nonJavaResource;
                if (file.exists() && JavaModelUtil.PACKAGE_HTML.equals(file.getName())) {
                    return EditorUtility.openInEditor(file, true);
                }
            }
        }

        // select the package in the Package Explorer if there is no associated package Javadoc file
        PackageExplorerPart view= (PackageExplorerPart) JavaPlugin.getActivePage().showView(JavaUI.ID_PACKAGES);
        view.tryToReveal(packageFragment);
        return null;
    }

    private IMethod getKeywordMethod() {
        try {
            IProject project = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
            IMethod method = JDTUtil.findMethodWithLooseParamTypesMatching(
                    project,
                    keywordClass,
                    keywordName,
                    TypeUtil.toReadableTypes(parameterTypes));
            return method;
        } catch (JavaModelException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
    
    private String getInfoText(IJavaElement element, boolean allowImage) {
        long flags = LABEL_FLAGS;

        StringBuffer label = new StringBuffer(JavaElementLinks.getElementLabel(element, flags));

        return getImageAndLabel(element, allowImage, label.toString());
    }

    public String getImageAndLabel(IJavaElement element, boolean allowImage, String label) {
        StringBuilder buf = new StringBuilder();
        int imageWidth = 16;
        int imageHeight = 16;
        int labelLeft = 20;
        int labelTop = 2;

        buf.append("<div style='word-wrap: break-word; position: relative; "); //$NON-NLS-1$

        String imageSrcPath = allowImage ? getImageURL(element) : null;
        if (imageSrcPath != null) {
            buf.append("margin-left: ").append(labelLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("padding-top: ").append(labelTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        buf.append("'>"); //$NON-NLS-1$
        if (imageSrcPath != null) {
            if (element != null) {
                try {
                    String uri = JavaElementLinks.createURI(JavaElementLinks.OPEN_LINK_SCHEME, element);
                    buf.append("<a href='").append(uri).append("'>"); //$NON-NLS-1$//$NON-NLS-2$
                } catch (URISyntaxException e) {
                    element = null; // no link
                }
            }
            StringBuffer imageStyle = new StringBuffer("border:none; position: absolute; "); //$NON-NLS-1$
            imageStyle.append("width: ").append(imageWidth).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
            imageStyle.append("height: ").append(imageHeight).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
            imageStyle.append("left: ").append(-labelLeft - 1).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$

            // hack for broken transparent PNG support in IE 6, see
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=223900 :
            buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); //$NON-NLS-1$
            String tooltip = element == null ? "" //$NON-NLS-1$
                    : "alt='" + ComposerTestcaseMessageConstants.JavadocHover_openDeclaration + "' "; //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("<span ").append(tooltip).append("style=\"").append(imageStyle). //$NON-NLS-1$ //$NON-NLS-2$
                    append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='").append(imageSrcPath) //$NON-NLS-1$
                    .append("')\"></span>\n"); //$NON-NLS-1$
            buf.append("<![endif]><![endif]-->\n"); //$NON-NLS-1$

            buf.append("<!--[if !IE]>-->\n"); //$NON-NLS-1$
            buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    .append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$
            buf.append("<!--<![endif]-->\n"); //$NON-NLS-1$
            buf.append("<!--[if gte IE 7]>\n"); //$NON-NLS-1$
            buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    .append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$
            buf.append("<![endif]-->\n"); //$NON-NLS-1$
            if (element != null) {
                buf.append("</a>"); //$NON-NLS-1$
            }
        }

        buf.append(label);

        buf.append("</div>"); //$NON-NLS-1$
        return buf.toString();
    }

    private String getImageURL(IJavaElement element) {
        String imageName = null;
        URL imageUrl = JavaPlugin.getDefault().getImagesOnFSRegistry().getImageURL(element);
        if (imageUrl != null) {
            imageName = imageUrl.toExternalForm();
        }

        return imageName;
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
    
    @Override
    public synchronized void hide() {
        if (iControl != null) {
            iControl.dispose();
        }
        if (tip != null && !tip.isDisposed()) {
            tip.dispose();
        }
    }   
}
