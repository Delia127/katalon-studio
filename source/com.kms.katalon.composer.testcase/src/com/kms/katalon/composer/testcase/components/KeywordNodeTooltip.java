package com.kms.katalon.composer.testcase.components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;

public class KeywordNodeTooltip {
    private static final String JAVADOC_SUFFIX = "[JAVADOC_";

    private static final String JAVADOC_HEADER = JAVADOC_SUFFIX + "HEADER]";

    private static final String JAVADOC_SECTION = JAVADOC_SUFFIX + "SECTION]";

    private static final String JAVADOC_SECTION_ITEM = JAVADOC_SUFFIX + "SECTION_ITEM]";

    private static final String JAVADOC_DESCRIPTION = JAVADOC_SUFFIX + "DESCRIPTION]";

    private static final String JAVADOC_SECTION_ITEM_LIST = JAVADOC_SUFFIX + "_SECTION_ITEM_LIST]";

    private StyledText javaDocContent;

    private String text = "";

    private int preferedWidth = 600;

    private int preferedHeight = 200;

    private final int TOOLBAR_DEFAULT_HEIGHT = 24;

    private Shell tip;

    private String keywordDescURI = null;

    private Control control;

    private ToolItem openKeywordDescToolItem;

    private ToolBar toolBar;

    private boolean showBelow = true;

    private Shell openKeywordDescTooltip;

    private boolean openedDesc;

    private boolean isOpeningKeywordDescription = false;

    private Point location;

    private static KeywordNodeTooltip currentTooltip = null;

    public KeywordNodeTooltip(Control control) {
        this.control = control;
    }

    public Shell getShell() {
        return tip;
    }

    private void initComponents(Composite parent) { 
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;

        parent.setBackgroundMode(SWT.INHERIT_FORCE);

        composite.setLayout(layout);
        composite.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        
        javaDocContent = new StyledText(composite, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        GridData gdJavaDocContent = new GridData(SWT.FILL, SWT.FILL, true, true);
        javaDocContent.setLeftMargin(20);
        javaDocContent.setTopMargin(5);
        javaDocContent.setLayoutData(gdJavaDocContent);

        Label lbl = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.verticalIndent = 0;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.END;
        lbl.setLayoutData(gd);

        toolBar = new ToolBar(composite, SWT.NONE);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.heightHint = TOOLBAR_DEFAULT_HEIGHT;
        gd.verticalAlignment = SWT.FILL;
        toolBar.setLayoutData(gd);
        toolBar.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        new ToolItem(toolBar, SWT.SEPARATOR);
        openKeywordDescToolItem = new ToolItem(toolBar, SWT.NONE);
        openKeywordDescToolItem.setImage(ImageConstants.IMG_KEYWORD_WIKI);

        Listener listener = new Listener() {

            @Override
            public void handleEvent(Event event) {
                switch (event.type) {
                    case SWT.Selection:
                        openKeywordDesc();
                        break;
                    case SWT.MouseExit:
                        if (!tip.getBounds().contains(Display.getCurrent().getCursorLocation())) {
                            hide();
                        }
                        if (event.widget == toolBar) {
                            processOpenKeywordTooltip();
                        }
                        break;
                    case SWT.MouseHover:
                        showOpenKeywordTooltip();
                        break;
                    case SWT.MouseMove:
                        processOpenKeywordTooltip();
                        break;
                }

            }
        };
        if (control instanceof Tree) {
            javaDocContent.addListener(SWT.MouseExit, listener);
            toolBar.addListener(SWT.MouseExit, listener);
        }
        toolBar.addListener(SWT.MouseHover, listener);
        toolBar.addListener(SWT.MouseMove, listener);
        openKeywordDescToolItem.addListener(SWT.Selection, listener);
        toolBar.pack();

        formatJavaDoc();
    }

    private void showOpenKeywordTooltip() {
        if (openKeywordDescTooltip != null && !openKeywordDescTooltip.isDisposed()) {
            return;
        }
        Point cursorLoc = Display.getCurrent().getCursorLocation();
        if (isCursorOnOpenKeywordDescButton(cursorLoc)) {
            openKeywordDescTooltip = new Shell(tip, SWT.NONE);
            FillLayout fl = new FillLayout();
            openKeywordDescTooltip.setLayout(fl);
            Point[] cursorSize = Display.getCurrent().getCursorSizes();
            Label lbl = new Label(openKeywordDescTooltip, SWT.None);
            lbl.setBackground(toolBar.getBackground());
            lbl.setText(ComposerTestcaseMessageConstants.KEYWORD_TOOLITEM_TIP_TEXT);
            int shift = Platform.WS_WIN32.equals(Platform.getOS()) ? 10 : 0;
            openKeywordDescTooltip.pack();
            openKeywordDescTooltip.setLocation(new Point(cursorLoc.x, cursorLoc.y + cursorSize[0].y - shift));
            openKeywordDescTooltip.setVisible(true);
        }
    }

    private void processOpenKeywordTooltip() {
        if (openKeywordDescTooltip == null || openKeywordDescToolItem.isDisposed()) {
            return;
        }
        Point cursorLoc = Display.getCurrent().getCursorLocation();
        if (!isCursorOnOpenKeywordDescButton(cursorLoc)) {
            openKeywordDescTooltip.dispose();
        } else {
            showOpenKeywordTooltip();
        }
    }

    private void createTooltip() {
        tip = new Shell(control.getShell(), SWT.ON_TOP | SWT.TOOL | SWT.RESIZE);
        tip.setLayout(new FillLayout());
        initComponents(tip);
    }

    public boolean isShowBelowPoint() {
        return showBelow;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setPreferedSize(int w, int h) {
        if (w > 0) {
            preferedWidth = w;
        }
        if (h > 0) {
            preferedHeight = h;
        }
    }

    public void show(Point p) {
        hide();
        location = p;
        createTooltip();
        tip.setLocation(p);
             
        Point tipSize = getBestSizeForKeywordDescriptionPopup();
        tip.setSize(tipSize);
        
        if (currentTooltip != null && currentTooltip != this) {
            currentTooltip.hide();
        }
        currentTooltip = this;
        openedDesc = false;
        tip.setVisible(true);
    }

    private Point getBestSizeForKeywordDescriptionPopup() {
        Monitor currentMonitor = null;
        for (Monitor monitor : Display.getCurrent().getMonitors()) {
            if (monitor.getClientArea().contains(location)) {
                currentMonitor = monitor;
                break;
            }
        }
        Rectangle displayRect = currentMonitor.getClientArea();
        int width = preferedWidth;
        if (location.x + width > displayRect.x + displayRect.width ) {
            width = displayRect.x + displayRect.width - location.x;
        }
        return new Point(width, preferedHeight);
    }
    
    private Point getLocation(Point suggestionLoc) {
        Rectangle bounds = Display.getCurrent().getBounds();
        Point tipSize = tip.getSize();
        showBelow = true;

        if (suggestionLoc.x + tipSize.x < bounds.width && suggestionLoc.y + tipSize.y < bounds.height) {
            return suggestionLoc;
        }
        if (suggestionLoc.x + tipSize.x > bounds.width) {
            suggestionLoc.x -= tipSize.x;
        }
        if (suggestionLoc.y + tipSize.y > bounds.height) {
            showBelow = false;
            suggestionLoc.y -= tipSize.y;
        }

        return suggestionLoc;
    }

    public synchronized void hide() {
        if (tip != null && !tip.isDisposed()) {
            Point cursorLoc = Display.getCurrent().getCursorLocation();
            if (isOpenKeywordDescToolItem(cursorLoc) && !isOpeningKeywordDescription()) {
                openKeywordDesc();
            } else {
                tip.dispose();
            }
            currentTooltip = null;
        }
    }

    public boolean isVisible() {
        return tip != null && !tip.isDisposed() && tip.isVisible();
    }

    public void setKeywordURL(String keywordDescURI) {
        this.keywordDescURI = keywordDescURI;
    }

    public boolean isOpenKeywordDescToolItem(Point screenPoint) {
        try {
            if (openKeywordDescToolItem == null) {
                return false;
            }
            return isCursorOnOpenKeywordDescButton(screenPoint);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isCursorOnOpenKeywordDescButton(Point cursorPoint) {
        Rectangle bounds = openKeywordDescToolItem.getBounds();
        Point screenLocation = toolBar.toDisplay(bounds.x, bounds.y);

        return new Rectangle(screenLocation.x, screenLocation.y, bounds.width, bounds.height).contains(cursorPoint);
    }

    public void openKeywordDesc() {
        setIsOpeingKeywordDescription(true);
        try {
            Program.launch(keywordDescURI);
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        } finally {
            openedDesc = true;
            if (isVisible()) {
                tip.dispose();
            }
            setIsOpeingKeywordDescription(false);
        }
    }

    public Rectangle getBounds() {
        return tip.getBounds();
    }

    private void formatJavaDoc() {
        text = text.replaceAll("<h4>", JAVADOC_HEADER)
                .replaceAll("<DT><B>", JAVADOC_SECTION)
                .replaceAll("(<DD>|</p>)\\s*", JAVADOC_SECTION_ITEM)
                .replaceAll("<p>\\s*", JAVADOC_DESCRIPTION)
                .replaceAll("<li>", JAVADOC_SECTION_ITEM_LIST)
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("<[^>]+>", "")
                .replaceAll(System.lineSeparator(), "")
                .trim();
        StringBuilder tipContent = new StringBuilder(text);
        StyleRange sr;
        Pattern pat = Pattern.compile("\\" + JAVADOC_SUFFIX + "\\w+\\]");
        Matcher mat = pat.matcher(tipContent);
        List<StyleRange> styles = new ArrayList<>();
        boolean descriptionExsist = false;

        while (mat.find()) {
            int start = mat.start(), i = mat.end();
            int end = mat.find() ? mat.start() : tipContent.length();
            String item = tipContent.substring(start, i);
            switch (item) {
                case JAVADOC_HEADER:
                    tipContent.delete(start, start + item.length());
                    sr = new StyleRange(start, end - start - item.length(), null, null);
                    sr.fontStyle = SWT.BOLD | SWT.ITALIC;
                    styles.add(sr);
                    break;
                case JAVADOC_SECTION:
                    tipContent.delete(start, start + item.length()).insert(start, System.lineSeparator());
                    javaDocContent.setText(tipContent.toString());
                    sr = new StyleRange(start + 1, end - start - item.length() + 1, null, null);
                    sr.fontStyle = SWT.BOLD;
                    styles.add(sr);
                    break;
                case JAVADOC_DESCRIPTION:
                    if (!descriptionExsist) {
                        tipContent.delete(start, start + item.length()).insert(start, System.lineSeparator());
                        descriptionExsist = true;
                        break;
                    }
                case JAVADOC_SECTION_ITEM:
                    tipContent.delete(start, start + item.length());
                    String line = wrapSelectionItemLongLine(tipContent.substring(start, end - item.length()));
                    tipContent.delete(start, end - item.length());
                    tipContent.insert(start, System.lineSeparator() + line);
                    break;
                case JAVADOC_SECTION_ITEM_LIST:
                    tipContent.delete(start, start + item.length()).insert(start,
                            System.lineSeparator() + "\t\t " + Character.toString('\u25CF') + " ");
                    break;
            }
            mat = pat.matcher(tipContent);
        }
        text = tipContent.toString();
        javaDocContent.setText(text);
        javaDocContent.setStyleRanges(styles.toArray(new StyleRange[] {}));
    }

    private String wrapSelectionItemLongLine(String line) {
        GC graphicContext = new GC(javaDocContent);
        int limWidth = 600;
        
        String[] words = line.split("\\s{1,}");
        StringBuilder temp = new StringBuilder("\t\t");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; ++i) {
            if (!appendWord(words[i], temp, graphicContext, limWidth)) {
                if (result.length() < 1) {
                    result.append(temp.toString());
                } else {
                    result.append(System.lineSeparator() + temp.toString());
                }
                temp.delete(2, temp.length());
            }
        }

        if (temp.length() > 2) {
            if (result.length() < 1) {
                result.append(temp.toString());
            } else {
                result.append(System.lineSeparator() + temp.toString());
            }
        }
        
        graphicContext.dispose();
        return result.toString();
    }

    private boolean appendWord(String word, StringBuilder line, GC graphicContext, int limWidth) {
        int lineWidth = graphicContext.textExtent(line.toString(), SWT.DRAW_TAB).x;
        if (lineWidth >= limWidth) {
            return false;
        }
        int w = graphicContext.textExtent(" " + word).x;
        if (lineWidth + w < limWidth) {
            line.append(" " + word);
            return true;
        }
        w = graphicContext.textExtent(word).x;
        if (lineWidth + w < limWidth) {
            line.append(word);
            return true;
        }
        return false;
    }

    public boolean isOpenedKeywordDesc() {
        return openedDesc;
    }

    private synchronized void setIsOpeingKeywordDescription(boolean isOpeningKeywordDescription) {
        this.isOpeningKeywordDescription = isOpeningKeywordDescription;
    }

    private synchronized boolean isOpeningKeywordDescription() {
        return isOpeningKeywordDescription;
    }

}
