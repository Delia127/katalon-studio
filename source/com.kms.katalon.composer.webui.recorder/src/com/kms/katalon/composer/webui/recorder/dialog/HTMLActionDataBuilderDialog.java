package com.kms.katalon.composer.webui.recorder.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction.HTMLActionParam;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.dialog.provider.HTMLActionParamLabelProvider;
import com.kms.katalon.composer.webui.recorder.dialog.provider.HTMLActionValueColumnSupport;
import com.kms.katalon.composer.webui.recorder.dialog.provider.HTMLActionValueTypeColumnSupport;
import com.kms.katalon.composer.webui.recorder.type.HTMLActionPropertyValueType;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.objectspy.element.WebElement;

public class HTMLActionDataBuilderDialog extends Dialog {
    private List<HTMLActionParamMapping> actionParamMappings;

    private Table table;

    private TableViewer tableViewer;

    private HTMLActionMapping actionParamMapping;

    public HTMLActionDataBuilderDialog(Shell parentShell, HTMLActionMapping actionParamMappingManager) {
        super(parentShell);
        this.actionParamMapping = actionParamMappingManager;

        createActionParamMappings(actionParamMappingManager);
    }

    private void createActionParamMappings(HTMLActionMapping actionParamMappingManager) {
        HTMLActionParam[] actionParams = actionParamMappingManager.getAction().getParams();
        HTMLActionParamValueType[] actionDatas = actionParamMappingManager.getData();
        this.actionParamMappings = new ArrayList<HTMLActionParamMapping>();
        for (int i = 0; i < actionParams.length; i++) {
            if (!TestObject.class.isAssignableFrom(actionParams[i].getClazz())
                    && !FailureHandling.class.isAssignableFrom(actionParams[i].getClazz())) {
                this.actionParamMappings.add(new HTMLActionParamMapping(actionParams[i], actionDatas[i].clone()));
            }
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = new Composite(container, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        ColumnViewerUtil.setTableActivation(tableViewer);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_PARAM_NAME, 100, 15, null);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_PARAM_TYPE, 100, 15, null);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_VALUE_TYPE, 150, 15,
                new HTMLActionValueTypeColumnSupport(tableViewer, getAditionalParamValueType()));
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_VALUE, 100, 55,
                new HTMLActionValueColumnSupport(tableViewer));

        tableViewer.setLabelProvider(new HTMLActionParamLabelProvider());

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(actionParamMappings);
        return container;
    }

    private HTMLActionPropertyValueType getAditionalParamValueType() {
        WebElement targetElement = actionParamMapping.getTargetElement();
        return (targetElement != null) ? new HTMLActionPropertyValueType(targetElement) : null;
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(),
                new ColumnWeightData(weight, tableColumn.getColumn().getWidth()));
    }

    public HTMLActionParamValueType[] getActionData() {
        HTMLActionParam[] actionParams = actionParamMapping.getAction().getParams();
        HTMLActionParamValueType[] actionDatas = new HTMLActionParamValueType[actionParams.length];
        for (int i = 0; i < actionParams.length; i++) {
            actionDatas[i] = actionParamMappings.get(i).getActionData();
        }
        return actionDatas;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_ACTION_DATA_LBL);
    }
}
