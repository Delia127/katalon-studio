package com.kms.katalon.composer.components.impl.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class Dropdown {

    private Shell shell;

    private static int style = SWT.NO_TRIM;

    private Composite container;

    private List<DropdownGroup> groups;
    
    private Listener eventListener;

    public Dropdown(Display display) {
        shell = new Shell(display, style);
        initialDropdown();
    }

    public Dropdown(Shell parent) {
        shell = new Shell(parent, style);
        initialDropdown();
    }

    private void initialDropdown() {
        GridLayout shellLayout = new GridLayout();
        shellLayout.marginWidth = 0;
        shellLayout.marginHeight = 0;
        shellLayout.horizontalSpacing = 0;
        shellLayout.verticalSpacing = 0;
        shell.setLayout(shellLayout);

        container = new Composite(shell, SWT.BORDER);
        GridLayout containerLayout = new GridLayout();
        containerLayout.marginHeight = 0;
        containerLayout.horizontalSpacing = 0;
        containerLayout.verticalSpacing = 0;
        container.setLayout(containerLayout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        groups = new ArrayList<>();

        shell.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                shell.setVisible(false);
            }
        });
        
        eventListener = new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                Widget widget = event.widget;
                if (widget instanceof Shell && widget != shell && shell.isVisible()) {
                    shell.setVisible(false);
                }
            }
        };

        shell.getDisplay().addFilter(SWT.Activate, eventListener);

        shell.addDisposeListener(new DisposeListener() {
            
            @Override
            public void widgetDisposed(DisposeEvent e) {
                shell.getDisplay().removeFilter(SWT.Activate, eventListener);
            }
        });
    }

    public DropdownGroup addDropdownGroupItem(String groupLabel, Image groupImage) {
        DropdownGroup dropdownGroup = new DropdownGroup(container, groupLabel, groupImage);
        groups.add(dropdownGroup);
        return dropdownGroup;
    }

    public DropdownGroup getItem(int index) {
        return groups.get(index);
    }

    public List<DropdownGroup> getItems() {
        return groups;
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }
    
    public void dispose() {
        shell.dispose();
    }

    public boolean isVisible() {
        return shell.isVisible();
    }

    public void setVisible(boolean isVisible) {
        shell.setVisible(isVisible);
        if (isVisible) {
            shell.setActive();
            shell.forceFocus();
        }
    }

    public void setLocation(int x, int y) {
        shell.setLocation(x, y);
    }

    public void resizeToFitContent() {
        shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
    }

    public int getWidth() {
        // ensure real content width will be gotten
        resizeToFitContent();
        return shell.getBounds().width;
    }

}
