package com.kms.katalon.composer.webservice.editor;

import java.nio.file.FileSystems;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.BodyContent;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;

public class FormDataBodyEditor extends AbstractFormBodyEditor<FormDataBodyParameter> {

    private static final String TABLE_COLUMN_KEY = "Key";
    
    private static final String TABLE_COLUMN_VALUE = "Value";
    
    private static final String TABLE_COLUMN_TYPE = "Type";
    
    private static final String DEFAULT_CONTENT_TYPE = "multipart/form-data";
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    private TableColumn cKey, cValue, cType;
    
    public FormDataBodyEditor(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected ParameterTable doCreateParameterTable(Composite parent) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 150;
        tableComposite.setLayoutData(gdData);
        tableComposite.setLayout(tableColumnLayout);
        
        ParameterTable tvParams = new ParameterTable(tableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        
        tvParams.setContentProvider(ArrayContentProvider.getInstance());
        Table tParams = tvParams.getTable();
        tParams.setHeaderVisible(true);
        tParams.setLinesVisible(true);
        
        ColumnViewerToolTipSupport.enableFor(tvParams, ToolTip.NO_RECREATE);
        
        TableViewerColumn cvKey = new TableViewerColumn(tvParams, SWT.LEFT);
        cKey = cvKey.getColumn();
        cKey.setText(TABLE_COLUMN_KEY);
        cvKey.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormDataBodyParameter) element).getName();
            }
        });
        cvKey.setEditingSupport(new EditingSupport(cvKey.getViewer()) {
            @Override
            protected void setValue(Object element, Object value) {
                ((FormDataBodyParameter) element).setName(String.valueOf(value));
                tvParams.update(element, null);
                fireModifyEvent();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tParams);
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                return ((FormDataBodyParameter) element).getName();
            }
        });
        
        TableViewerColumn cvType = new TableViewerColumn(tvParams, SWT.LEFT);
        cType = cvType.getColumn();
        cType.setText(TABLE_COLUMN_TYPE);
        cvType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormDataBodyParameter) element).getType();
            }
        });
        cvType.setEditingSupport(new EditingSupport(cvType.getViewer()) {
            @Override
            protected void setValue(Object element, Object value) {
                FormDataBodyParameter param = (FormDataBodyParameter) element;
                if (!param.getType().equals((String) value)) {
                    param.setValue(StringUtils.EMPTY);
                }
                param.setType(String.valueOf(value));
                tvParams.update(element, null);
                fireModifyEvent();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new StringComboBoxCellEditor(
                        tParams, 
                        new String[]{
                                FormDataBodyParameter.PARAM_TYPE_TEXT, 
                                FormDataBodyParameter.PARAM_TYPE_FILE
                        });
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                return ((FormDataBodyParameter) element).getType();
            }
        });
        
        TableViewerColumn cvValue = new TableViewerColumn(tvParams, SWT.LEFT);
        cValue = cvValue.getColumn();
        cValue.setText(TABLE_COLUMN_VALUE);
        cvValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormDataBodyParameter) element).getValue();
            }
        });
        cvValue.setEditingSupport(new EditingSupport(cvValue.getViewer()) {
            @Override
            protected void setValue(Object element, Object value) {
                ((FormDataBodyParameter) element).setValue(String.valueOf(value));
                tvParams.update(element, null);
                fireModifyEvent();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                FormDataBodyParameter param = (FormDataBodyParameter) element;
                if (param.getType().equals(FormDataBodyParameter.PARAM_TYPE_TEXT)) {
                    return new TextCellEditor(tParams);
                } else {
                    return new FileSelectionCellEditor(tParams);
                }
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                return ((FormDataBodyParameter) element).getValue();
            }
        });
        
        tableColumnLayout.setColumnData(cKey, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cValue, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cType, new ColumnWeightData(40, 100));
        
        return tvParams;
    }

    @Override
    protected FormDataBodyParameter createEmptyParameter() {
        FormDataBodyParameter param = new FormDataBodyParameter();
        param.setName(StringUtils.EMPTY);
        param.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
        param.setValue(StringUtils.EMPTY);
        return param;
    }

    @Override
    protected boolean checkEmptyParameter(FormDataBodyParameter parameter) {
        return StringUtils.isBlank(parameter.getName())
                && StringUtils.isBlank(parameter.getValue());
    }

    @Override
    public void setInput(String httpBodyContent) {
        if (StringUtils.isEmpty(httpBodyContent)) {
            bodyContent = new BodyContent<FormDataBodyParameter>();
            bodyContent.setContentType(DEFAULT_CONTENT_TYPE);
            bodyContent.setCharset(DEFAULT_CHARSET);
        } else {
            bodyContent = JsonUtil.fromJson(httpBodyContent, 
                    new TypeToken<BodyContent<FormDataBodyParameter>>(){}.getType());
        }
        
        tvParams.setInput(bodyContent.getParameters());
        if (!bodyContent.getParameters().isEmpty()) {
            btnRemove.setEnabled(true);
        }
    }

    public class FileSelectionCellEditor extends DialogCellEditor {

        public FileSelectionCellEditor(Composite parent) {
            super(parent, SWT.NONE);
        }
        
        @Override
        protected Button createButton(Composite parent) {
            Button result = new Button(parent, SWT.DOWN);
            result.setText("Choose files");
            return result;
        }

        @Override
        protected Object openDialogBox(Control cellEditorWindow) {
            Object oldValue = doGetValue();
            
            FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SINGLE);

            String firstFile = fileDialog.open();
            if (firstFile != null) {
                String filterPath = fileDialog.getFilterPath();
                String fileName = fileDialog.getFileName();
                String filePath = String.format("%s%s%s", 
                      filterPath, 
                      FileSystems.getDefault().getSeparator(), 
                      fileName);
                return filePath;
            } else {
                return oldValue;
            }
        }
    }
}
