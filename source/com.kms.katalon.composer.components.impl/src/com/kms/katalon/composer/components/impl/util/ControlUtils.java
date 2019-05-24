package com.kms.katalon.composer.components.impl.util;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import com.kms.katalon.composer.components.ComponentBundleActivator;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.control.CMenu;
import com.kms.katalon.entity.file.FileEntity;

public class ControlUtils {
    private static final String UPDATING_LAYOUT = "updatingLayout";

    public static final int DF_CONTROL_HEIGHT = 18;

    public static final int DF_VERTICAL_SPACING = 10;

    public static final int DF_HORIZONTAL_SPACING = 10;

    private static final int DELAY_IN_MILLIS = 50;

    public static final int MENU_OPEN_ID = 100;

    private ControlUtils() {
        // Disable default constructor.
    }

    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
                c.setEnabled(enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }

    public static void recursivelyAddMouseListener(Control ctrl, MouseAdapter mouseAdapter) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursivelyAddMouseListener(c, mouseAdapter);
            }
        }
        ctrl.addMouseListener(mouseAdapter);
    }

    public static Font getFontBold(Control control) {
        return getFontStyle(control, SWT.BOLD);
    }

    public static Font getFontItalic(Control control) {
        return getFontStyle(control, SWT.ITALIC);
    }

    public static Font getFontBoldItalic(Control control) {
        return getFontStyle(control, SWT.BOLD | SWT.ITALIC);
    }

    /**
     * Get font of the control
     * 
     * @param control the control to get the font
     * @param style a bitwise combination of SWT.NORMAL, SWT.ITALIC and SWT.BOLD
     * @return Font
     */
    public static Font getFontStyle(Control control, int style) {
        return getFontStyle(control, style, -1);
    }

    /**
     * Get font of the control
     * 
     * @param control the control to get the font
     * @param style a bitwise combination of SWT.NORMAL, SWT.ITALIC and SWT.BOLD
     * @param size font size. Use -1 if don't want to change the size.
     * @return Font
     */
    public static Font getFontStyle(Control control, int style, int size) {
        FontDescriptor fontDescriptor = FontDescriptor.createFrom(control.getFont());
        if (size != -1) {
            return fontDescriptor.setStyle(style).setHeight(size).createFont(control.getDisplay());
        }
        return fontDescriptor.setStyle(style).createFont(control.getDisplay());
    }

    public static void setFontToBeBold(Control ctrl) {
        // ctrl.setFont(JFaceResources.getFontRegistry().getBold(""));
        ctrl.setFont(getFontBold(ctrl));
    }

    /**
     * Set font style to the control
     * 
     * @param control the control to set the font
     * @param style a bitwise combination of SWT.NORMAL, SWT.ITALIC and SWT.BOLD
     * @param size font size. Use -1 if don't want to change the size.
     */
    public static void setFontStyle(Control control, int style, int size) {
        control.setFont(getFontStyle(control, style, size));
    }

    public static void setFontSize(Control ctrl, int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Font's size must be a positive number");
        }
        FontData[] fD = ctrl.getFont().getFontData();
        fD[0].setHeight(height);
        ctrl.setFont(new Font(ctrl.getDisplay(), fD));
    }

    public static Listener getAutoHideStyledTextScrollbarListener = new Listener() {
        @Override
        public void handleEvent(final Event event) {
            final StyledText t = (StyledText) event.widget;
            final Rectangle r1 = t.getClientArea();
            final Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
            final Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            t.getDisplay().timerExec(DELAY_IN_MILLIS, new Runnable() {
                @Override
                public void run() {
                    if (t.isDisposed() || (Boolean.TRUE.equals(t.getData(UPDATING_LAYOUT)))) {
                        return;
                    }
                    t.setRedraw(false);
                    t.setData(UPDATING_LAYOUT, true);

                    try {
                        ScrollBar horizontalBar = t.getHorizontalBar();
                        if (horizontalBar != null) {
                            horizontalBar.setVisible(!t.getWordWrap() && r2.width < p.x);
                        }

                        ScrollBar verticalBar = t.getVerticalBar();
                        if (verticalBar != null) {
                            verticalBar.setVisible(r2.height < p.y);
                        }

                        if (event.type == SWT.Modify) {
                            updateParentLayout(t);
                            t.showSelection();
                        }
                    } finally {
                        t.setData(UPDATING_LAYOUT, false);
                        t.setRedraw(true);
                    }
                }
            });
        }
    };

    private static void updateParentLayout(Control ctrl) {
        Composite parentComposite = ctrl.getParent();
        if (parentComposite != null) {
            parentComposite.layout(true);
        }
    }

    public static void removeOldOpenMenuItem(Menu menu) {
        for (MenuItem item : menu.getItems()) {
            if (item.getID() == MENU_OPEN_ID) {
                item.dispose();
                return;
            }
        }
    }

    public static void createOpenMenuWhenSelectOnlyOne(CMenu menu, FileEntity entity,
            Callable<Boolean> enableWhenItemSelected, SelectionAdapter adapter) {
        if (entity == null) {
            return;
        }
        MenuItem openMenuItem = menu.createMenuItemWithoutSelectionListener(
                ComposerComponentsImplMessageConstants.MENU_OPEN, null, enableWhenItemSelected, SWT.PUSH);
        openMenuItem.setID(ControlUtils.MENU_OPEN_ID);
        openMenuItem.setText(getFileEntityMenuItemLabel(entity));
        openMenuItem.setData(entity);
        openMenuItem.addSelectionListener(adapter);
    }

    public static void createOpenMenuWhenSelectOnlyOne(final Menu menu, FileEntity entity, final TableViewer viewer,
            SelectionAdapter adapter) {
        if (entity == null) {
            return;
        }
        MenuItem openMenuItem = new MenuItem(menu, SWT.PUSH);
        openMenuItem.setText(getFileEntityMenuItemLabel(entity));
        openMenuItem.setID(ControlUtils.MENU_OPEN_ID);
        openMenuItem.setData(entity);
        viewer.getTable().addMenuDetectListener(new MenuDetectListener() {

            @Override
            public void menuDetected(MenuDetectEvent e) {
                menu.setEnabled(!viewer.getSelection().isEmpty());

            }
        });
        openMenuItem.addSelectionListener(adapter);
    }

    public static void createSubMenuOpen(Menu subMenu, FileEntity fileEntity, SelectionAdapter selectionAdapter,
            String name) {
        if (fileEntity == null) {
            return;
        }
        MenuItem menuItem = new MenuItem(subMenu, SWT.PUSH);
        menuItem.setText(name);
        menuItem.setData(fileEntity);
        menuItem.addSelectionListener(selectionAdapter);
    }

    private static String getFileEntityMenuItemLabel(FileEntity entity) {
        return ComposerComponentsImplMessageConstants.MENU_OPEN + " " + entity.getName();
    }

    public static boolean isReady(Control control) {
        return control != null && !control.isDisposed();
    }

    public static String createMenuItemText(String name, String hotkey) {
        if (StringUtils.isEmpty(hotkey)) {
            return name;
        }
        return name + "\t" + hotkey; //$NON-NLS-1$
    }

    public static boolean shouldLineVisble(Display display) {
    	return !ComponentBundleActivator.isDarkTheme(display);
    }
}
