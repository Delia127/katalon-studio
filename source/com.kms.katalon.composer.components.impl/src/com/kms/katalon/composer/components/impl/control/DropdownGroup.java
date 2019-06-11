package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;

public class DropdownGroup {

    private Composite parent;

    private CLabel lblGroup;

    private Composite composite;

    public Composite getComposite() {
        return composite;
    }

    public DropdownGroup(Composite parent, String groupLabel, Image groupImage) {
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        this.parent = parent;

        lblGroup = new CLabel(composite, SWT.NONE);
        lblGroup.setText(groupLabel);
        lblGroup.setImage(groupImage);
        lblGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ControlUtils.setFontToBeBold(lblGroup);
    }

    public ToolItem addItem(String label, Image image, SelectionListener selectionListener) {
        ToolBar toolbar = new ToolBar(composite, SWT.FLAT | SWT.VERTICAL | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        GridData ldToolbar = new GridData(SWT.FILL, SWT.TOP, true, false);
        ldToolbar.horizontalIndent = 15;
        toolbar.setLayoutData(ldToolbar);

        ToolItem item = new ToolItem(toolbar, SWT.PUSH);
        item.setText(label);
        item.setImage(image);
        if (selectionListener == null) {
            return item;
        }
        item.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                super.widgetDefaultSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectionListener.widgetSelected(e);
                parent.getShell().setVisible(false);
            }
        });
        return item;
    }

    public Composite getParent() {
        return parent;
    }

    public CLabel getGroupLabel() {
        return lblGroup;
    }

}
