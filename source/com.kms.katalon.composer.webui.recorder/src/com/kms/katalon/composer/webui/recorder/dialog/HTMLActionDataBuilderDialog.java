package com.kms.katalon.composer.webui.recorder.dialog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.editors.NumberCellEditor;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionDataType;
import com.kms.katalon.composer.webui.recorder.action.HTMLElementProperty;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction.HTMLActionParam;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;

public class HTMLActionDataBuilderDialog extends Dialog {
    private List<HTMLActionParamMapping> actionParamMappings;
    private HTMLActionParam[] actionParams;
    private Table table;
    private TableViewer tableViewer;
    private List<String> propertyNameList;

    private class HTMLActionParamMapping {
        private Object actionData;
        private HTMLActionParam actionParam;

        public HTMLActionParamMapping(HTMLActionParam actionParam, Object actionData) {
            this.setActionParam(actionParam);
            this.setActionData(actionData);
        }

        public Object getActionData() {
            return actionData;
        }

        public void setActionData(Object actionData) {
            this.actionData = actionData;
        }

        public HTMLActionParam getActionParam() {
            return actionParam;
        }

        public void setActionParam(HTMLActionParam actionParam) {
            this.actionParam = actionParam;
        }
    }

    public HTMLActionDataBuilderDialog(Shell parentShell, HTMLActionParam[] actionParams, Object[] actionDatas,
            List<String> propertyList) {
        super(parentShell);
        if (actionParams == null || actionDatas == null || actionParams.length != actionDatas.length) {
            throw new IllegalArgumentException();
        }
        this.actionParams = actionParams;
        this.actionParamMappings = new ArrayList<HTMLActionParamMapping>();
        for (int i = 0; i < actionParams.length; i++) {
            if (!TestObject.class.isAssignableFrom(actionParams[i].getClazz())
                    && !FailureHandling.class.isAssignableFrom(actionParams[i].getClazz())) {
                this.actionParamMappings.add(new HTMLActionParamMapping(actionParams[i], actionDatas[i]));
            }
        }
        this.propertyNameList = new ArrayList<String>(propertyList);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = new Composite(container, SWT.NONE);
        GridLayout gl_composite = new GridLayout(1, false);
        gl_composite.marginWidth = 0;
        gl_composite.marginHeight = 0;
        composite.setLayout(gl_composite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        ColumnViewerUtil.setTableActivation(tableViewer);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_PARAM_NAME, 100, 15, null);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_PARAM_TYPE, 100, 15, null);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_VALUE_TYPE, 100, 15,
                new EditingSupport(tableViewer) {
                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        return new ComboBoxCellEditor(tableViewer.getTable(), HTMLActionDataType.stringValues());
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        return element instanceof HTMLActionParamMapping;
                    }

                    @Override
                    protected Object getValue(Object element) {
                        HTMLActionDataType valueType = HTMLActionDataType.fromValue(((HTMLActionParamMapping) element)
                                .getActionData());
                        for (int i = 0; i < HTMLActionDataType.values().length; i++) {
                            if (HTMLActionDataType.values()[i] == valueType) {
                                return i;
                            }
                        }
                        return 0;
                    }

                    @Override
                    protected void setValue(Object element, Object value) {
                        if (!(value instanceof Integer)) {
                            return;
                        }
                        HTMLActionParamMapping actionParamMapping = (HTMLActionParamMapping) element;
                        HTMLActionDataType valueType = HTMLActionDataType.fromValue(((HTMLActionParamMapping) element)
                                .getActionData());
                        HTMLActionDataType newType = HTMLActionDataType.valueOf(HTMLActionDataType.stringValues()[(Integer) value]);
                        if (valueType == newType) {
                            // same value, so do nothing
                            return;
                        }
                        Object newActionData = newType.getDefaultValue();
                        if (newType == HTMLActionDataType.Property && !propertyNameList.isEmpty()) {
                            newActionData = new HTMLElementProperty(propertyNameList.get(0));
                        }
                        actionParamMapping.setActionData(newActionData);
                        tableViewer.refresh(actionParamMapping);
                    }
                });
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.COLUMN_DATA_VALUE, 100, 55, new EditingSupport(
                tableViewer) {

            @Override
            protected CellEditor getCellEditor(Object element) {
                HTMLActionDataType propertyType = HTMLActionDataType.fromValue(((HTMLActionParamMapping) element)
                        .getActionData());
                switch (propertyType) {
                case Constant:
                    Class<?> paramClass = ((HTMLActionParamMapping) element).getActionParam().getClazz();
                    if (ClassUtils.isAssignable(paramClass, Number.class, true)) {
                        return new NumberCellEditor(tableViewer.getTable());
                    } else if (ClassUtils.isAssignable(paramClass, String.class, true)) {
                        return new TextCellEditor(tableViewer.getTable());
                    } else if (ClassUtils.isAssignable(paramClass, Boolean.class, true)) {
                        return new ComboBoxCellEditor(tableViewer.getTable(), new String[] {
                                Boolean.TRUE.toString().toLowerCase(), Boolean.FALSE.toString().toLowerCase() });
                    }
                case Property:
                    return new ComboBoxCellEditor(tableViewer.getTable(), propertyNameList
                            .toArray(new String[propertyNameList.size()]));
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return element instanceof HTMLActionParamMapping;
            }

            @Override
            protected Object getValue(Object element) {
                HTMLActionDataType propertyType = HTMLActionDataType.fromValue(((HTMLActionParamMapping) element)
                        .getActionData());
                switch (propertyType) {
                case Constant:
                    Class<?> paramClass = ((HTMLActionParamMapping) element).getActionParam().getClazz();
                    if (ClassUtils.isAssignable(paramClass, Number.class, true)
                            || ClassUtils.isAssignable(paramClass, String.class, true)) {
                        return String.valueOf(((HTMLActionParamMapping) element).getActionData());
                    } else if (ClassUtils.isAssignable(paramClass, Boolean.class, true)) {
                        Boolean booleanValue = (Boolean) ((HTMLActionParamMapping) element).getActionData();
                        return booleanValue == true ? 0 : 1;
                    }
                    break;
                case Property:
                    HTMLElementProperty propertyData = (HTMLElementProperty) ((HTMLActionParamMapping) element)
                            .getActionData();
                    int propertyIndex = propertyNameList.indexOf(propertyData.getName());
                    if (propertyIndex >= 0 && propertyIndex < propertyNameList.size()) {
                        return propertyIndex;
                    }
                    return 0;
                }
                return null;
            }

            @Override
            protected void setValue(Object element, Object value) {
                HTMLActionDataType propertyType = HTMLActionDataType.fromValue(((HTMLActionParamMapping) element)
                        .getActionData());
                HTMLActionParamMapping actionParamMapping = (HTMLActionParamMapping) element;
                switch (propertyType) {
                case Constant:
                    Class<?> paramClass = ((HTMLActionParamMapping) element).getActionParam().getClazz();
                    if (ClassUtils.isAssignable(paramClass, Number.class, true)) {
                        try {
                            actionParamMapping.setActionData(NumberFormat.getInstance().parse(String.valueOf(value)));
                        } catch (NumberFormatException | ParseException e) {
                            // not a number, so not setting value
                        }
                    } else if (ClassUtils.isAssignable(paramClass, String.class, true) && value instanceof String) {
                        actionParamMapping.setActionData(value);
                    } else if (ClassUtils.isAssignable(paramClass, Boolean.class, true) && value instanceof Integer) {
                        actionParamMapping.setActionData((Integer) value == 0);
                    }
                    break;
                case Property:
                    if (value instanceof Integer) {
                        int propertyIndex = (int) value;
                        if (propertyIndex >= 0 && propertyIndex < propertyNameList.size()) {
                            actionParamMapping.setActionData(new HTMLElementProperty(propertyNameList
                                    .get(propertyIndex)));
                        }
                    }
                    break;
                }
                tableViewer.refresh(element);
            }
        });

        tableViewer.setLabelProvider(new ITableLabelProvider() {

            private static final int COLUMN_PARAM_NAME_INDEX = 0;
            private static final int COLUMN_PARAM_TYPE_INDEX = 1;
            private static final int COLUMN_VALUE_TYPE_INDEX = 2;
            private static final int COLUMN_VALUE_INDEX = 3;

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (!(element instanceof HTMLActionParamMapping) || columnIndex < 0 || columnIndex > COLUMN_VALUE_INDEX)
                    return "";
                HTMLActionParamMapping actionParamMapping = (HTMLActionParamMapping) element;
                switch (columnIndex) {
                case COLUMN_PARAM_NAME_INDEX:
                    return actionParamMapping.getActionParam().getName();
                case COLUMN_PARAM_TYPE_INDEX:
                    return actionParamMapping.getActionParam().getClazz().getSimpleName();
                case COLUMN_VALUE_TYPE_INDEX:
                    return HTMLActionDataType.fromValue(actionParamMapping.getActionData()).toString();
                case COLUMN_VALUE_INDEX:
                    if (actionParamMapping.getActionData() instanceof String) {
                        return "'" + (String) actionParamMapping.getActionData() + "'";
                    }
                    return String.valueOf(actionParamMapping.getActionData());
                }
                return null;
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }
        });

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(actionParamMappings);
        return container;
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(), new ColumnWeightData(weight, tableColumn.getColumn()
                .getWidth()));
    }

    public Object[] getActionData() {
        Object[] actionDatas = new Object[actionParams.length];
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
