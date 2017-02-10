package com.kms.katalon.composer.components.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class HelpCompositeForDialog extends HelpComposite {

    public HelpCompositeForDialog(Composite parent, String documentationUrl) {
        super(parent, documentationUrl);
    }
    
    protected GridData createGridData() {
        return new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
    }

    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginBottom = 5;
        layout.marginWidth = 5;
        return layout;
    }

}
