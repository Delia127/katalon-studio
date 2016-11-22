package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public abstract class DropdownItemSelectionListener extends SelectionAdapter {

    Dropdown dropdown;

    public DropdownItemSelectionListener(Dropdown dropdown) {
        this.dropdown = dropdown;
    }

    @Override
    public final void widgetSelected(SelectionEvent event) {
        switch (event.detail) {
            case SWT.ARROW:
                arrowSelected(event);
                break;

            default:
                itemSelected(event);
                break;
        }
    }

    protected final void arrowSelected(SelectionEvent event) {
        Widget widget = event.widget;
        if (!(widget instanceof ToolItem)) {
            return;
        }

        ToolItem item = (ToolItem) widget;
        Rectangle rect = item.getBounds();
        Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
        if (dropdown == null || dropdown.isDisposed()) {
            return;
        }
        int dropdownWidth = dropdown.getWidth();
        if (rect.width > dropdownWidth) {
            pt.x = pt.x + rect.width - dropdownWidth;
        }
        dropdown.setLocation(pt.x, pt.y + rect.height);
        dropdown.setVisible(!dropdown.isVisible());
    }

    public void itemSelected(SelectionEvent event) {
        arrowSelected(event);
    }
}
