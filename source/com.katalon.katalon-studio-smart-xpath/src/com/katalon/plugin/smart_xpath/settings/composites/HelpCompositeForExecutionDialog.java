package com.katalon.plugin.smart_xpath.settings.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.composer.components.util.ColorUtil;

public class HelpCompositeForExecutionDialog extends HelpComposite {

    public HelpCompositeForExecutionDialog(Composite parent, String documentationUrl) {
        super(parent, documentationUrl);
    }

    @Override
    protected GridData createGridData() {
        return new GridData(SWT.LEFT, SWT.CENTER, true, false);
    }

    @Override
    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }

}
