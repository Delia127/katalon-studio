package com.kms.katalon.composer.components.controls;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.ToolBar;

public class ToolBarForMPart extends ToolBar {
    private static final String ELEMENT_ID_SUFFIX = "_toolbar";

    public ToolBarForMPart(MPart part) {
        super((CTabFolder) part.getParent().getWidget(), SWT.FLAT);
        MToolBar mToolbar = MMenuFactory.INSTANCE.createToolBar();
        mToolbar.setElementId(part.getElementId() + ELEMENT_ID_SUFFIX);
        mToolbar.setWidget(this);
        part.setToolbar(mToolbar);
    }

    @Override
    protected void checkSubclass() {
        // Override this to subclass
    }
}
