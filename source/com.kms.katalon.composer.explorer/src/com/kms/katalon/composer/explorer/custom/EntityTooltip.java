package com.kms.katalon.composer.explorer.custom;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.components.impl.control.CustomColumnViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.composer.components.util.ColorUtil;

public class EntityTooltip extends ColumnViewerToolTipSupport {

    private CustomColumnViewer viewer;

    /**
     * @see {@link ColumnViewerToolTipSupport#VIEWER_CELL_KEY}
     */
    private static final String VIEWER_CELL_KEY = Policy.JFACE + "_VIEWER_CELL_KEY"; //$NON-NLS-1$

    private EntityTooltip(CustomColumnViewer viewer) {
        super((ColumnViewer) viewer, ToolTip.RECREATE, false);
        this.viewer = viewer;
    }

    public static EntityTooltip createFor(CustomColumnViewer viewer) {
        return new EntityTooltip(viewer);
    }

    /**
     * @wbp.parser.entryPoint
     */
    protected Composite createViewerToolTipContentArea(Event event, ViewerCell cell, Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        result.setBackground(getBackgroundColor(event));
        result.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout glResult = new GridLayout();
        glResult.marginHeight = 0;
        glResult.marginWidth = 0;
        glResult.verticalSpacing = 10;
        result.setLayout(glResult);

        Composite propertiesComposite = new Composite(result, SWT.NONE);
        propertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glProperties = new GridLayout(2, false);
        glProperties.horizontalSpacing = 10;
        propertiesComposite.setLayout(glProperties);

        createPropertiesControls(propertiesComposite, cell);

        Composite bottomComposite = new Composite(result, SWT.NONE);
        GridLayout glBottom = new GridLayout();
        glBottom.verticalSpacing = 0;
        glBottom.marginHeight = 0;
        glBottom.marginWidth = 0;
        glBottom.marginBottom = 2;
        bottomComposite.setLayout(glBottom);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        Label separator = new Label(bottomComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        StyledText txtSuggestion = new StyledText(bottomComposite, SWT.WRAP);
        txtSuggestion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        String message = StringConstants.TOOLTIP_MESSAGE_PROPERTIES_ENTITY;
        txtSuggestion.setText(message);
        txtSuggestion.setForeground(ColorUtil.getTooltipPlaceHolderForegroundColor());
        StyleRange sr = new StyleRange(message.indexOf(StringConstants.PROPERTIES),
                StringConstants.PROPERTIES.length(), null, null);
        sr.fontStyle = SWT.BOLD | SWT.ITALIC;
        txtSuggestion.setStyleRange(sr);
        txtSuggestion.setLineAlignment(0, 1, SWT.CENTER);

        return result;
    }

    private ITreeEntity getTreeEntity(ViewerCell cell) {
        return (ITreeEntity) viewer.getViewerRowFromWidgetItem(cell.getItem()).getElement();
    }

    private void createPropertiesControls(Composite result, ViewerCell cell) {
        ITreeEntity treeEntity = getTreeEntity(cell);
        for (TooltipPropertyDescription property : treeEntity.getTooltipDescriptions()) {
            Label lblPropKey = new Label(result, SWT.NONE);
            lblPropKey.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
            lblPropKey.setText(property.getKey() + ":");
            ControlUtils.setFontToBeBold(lblPropKey);

            StyledText txtPropValue = new StyledText(result, SWT.WRAP);
            GridData gdTxtPropertyValue = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
            gdTxtPropertyValue.widthHint = 250;
            txtPropValue.setLayoutData(gdTxtPropertyValue);

            String value = property.getValue();
            String valueText = property.isLengthLimited() ? StringUtils.abbreviate(value, property.getMaxValueLength())
                    : value;
            txtPropValue.setText(valueText);
        }
    }

    @Override
    protected boolean shouldCreateToolTip(Event event) {
        if (!super.shouldCreateToolTip(event)) {
            return false;
        }
        ViewerCell cell = (ViewerCell) getData(VIEWER_CELL_KEY);
        if (cell != null) {
            ITreeEntity treeEntity = getTreeEntity(cell);
            List<TooltipPropertyDescription> tooltipDescriptions = treeEntity.getTooltipDescriptions();
            return tooltipDescriptions != null && !tooltipDescriptions.isEmpty();
        }
        return false;
    }
}
