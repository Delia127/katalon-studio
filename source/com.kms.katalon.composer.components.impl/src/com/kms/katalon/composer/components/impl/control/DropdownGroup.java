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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.util.ControlUtils;

public class DropdownGroup {

    private Composite parent;

    private CLabel lblGroup;

    private ToolBar toolbar;

    private Composite composite;

    public Composite getComposite() {
        return composite;
    }

    public DropdownGroup(Composite parent, String groupLabel, Image groupImage) {
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
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

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(16, SWT.DEFAULT));

        toolbar = new ToolBar(composite, SWT.FLAT | SWT.VERTICAL | SWT.RIGHT);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    public ToolItem addItem(String label, Image image, SelectionListener selectionListener) {
        ToolItem item = new ToolItem(toolbar, SWT.PUSH);
        item.setText(label);
        item.setImage(image);
        if (selectionListener == null) {
            return item;
        }
        item.addSelectionListener(new SelectionAdapter() {

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

    public ToolItem getItem(int index) {
        return toolbar.getItem(index);
    }

    public ToolItem[] getItems() {
        return toolbar.getItems();
    }

    public ToolBar getToolBar() {
        return toolbar;
    }

    public CLabel getGroupLabel() {
        return lblGroup;
    }

}
