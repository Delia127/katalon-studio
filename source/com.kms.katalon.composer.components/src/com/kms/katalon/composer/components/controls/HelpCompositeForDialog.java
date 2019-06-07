package com.kms.katalon.composer.components.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.util.ColorUtil;

public class HelpCompositeForDialog extends HelpComposite {

    public HelpCompositeForDialog(Composite parent, String documentationUrl) {
        super(parent, documentationUrl);
        setBackground(ColorUtil.getCompositeBackgroundColorForDialog());
    }

    @Override
    protected GridData createGridData() {
        return new GridData(SWT.LEFT, SWT.CENTER, true, false);
    }

    @Override
    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginBottom = 5;
        return layout;
    }

}
