package com.kms.katalon.composer.webservice.editor;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.UrlEncodedBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

public class UrlEncodedBodyEditor extends HttpBodyEditor {
    
    private static final String TABLE_COLUMN_KEY = "Key";
    
    private static final String TABLE_COLUMN_VALUE = "Value";

    private ToolItem btnAdd, btnRemove;
    
    private ParameterTable tvParams;
    
    private TableColumn cKey, cValue;
    
    private UrlEncodedBodyContent bodyContent = new UrlEncodedBodyContent();
    
    public UrlEncodedBodyEditor(Composite parent, int style) {
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
        tableColumnLayout.setColumnData(cKey, new ColumnWeightData(50, 100));
        tableColumnLayout.setColumnData(cValue, new ColumnWeightData(50, 100));
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
            bodyContent = new UrlEncodedBodyContent();
        } else {
            bodyContent = JsonUtil.fromJson(httpBodyContent, UrlEncodedBodyContent.class);
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
            
            TableViewerColumn cvKey = new TableViewerColumn(tvParams, SWT.LEFT);
            cKey = cvKey.getColumn();
            cKey.setText(TABLE_COLUMN_KEY);
            cvKey.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((UrlEncodedBodyParameter) element).getName();
                }
            });
            cvKey.setEditingSupport(new EditingSupport(cvKey.getViewer()) {
                @Override
                protected void setValue(Object element, Object value) {
                    ((UrlEncodedBodyParameter) element).setName(String.valueOf(value));
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
                    return ((UrlEncodedBodyParameter) element).getName();
                }
            });
            
            TableViewerColumn cvValue = new TableViewerColumn(tvParams, SWT.LEFT);
            cValue = cvValue.getColumn();
            cValue.setText(TABLE_COLUMN_VALUE);
            cvValue.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((UrlEncodedBodyParameter) element).getValue();
                }
            });
            cvValue.setEditingSupport(new EditingSupport(cvValue.getViewer()) {
                @Override
                protected void setValue(Object element, Object value) {
                    ((UrlEncodedBodyParameter) element).setValue(String.valueOf(value));
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
                    return ((UrlEncodedBodyParameter) element).getValue();
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
            UrlEncodedBodyParameter param = new UrlEncodedBodyParameter();
            param.setName(StringUtils.EMPTY);
            param.setValue(StringUtils.EMPTY);
            bodyContent.addParameter(param);
            tvParams.add(param);
            tvParams.editElement(param, 0);
            fireModifyEvent();
        }
        
        public void addEmptyRowAt(int selectedRowIndex) {
            UrlEncodedBodyParameter param = new UrlEncodedBodyParameter();
            param.setName(StringUtils.EMPTY);
            param.setValue(StringUtils.EMPTY);
            bodyContent.addParameter(selectedRowIndex, param);
            tvParams.insert(param, selectedRowIndex);
            tvParams.editElement(param, 0);
            fireModifyEvent();
        }
        
        public void removeSelectedRows() {
            Object[] selections = tvParams.getStructuredSelection().toArray();
            for (Object selection : selections) {
                bodyContent.removeParameter((UrlEncodedBodyParameter) selection);
            }
            tvParams.remove(selections);
            fireModifyEvent();
        }
        
        public void removeEmptyRows() {
            bodyContent.removeEmptyParameters();
            tvParams.refresh();
        }
    }
}
