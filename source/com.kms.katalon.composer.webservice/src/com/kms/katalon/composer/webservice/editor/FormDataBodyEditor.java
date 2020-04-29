package com.kms.katalon.composer.webservice.editor;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.composer.components.impl.editors.SingleFileSelectionDialogCellEditor;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.MediaTypes;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;;

public class FormDataBodyEditor extends AbstractNameValueBodyEditor<FormDataBodyParameter> {

    private static final String DEFAULT_CONTENT_TYPE = "multipart/form-data"; //$NON-NLS-1$

    private static final String DEFAULT_CHARSET = "UTF-8"; //$NON-NLS-1$

    private boolean initialized = false;

    private TableColumn cName, cValue, cType, cContentType;

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
        tParams.setLinesVisible(ControlUtils.shouldLineVisble(tParams.getDisplay()));

        ColumnViewerToolTipSupport.enableFor(tvParams, ToolTip.NO_RECREATE);

        TableViewerColumn cvName = new TableViewerColumn(tvParams, SWT.LEFT);
        cName = cvName.getColumn();
        cName.setText(ComposerWebserviceMessageConstants.FormDataBodyEditor_COL_NAME);
        cvName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormDataBodyParameter) element).getName();
            }
        });
        cvName.setEditingSupport(new EditingSupport(cvName.getViewer()) {
            @Override
            protected void setValue(Object element, Object value) {
                ((FormDataBodyParameter) element).setName(String.valueOf(value));
                tvParams.update(element, null);
                // tvParams.setInput(bodyContent.getParameters());
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
        cType.setText(ComposerWebserviceMessageConstants.FormDataBodyEditor_COL_TYPE);
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
                return new StringComboBoxCellEditor(tParams,
                        new String[] { FormDataBodyParameter.PARAM_TYPE_TEXT, FormDataBodyParameter.PARAM_TYPE_FILE });
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

        TableViewerColumn cvContentType = new TableViewerColumn(tvParams, SWT.LEFT);
        cContentType = cvContentType.getColumn();
        cContentType.setText(ComposerWebserviceMessageConstants.FormDataBodyEditor_COL_CONTENT_TYPE);
        cvContentType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                String contentType = ((FormDataBodyParameter) element).getContentType();
                if (StringUtils.isEmpty(contentType)) {
                    return ComposerWebserviceMessageConstants.FormDataBodyEditor_CONTENT_TYPE_AUTO;
                }
                return contentType;
            }

            @Override
            public Color getForeground(Object element) {
                String contentType = ((FormDataBodyParameter) element).getContentType();
                if (StringUtils.isEmpty(contentType)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
                }
                return super.getForeground(element);
            }
        });

        cvContentType.setEditingSupport(new EditingSupport(cvContentType.getViewer()) {

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new ContentTypeValueCellEditor(tParams) {
                    @Override
                    protected String getMessage() {
                        return ComposerWebserviceMessageConstants.FormDataBodyEditor_CONTENT_TYPE_AUTO;
                    }
                };
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                return ((FormDataBodyParameter) element).getContentType();
            }

            @Override
            protected void setValue(Object element, Object value) {
                String contentType = (String) value;
                ((FormDataBodyParameter) element).setContentType(contentType);
                tvParams.update(element, null);
                fireModifyEvent();
            }
        });

        TableViewerColumn cvValue = new TableViewerColumn(tvParams, SWT.LEFT);
        cValue = cvValue.getColumn();
        cValue.setText(ComposerWebserviceMessageConstants.FormDataBodyEditor_COL_VALUE);
        cvValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormDataBodyParameter) element).getValue();
            }
        });
        cvValue.setEditingSupport(new EditingSupport(cvValue.getViewer()) {
            @Override
            protected void setValue(Object element, Object value) {
                String filePath = (String) value;
                filePath = convertToRelativePathIfPossible(filePath);
                ((FormDataBodyParameter) element).setValue(String.valueOf(filePath));
                tvParams.update(element, null);
                fireModifyEvent();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                FormDataBodyParameter param = (FormDataBodyParameter) element;
                if (param.getType().equals(FormDataBodyParameter.PARAM_TYPE_TEXT)) {
                    return new TextCellEditor(tParams);
                } else {
                    return new SingleFileSelectionDialogCellEditor(tParams);
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

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cValue, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cType, new ColumnWeightData(20, 100));
        tableColumnLayout.setColumnData(cContentType, new ColumnWeightData(20, 100));

        return tvParams;
    }

    private String convertToRelativePathIfPossible(String filePath) {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        File file = new File(filePath);
        if (file.isAbsolute() && file.getAbsolutePath().startsWith(projectDir)) {
            return PathUtil.absoluteToRelativePath(filePath, projectDir);
        } else {
            return filePath;
        }
    }

    @Override
    protected FormDataBodyParameter createEmptyParameter() {
        FormDataBodyParameter param = new FormDataBodyParameter();
        param.setName(StringUtils.EMPTY);
        param.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
        param.setContentType(StringUtils.EMPTY);
        param.setValue(StringUtils.EMPTY);
        return param;
    }

    @Override
    protected boolean isEmptyParameter(FormDataBodyParameter parameter) {
        return StringUtils.isBlank(parameter.getName()) && StringUtils.isBlank(parameter.getValue());
    }

    @Override
    public void setInput(String httpBodyContent) {
        if (StringUtils.isEmpty(httpBodyContent)) {
            bodyContent = new ParameterizedBodyContent<FormDataBodyParameter>();
            bodyContent.setContentType(DEFAULT_CONTENT_TYPE);
            bodyContent.setCharset(DEFAULT_CHARSET);
        } else {
            bodyContent = JsonUtil.fromJson(httpBodyContent,
                    new TypeToken<ParameterizedBodyContent<FormDataBodyParameter>>() {
                    }.getType());
        }

        tvParams.setInput(bodyContent.getParameters());
        if (!bodyContent.getParameters().isEmpty()) {
            btnRemove.setEnabled(true);
        }
        updateViewModel();
    }

    @Override
    public void onBodyTypeChanged() {
        if (bodyContent == null) {
            bodyContent = new ParameterizedBodyContent<FormDataBodyParameter>();
            bodyContent.setContentType(DEFAULT_CONTENT_TYPE);
            bodyContent.setCharset(DEFAULT_CHARSET);
        }

        if (!initialized) {
            tvParams.setInput(bodyContent.getParameters());
            if (!bodyContent.getParameters().isEmpty()) {
                btnRemove.setEnabled(true);
            }
            initialized = true;
        }
        updateViewModel();
        setContentTypeUpdated(true);
    }
    
    private class ContentTypeValueCellEditor extends StringComboBoxCellEditor {

        public ContentTypeValueCellEditor(Composite parent) {
            super(parent, MediaTypes.PREDEFINED_MEDIA_TYPES);
            setMessage(getMessage());
        }

        private void setMessage(String message) {
            CCombo combo = (CCombo) getControl();

            try {
                Field textField = combo.getClass().getDeclaredField("text"); //$NON-NLS-1$
                textField.setAccessible(true);
                if (textField != null) {
                    Text text = (Text) textField.get(combo);
                    text.setMessage(message);
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                LoggerSingleton.logError(e);
            }
        }
        
        protected String getMessage() {
            return StringUtils.EMPTY;
        }
    }
}
