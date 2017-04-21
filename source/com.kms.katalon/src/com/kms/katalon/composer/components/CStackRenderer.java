package com.kms.katalon.composer.components;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMruVisibleSWTHandler;
import org.eclipse.e4.ui.internal.workbench.OpaqueElementUtil;
import org.eclipse.e4.ui.internal.workbench.swt.CSSRenderingUtils;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.w3c.dom.css.CSSValue;

/**
 * This class overrides code from {@link StackRenderer} to fix the problem
 * https://incubation.kms-technology.com/browse/KAT-1952
 * 
 * This class should not be re-factored
 *
 */
@SuppressWarnings("restriction")
public class CStackRenderer extends StackRenderer {

    private static final String THE_PART_KEY = "thePart"; //$NON-NLS-1$

    boolean adjusting = false;

    private IPresentationEngine renderer;

    private IEclipsePreferences preferences;

    private Field removeRootField;

    public CStackRenderer(IPresentationEngine renderer, IEclipsePreferences preferences) {
        this.renderer = renderer;
        this.preferences = preferences;
    }

    /**
     * Determine whether the given view menu has any visible menu items.
     *
     * @param viewMenu
     * the view menu to check
     * @param part
     * the view menu's parent part
     * @return <tt>true</tt> if the specified view menu has visible children,
     * <tt>false</tt> otherwise
     */
    private boolean hasVisibleMenuItems(MMenu viewMenu, MPart part) {
        if (!viewMenu.isToBeRendered() || !viewMenu.isVisible()) {
            return false;
        }

        for (MMenuElement menuElement : viewMenu.getChildren()) {
            if (menuElement.isToBeRendered() && menuElement.isVisible()) {
                if (OpaqueElementUtil.isOpaqueMenuItem(menuElement)
                        || OpaqueElementUtil.isOpaqueMenuSeparator(menuElement)) {
                    IContributionItem item = (IContributionItem) OpaqueElementUtil.getOpaqueItem(menuElement);
                    if (item != null && item.isVisible()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        Object menuRenderer = viewMenu.getRenderer();
        if (menuRenderer instanceof MenuManagerRenderer) {
            MenuManager manager = ((MenuManagerRenderer) menuRenderer).getManager(viewMenu);
            if (manager != null && manager.isVisible()) {
                return true;
            }
        }

        Control control = (Control) part.getWidget();
        if (control != null) {
            Menu menu = (Menu) renderer.createGui(viewMenu, control.getShell(), part.getContext());
            if (menu != null) {
                menuRenderer = viewMenu.getRenderer();
                if (menuRenderer instanceof MenuManagerRenderer) {
                    MenuManagerRenderer menuManagerRenderer = (MenuManagerRenderer) menuRenderer;
                    MenuManager manager = menuManagerRenderer.getManager(viewMenu);
                    if (manager != null) {
                        // remark ourselves as dirty so that the menu will be
                        // reconstructed
                        manager.markDirty();
                    }
                }
                return menu.getItemCount() != 0;
            }
        }
        return false;
    }

    @Override
    public void adjustTopRight(final CTabFolder ctf) {
        if (adjusting)
            return;

        adjusting = true;

        try {
            // Gather the parameters...old part, new part...
            MPartStack stack = (MPartStack) ctf.getData(OWNING_ME);
            MUIElement element = stack.getSelectedElement();
            MPart curPart = (MPart) ctf.getTopRight().getData(THE_PART_KEY);
            MPart part = null;
            if (element != null) {
                part = (MPart) ((element instanceof MPart) ? element : ((MPlaceholder) element).getRef());
            }

            if (isPartBeingRemoved()) {
                return;
            }

            // Hide the old TB if we're changing
            if (part != curPart && curPart != null && curPart.getToolbar() != null) {
                curPart.getToolbar().setVisible(false);
            }

            Composite trComp = (Composite) ctf.getTopRight();
            Control[] kids = trComp.getChildren();

            boolean needsTB = part != null && part.getToolbar() != null && part.getToolbar().isToBeRendered();

            // View menu (if any)
            MMenu viewMenu = getViewMenu(part);
            boolean needsMenu = viewMenu != null && hasVisibleMenuItems(viewMenu, part);

            // Check the current state of the TB's
            ToolBar menuTB = (ToolBar) kids[kids.length - 1];

            // We need to modify the 'exclude' bit based on if the menuTB is
            // visible or not
            RowData rd = (RowData) menuTB.getLayoutData();
            if (needsMenu) {
                menuTB.getItem(0).setData(THE_PART_KEY, part);
                menuTB.moveBelow(null);
                menuTB.pack();
                rd.exclude = false;
                menuTB.setVisible(true);
            } else {
                menuTB.getItem(0).setData(THE_PART_KEY, null);
                rd.exclude = true;
                menuTB.setVisible(false);
            }

            ToolBar newViewTB = null;
            if (needsTB && part != null && part.getObject() != null) {
                part.getToolbar().setVisible(true);
                newViewTB = (ToolBar) renderer.createGui(part.getToolbar(), ctf.getTopRight(), part.getContext());
                // We can get calls during shutdown in which case the
                // rendering engine will return 'null' because you can't
                // render anything while a removeGui is taking place...
                if (newViewTB == null) {
                    adjusting = false;
                    return;
                }
                newViewTB.moveAbove(null);
                newViewTB.pack();
            }

            if (needsMenu || needsTB) {
                ctf.getTopRight().setData(THE_PART_KEY, part);
                ctf.getTopRight().pack(true);
                ctf.getTopRight().setVisible(true);
            } else {
                ctf.getTopRight().setData(THE_PART_KEY, null);
                ctf.getTopRight().setVisible(false);
            }

            // Pack the result
            trComp.pack();
        } finally {
            adjusting = false;
        }
        updateMRUValue(ctf);
    }

    /**
     * FIXME: Using reflection to overcome the bug of {@link StackRenderer} that will try to render
     * {@link MCompositePart} parts that are being removed (https://incubation.kms-technology.com/browse/KAT-1952)
     * 
     * Should try to fix this using better methods in the future
     * @return true if a part is being removed, otherwise false
     */
    private boolean isPartBeingRemoved() {
        try {
            MUIElement removingElement = (MUIElement) getRemoveRootField().get(renderer);
            if (removingElement != null) {
                return true;
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // ignore this
        }
        return false;
    }

    private Field getRemoveRootField() throws NoSuchFieldException {
        if (removeRootField == null) {
            removeRootField = renderer.getClass().getDeclaredField("removeRoot");
            removeRootField.setAccessible(true);
        }
        return removeRootField;
    }

    private void updateMRUValue(CTabFolder ctf) {
        boolean actualMRUValue = getMRUValue(ctf);
        ctf.setMRUVisible(actualMRUValue);
    }

    private boolean getMRUValue(Control control) {
        if (CSSPropertyMruVisibleSWTHandler.isMRUControlledByCSS()) {
            return getInitialMRUValue(control);
        }
        return getMRUValueFromPreferences();
    }

    private boolean getInitialMRUValue(Control control) {
        CSSRenderingUtils util = context.get(CSSRenderingUtils.class);
        if (util == null) {
            return getMRUValueFromPreferences();
        }

        CSSValue value = util.getCSSValue(control, "MPartStack", "swt-mru-visible"); //$NON-NLS-1$ //$NON-NLS-2$

        if (value == null) {
            value = util.getCSSValue(control, "MPartStack", "mru-visible"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (value == null) {
            return getMRUValueFromPreferences();
        }
        return Boolean.parseBoolean(value.getCssText());
    }

    private boolean getMRUValueFromPreferences() {
        boolean initialMRUValue = preferences.getBoolean(MRU_KEY_DEFAULT, MRU_DEFAULT);
        boolean actualValue = preferences.getBoolean(MRU_KEY, initialMRUValue);
        return actualValue;
    }
}
