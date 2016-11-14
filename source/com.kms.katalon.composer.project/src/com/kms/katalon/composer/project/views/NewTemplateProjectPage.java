package com.kms.katalon.composer.project.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.composer.project.constants.StringConstants;

public class NewTemplateProjectPage extends ResizableProjectPage {

    private Composite container;

    private Table table;

    public NewTemplateProjectPage() {
        super(StringConstants.VIEW_TESTING_TYPES_PROJECT_PAGE_NAME);
        setTitle(StringConstants.VIEW_TITLE_NEW_PROJ);
        setDescription(StringConstants.VIEW_MSG_SPECIFY_TESTING_TYPES);
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        createItem(table, StringConstants.VIEW_LBL_WEB_TESTING, ImageConstants.WEB_ICON, true);
        createItem(table, StringConstants.VIEW_LBL_MOBILE_TESTING, ImageConstants.MOBILE_ICON, true);
        createItem(table, StringConstants.VIEW_LBL_API_TESTING, ImageConstants.API_ICON, true);

        addCheckListener(table);

        setControl(container);
        setPageComplete(true);
    }

    private void createItem(Table table, String label, Image icon, boolean isChecked) {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(label);
        item.setImage(icon);
        item.setChecked(isChecked);
    }

    private void addCheckListener(final Table table) {
        table.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                for (TableItem item : table.getItems()) {
                    if (item.getChecked()) {
                        setPageComplete(true);
                        return;
                    }
                }
                setPageComplete(false);
            }
        });
    }

    public List<String> getSelectedTemplates() {
        List<String> selectedOptions = new ArrayList<String>();
        for (int i = 0; i < table.getItems().length; i++) {
            if (table.getItems()[i].getChecked()) {
                selectedOptions.add(table.getItems()[i].getText());
            }
        }
        return selectedOptions;
    }

    @Override
    public Point getPageSize() {
        return new Point(550, 250);
    }
}
