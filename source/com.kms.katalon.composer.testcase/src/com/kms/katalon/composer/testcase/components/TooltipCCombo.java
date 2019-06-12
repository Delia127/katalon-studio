package com.kms.katalon.composer.testcase.components;

/*******************************************************************************
 * Copyright (c) 2010 Compart AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Compart AG - Stefan Weber - Initial Release
 *******************************************************************************/

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.util.KeywordURLUtil;

/**
 * The CCombo class represents a selectable user interface object
 * that combines a text field and a list and issues notification
 * when an item is selected from the list.
 * <p>
 * CCombo was written to work around certain limitations in the native combo box. Specifically, on win32, the height of
 * a CCombo can be set; attempts to set the height of a Combo are ignored. CCombo can be used anywhere that having the
 * increased flexibility is more important than getting native L&F, but the decision should not be taken lightly. There
 * is no is no strict requirement that CCombo look or behave the same as the native combo box.
 * </p>
 * <p>
 * <b>This class support tooltips for the combo items. See {@link #add(String, String)} and
 * {@link #setToolTip(String, String)} </b>
 * </p>
 * 
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it does not make sense to add children to it,
 * or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
 * </dl>
 * 
 * @author Stefan Weber
 * @see <a href="http://www.eclipse.org/swt/snippets/#ccombo">CCombo snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: CustomControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 */
public class TooltipCCombo extends CCombo {

    private static final String FIELD_LIST = "list"; //$NON-NLS-1$

    private static final String FIELD_POPUP = "popup"; //$NON-NLS-1$

    private final java.util.List<String> tooltips = new ArrayList<String>();
    
    private Shell popup;
    
    private String classKeywordName = "";

    /**
     * Constructs a new instance of this class given its parent
     * and a style value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
     * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
     * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
     * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     *
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     *
     * @exception IllegalArgumentException <ul>
     * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @exception SWTException <ul>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     * </ul>
     *
     * @see SWT#BORDER
     * @see SWT#READ_ONLY
     * @see SWT#FLAT
     * @see Widget#getStyle()
     * @see CCombo#CCombo(Composite, int)
     */
    public TooltipCCombo(Composite parent, int style) {
        super(parent, style);
        
        if (parent instanceof Tree) {
            Tree tree = (Tree)parent;
            Object node = tree.getSelection()[0].getData();
            if (node instanceof AstBuiltInKeywordTreeTableNode) {
                classKeywordName = ((AstBuiltInKeywordTreeTableNode)node).getBuiltInKWClassSimpleName();
            }
        }
        
        try {
            // get the list from the combo
            Field listField = CCombo.class.getDeclaredField(FIELD_LIST);
            listField.setAccessible(true);
            final List list = (List) listField.get(this);
            // get the popup from the combo
            Field popupField = CCombo.class.getDeclaredField(FIELD_POPUP);
            popupField.setAccessible(true);
//            final Shell popup = (Shell) popupField.get(this);
            popup = (Shell) popupField.get(this);

            // register the popup listener
            ActivationListener activationListener = new ActivationListener(list, this.tooltips);
            popup.addListener(SWT.Paint, activationListener);
            popup.addListener(SWT.Close, activationListener);
            popup.addListener(SWT.Deactivate, activationListener);
            this.addListener(SWT.FocusOut, activationListener);

        } catch (Exception e) {
            throw new UnsupportedOperationException("Reflections of this JVM can't be used " + //$NON-NLS-1$
                    "to access private fields. Thus this class won't work with your JVM.", e); //$NON-NLS-1$
        }
    }
    
    public String getKeywordClassName() {
        return classKeywordName;
    }

    @Override
    public void add(String string) {
        super.add(string);
        this.tooltips.add(null);
    }

    @Override
    public void add(String string, int index) {
        super.add(string, index);
        this.tooltips.add(index, null);
    }

    /**
     * Adds a new element with its tooltip to this combo
     * 
     * @param element the element to add
     * @param tooltip the tooltip for the element
     */
    public void add(String element, String tooltip) {
        Assert.isNotNull(tooltip, "Tooltip must not be null!"); //$NON-NLS-1$
        super.add(element);
        this.tooltips.add(tooltip);
    }

    /**
     * Adds a new item with its tooltip at the given index to the list
     * 
     * @param string the new item
     * @param index the index for the item
     * @param tooltip the tooltip for the new item
     */
    public void add(String string, int index, String tooltip) {
        Assert.isNotNull(tooltip, "Tooltip must not be null!"); //$NON-NLS-1$
        super.add(string, index);
        this.tooltips.add(index, tooltip);
    }

    /**
     * Sets the tooltip to for the element at the given zero-relative index. <br>
     * <br>
     * Note: you can only set a tooltip to an already inserted element.
     * 
     * @param index the index of the element to add the tooltip to
     * @param tooltip the tooltip
     */
    public void setToolTip(int index, String tooltip) {
        Assert.isNotNull(tooltip, "Tooltip must not be null!"); //$NON-NLS-1$
        if (0 <= index && index < getItemCount()) {
            throw new IndexOutOfBoundsException("The index must be  in " + //$NON-NLS-1$
                    "range 0 <= index && index < getItemCount()"); //$NON-NLS-1$
        }
        this.tooltips.set(index, tooltip);
    }

    /**
     * Sets the tooltip for an element. If the element is not contained in the
     * combo, nothing will be changed!
     * 
     * @param element the element for which the tooltip should be set
     * @param tooltip the tooltip
     */
    public void setToolTip(String element, String tooltip) {
        Assert.isNotNull(element, "Element must not be null!"); //$NON-NLS-1$
        Assert.isNotNull(tooltip, "Tooltip must not be null!"); //$NON-NLS-1$
        int idx = indexOf(element);
        if (idx != -1) {
            this.tooltips.set(idx, tooltip);
        }
    }

    /**
     * Returns the tooltip for the given element, or <code>null</code> if there is none
     * 
     * @param element the element the tooltip is associated to
     * @return the tooltip
     * @see #getToolTip(int)
     */
    public String getToolTip(String element) {
        int idx = indexOf(element);
        if (idx != -1) {
            return this.tooltips.get(idx);
        }
        return null;
    }

    /**
     * Returns the tooltip for the element associated with the given index,
     * or <code>null</code> if there is none
     * 
     * @param index the index of the element associated with the tooltip
     * @return the tooltip
     * @see #getToolTip(String)
     * @see #getItem(int)
     */
    public String getToolTip(int index) {
        return this.tooltips.get(index);
    }

    @Override
    public void remove(int start, int end) {
        int count = this.getItemCount();
        if (!(0 <= start && start <= end && end < count)) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        for (int i = start; i <= end; i++) {
            this.tooltips.remove(this.getItem(i));
        }
        super.remove(start, end);
    }

    @Override
    public void remove(String string) {
        this.tooltips.remove(string);
        super.remove(string);
    }

    @Override
    public void remove(int index) {
        this.tooltips.remove(this.getItem(index));
        super.remove(index);
    }

    @Override
    public void removeAll() {
        this.tooltips.clear();
        super.removeAll();
    }

    /**
     * Method to fake this class as valid subclass of button
     */
    protected void checkSubclass() {
    }

    private class ActivationListener implements Listener {
        private List list;

        private java.util.List<String> textLookup;

        private TooltipHandler handler;

        public ActivationListener(List list, java.util.List<String> textLookup) {
            this.list = list;
            this.textLookup = textLookup;
        }

        @Override
        public void handleEvent(Event event) {
            switch (event.type) {
                case SWT.Paint:
                    this.handler = new TooltipHandler(this.list, this.textLookup);
                    break;
                case SWT.Close:
                case SWT.FocusOut:
                case SWT.MouseExit:
                case SWT.Deactivate:
                    if (this.handler != null) {
                        this.handler.dispose();
                        this.handler = null;
                    }
                    break;
            }
        }
    }

    private class TooltipHandler implements MouseMoveListener, SelectionListener, MouseTrackListener {
        private int previousSelectionIdx = -1;

        private KeywordNodeTooltip tooltip = null;

        private List list;

        private java.util.List<String> textLookup;
        
        private int itemHeight;

        public TooltipHandler(List list, java.util.List<String> textLookup) {
            this.list = list;
            this.list.addMouseMoveListener(this);
            this.list.addSelectionListener(this);
            this.list.addMouseTrackListener(this);
            
            this.textLookup = textLookup;
            this.tooltip = new KeywordNodeTooltip(list);
            // show tooltip if currently an item is selected
            if (this.list.getSelectionIndex() != -1) {
                updateTooltip(this.list.getSelectionIndex());
            }
            itemHeight = list.getItemHeight();
            if (Platform.getOS().equals(Platform.OS_MACOSX)) {
                GC graphicContext = new GC(list);
                FontMetrics fm = graphicContext.getFontMetrics();
                itemHeight = fm.getHeight() + 2;
            }
        }
        
        @Override
        public void widgetSelected(SelectionEvent e) {
            updateTooltip(this.list.getSelectionIndex());
        }
        
        private int getItemIndexFormMousePos(int x, int y) {
            if (Platform.getOS().equals(Platform.OS_MACOSX)) {
                return y / itemHeight;
            }
            
            int hoverItem = y / itemHeight;
            return this.list.getTopIndex() + hoverItem;
        }

        @Override
        public void mouseMove(MouseEvent e) {
            // calculate the idx
            int itemIdx = getItemIndexFormMousePos(e.x, e.y);
            if (this.previousSelectionIdx != itemIdx) {
                updateTooltip(itemIdx);
            }
        }

        /**
         * Updates the tooltip text and location
         * 
         * @param id the item which is currently elected/hovered
         */
        private void updateTooltip(int index) {
            if (Platform.OS_LINUX.equals(Platform.getOS())) {
                return;
            }
            if (index < 0 || index >= textLookup.size()) {
                this.tooltip.hide();
                previousSelectionIdx = -1;
                return;
            }
            String text = this.textLookup.get(index);
            if (list.isVisible() && index >= 0 && index < this.list.getItemCount() && StringUtils.isNotEmpty(classKeywordName) && StringUtils.isNotEmpty(text)) {
                this.tooltip.setText(text);
                // calculate the location
                Point size = this.list.getSize();
                Point loc = this.list.getParent().getLocation();
                tooltip.setPreferedSize(600, size.y);
                this.tooltip.setKeywordURL(KeywordURLUtil.getKeywordDescriptionURI(classKeywordName, list.getItem(index)));
                this.tooltip.show(new Point(loc.x + size.x - 2, loc.y));
                this.previousSelectionIdx = index;
            } else {
                this.tooltip.hide();
                previousSelectionIdx = -1;
            }
        }

        /**
         * Disposes all controls in this listener
         */
        public void dispose() {
            this.list.removeMouseMoveListener(this);
            this.list.removeSelectionListener(this);
            this.list.removeMouseTrackListener(this);
            this.tooltip.hide();
        }
        
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void mouseExit(MouseEvent e) {
        }

        @Override
        public void mouseEnter(MouseEvent e) {
        }

        @Override
        public void mouseHover(MouseEvent e) {
        }

    }
}