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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.FormDataBodyContent;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;

public class FormDataBodyEditor extends HttpBodyEditor {

    private static final String TABLE_COLUMN_KEY = "Key";
    
    private static final String TABLE_COLUMN_VALUE = "Value";
    
    private static final String TABLE_COLUMN_TYPE = "Type";

    private ToolItem btnAdd, btnRemove;
    
    private ParameterTable tvParams;
    
    private TableColumn cKey, cValue, cType;
    
    private FormDataBodyContent bodyContent = new FormDataBodyContent();
    
    public FormDataBodyEditor(Composite parent, int style) {
        super(parent, style);
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        
        createToolbar(this);
        createParamTable(this);
    }
    
    private void createToolbar(Composite parent) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAdd = new ToolItem(toolbar, SWT.FLAT);
        btnAdd.setText(StringConstants.ADD);
        btnAdd.setImage(ImageManager.getImage(IImageKeys.ADD_16));
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tvParams.addEmptyRow();
            }
        });
        
        btnRemove = new ToolItem(toolbar, SWT.FLAT);
        btnRemove.setText(StringConstants.REMOVE);
        btnRemove.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
        btnRemove.setDisabledImage(ImageManager.getImage(IImageKeys.DELETE_DISABLED_16));
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tvParams.removeSelectedRows();
            }
        });
    }
    
    private void createParamTable(Composite parent) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 150;
        tableComposite.setLayoutData(gdData);
        tableComposite.setLayout(tableColumnLayout);
        
        tvParams = new ParameterTable(tableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvParams.init();
        tableColumnLayout.setColumnData(cKey, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cValue, new ColumnWeightData(30, 100));
        tableColumnLayout.setColumnData(cType, new ColumnWeightData(40, 100));
    }

    @Override
    public String getContentType() {
        return bodyContent.getContentType();
    }

    @Override
    public String getContentData() {
        tvParams.removeEmptyRows();
        return JsonUtil.toJson(bodyContent);
    }

    @Override
    public void setInput(String httpBodyContent) {
        if (StringUtils.isEmpty(httpBodyContent)) {
            bodyContent = new FormDataBodyContent();
        } else {
            bodyContent = JsonUtil.fromJson(httpBodyContent, FormDataBodyContent.class);
        }
        
        tvParams.setInput(bodyContent.getParameters());
        if (!bodyContent.getParameters().isEmpty()) {
            btnRemove.setEnabled(true);
        }
    }
    
    private class ParameterTable extends TableViewer {
        
        private static final String MENU_ITEM_ADD = "Insert";
        
        private static final String MENU_ITEM_REMOVE = "Delete";

        public ParameterTable(Composite parent, int style) {
            super(parent, style);
        }
        
        public void init() {
            tvParams.setContentProvider(ArrayContentProvider.getInstance());
            Table tParams = tvParams.getTable();
            tParams.setHeaderVisible(true);
            tParams.setLinesVisible(true);
            tParams.addListener(SWT.MouseDoubleClick, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    addEmptyRow();
                }
            });
            tParams.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    showContextMenu();
                }
            });
            
            tvParams.setInput(bodyContent.getParameters());
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
                            new String[]{FormDataBodyParameter.PARAM_TYPE_TEXT, FormDataBodyParameter.PARAM_TYPE_FILE});
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
                    FormDataBodyParameter param = (FormDataBodyParameter) element;
//                    if (value instanceof String[] &&
//                            param.getType().equals(FormDataBodyParameter.PARAM_TYPE_FILE)) {
//                        String[] filePaths = (String[]) value;
//                        param.setFilePaths(filePaths);
//                    } else {
//                        param.setValue(String.valueOf(value));
//                    }
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
        }
        
        private void showContextMenu() {
            Table table = this.getTable();
            Menu menu = table.getMenu();
            if (menu != null) {
                menu.dispose();
            }
            menu = new Menu(table);

            MenuItem menuItemAdd = new MenuItem(menu, SWT.PUSH);
            menuItemAdd.setText(MENU_ITEM_ADD);
            menuItemAdd.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = table.getSelection();
                    if (items.length > 0) {
                        addEmptyRowAt(table.indexOf(items[0]));
                    } else {
                        addEmptyRow();
                    }
                }
            });
            
            if (table.getItemCount() > 0) {
                MenuItem menuItemRemove = new MenuItem(menu, SWT.PUSH);
                menuItemRemove.setText(MENU_ITEM_REMOVE);
                menuItemRemove.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        removeSelectedRows();
                    }
                });
            }
            
            table.setMenu(menu);
        }

        public void addEmptyRow() {
            FormDataBodyParameter param = new FormDataBodyParameter();
            param.setName(StringUtils.EMPTY);
            param.setValue(StringUtils.EMPTY);
            param.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
            bodyContent.addParameter(param);
            tvParams.add(param);
            tvParams.editElement(param, 0);
            fireModifyEvent();
        }
        
        public void addEmptyRowAt(int selectedRowIndex) {
            FormDataBodyParameter param = new FormDataBodyParameter();
            param.setName(StringUtils.EMPTY);
            param.setValue(StringUtils.EMPTY);
            param.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
            bodyContent.addParameter(selectedRowIndex, param);
            tvParams.insert(param, selectedRowIndex);
            tvParams.editElement(param, 0);
            fireModifyEvent();
        }
        
        public void removeSelectedRows() {
            Object[] selections = tvParams.getStructuredSelection().toArray();
            for (Object selection : selections) {
                bodyContent.removeParameter((FormDataBodyParameter) selection);
            }
            tvParams.remove(selections);
            fireModifyEvent();
        }
        
        public void removeEmptyRows() {
            bodyContent.removeEmptyParameters();
            tvParams.refresh();
        }
    }
    
    private class FileSelectionCellEditor extends DialogCellEditor {
        
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
            
            FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);
  
            String firstFile = fileDialog.open();
            if (firstFile != null) {
                String filterPath = fileDialog.getFilterPath();
                String[] selectedFiles = fileDialog.getFileNames();
                String[] filePaths = new String[selectedFiles.length];
                for (int i = 0; i < selectedFiles.length; i++) {
                    String file = selectedFiles[i];
                    String filePath = String.format("%s%s%s", 
                                                filterPath, 
                                                FileSystems.getDefault().getSeparator(), 
                                                file);
                    filePaths[i]= filePath;
                }
                return filePaths;
            } else {
                return oldValue;
            }
        }
    }
}
