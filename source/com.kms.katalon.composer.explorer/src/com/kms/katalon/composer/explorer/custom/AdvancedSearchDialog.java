package com.kms.katalon.composer.explorer.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.extension.filter.impl.InternalFilterAction;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.constants.ImageConstants;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.controller.FilterController;

public class AdvancedSearchDialog extends Dialog {

    public static final int MIN_WIDTH = 500;

    private static final String DIALOG_TITLE = StringConstants.CUS_DIALOG_TITLE;

    private static final String SEARCH_LABEL = StringConstants.CUS_LBL_SEARCH;

    private static final String CLEAR_LABEL = StringConstants.CUS_LBL_CLEAR;

    private String[] searchTags;

    private Map<String, String> properties; // key: searchTag, value: input to search

    private Map<String, Text> textMap; // key: searchTag, text: where to place input

    private String txtInput;

    private Point location;

    public AdvancedSearchDialog(Shell parentShell, String txtInput, Point location) {
        super(parentShell);
        this.txtInput = txtInput;
        textMap = new LinkedHashMap<String, Text>();
        this.location = location;
        this.searchTags = FilterController.getInstance().getAllKeywords().toArray(new String[0]);
    }

    @SuppressWarnings("restriction")
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        try {
            if (searchTags != null) {
                properties = EntityViewerFilter.parseSearchedString(searchTags, txtInput);
                for (final String tag : searchTags) {
                    Label tagLabel = new Label(container, SWT.NONE);
                    tagLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
                    tagLabel.setText(StringUtils.capitalize(tag));

                    Text tagValue = new Text(container, SWT.BORDER);
                    tagValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
                    textMap.put(tag, tagValue);
                    tagValue.addModifyListener(new ModifyListener() {

                        @Override
                        public void modifyText(ModifyEvent e) {
                            properties.put(tag, ((Text) e.getSource()).getText());
                        }

                    });
                }
                refreshControls();
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(DIALOG_TITLE);
        newShell.setImage(ImageConstants.IMG_16_ADVANCED_SEARCH);
    }

    public String getOutput() {
        String output = "";
        for (Entry<String, String> entry : properties.entrySet()) {
            if (StringUtils.isNotEmpty(entry.getValue())) {
                output += entry.getKey() + "=(" + entry.getValue() + ") ";
            }
        }
        return output;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, SEARCH_LABEL, true);
        Button clear = createButton(parent, -1, CLEAR_LABEL, false);
        clear.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                clearOptions();
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    private void clearOptions() {
        this.txtInput = StringUtils.EMPTY;
        properties.clear();
        refreshControls();
    }

    private void refreshControls() {
        if (properties == null) {
            properties = new LinkedHashMap<String, String>();
        }
        for (Entry<String, Text> entry : textMap.entrySet()) {
            if (properties.containsKey(entry.getKey())) {
                entry.getValue().setText(properties.get(entry.getKey()));
            } else {
                properties.put(entry.getKey(), StringUtils.EMPTY);
                entry.getValue().setText(StringUtils.EMPTY);
            }
        }
    }

    @Override
    public Point getInitialSize() {
        Point preferSize = super.getInitialSize();
        return new Point(Math.max(preferSize.x, MIN_WIDTH), preferSize.y);
    }

    @Override
    public Point getInitialLocation(Point initialSize) {
        return new Point(this.location.x, this.location.y);

    }

}
