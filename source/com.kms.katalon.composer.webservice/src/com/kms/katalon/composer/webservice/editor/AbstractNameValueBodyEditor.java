package com.kms.katalon.composer.webservice.editor;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;

public abstract class AbstractNameValueBodyEditor<P> extends HttpBodyEditor {

    protected  ToolItem btnAdd, btnRemove;
    
    protected ParameterTable tvParams;
    
    protected ParameterizedBodyContent<P> bodyContent;
    
    public AbstractNameValueBodyEditor(Composite parent, int style) {
        super(parent, style);
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        
        createToolbar(this);
        createParameterTable(this);
    }
    
    private void createToolbar(Composite parent) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
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
        btnRemove.setEnabled(false);
    }
    
    private void createParameterTable(Composite parent) {
        tvParams = doCreateParameterTable(parent);
        
        Table tParams = tvParams.getTable();
        tParams.setHeaderVisible(true);
        tParams.setLinesVisible(ControlUtils.shouldLineVisble(tParams.getDisplay()));
        tParams.addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(Event event) {
                tvParams.addEmptyRow();
            }
        });
        tParams.addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(Event event) {
                tvParams.showContextMenu();
            }
        });
    }

    protected abstract ParameterTable doCreateParameterTable(Composite parent);
    
    protected abstract P createEmptyParameter();
    
    protected abstract boolean isEmptyParameter(P parameter);
    
    @Override
    public String getContentType() {
        return bodyContent.getContentType();
    }

    @Override
    public String getContentData() {
        tvParams.removeEmptyRows();
        return JsonUtil.toJson(bodyContent);
    }
    
    private void updateButtonRemoveState() {
        if (bodyContent.getParameters().isEmpty()) {
            btnRemove.setEnabled(false);
        } else {
            btnRemove.setEnabled(true);
        }
    }

    protected class ParameterTable extends TableViewer {

        public ParameterTable(Composite parent, int style) {
            super(parent, style);
        }
        
        private void showContextMenu() {
            Table table = this.getTable();
            Menu menu = table.getMenu();
            if (menu != null) {
                menu.dispose();
            }
            menu = new Menu(table);

            MenuItem menuItemAdd = new MenuItem(menu, SWT.PUSH);
            menuItemAdd.setText(StringConstants.PARAM_TABLE_MENU_ITEM_ADD);
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
                menuItemRemove.setText(StringConstants.PARAM_TABLE_MENU_ITEM_REMOVE);
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
            List<P> params = bodyContent.getParameters();
            P lastParam;
            if (params.size() > 0 &&
                    isEmptyParameter(lastParam = params.get(params.size() - 1))) {
                tvParams.editElement(lastParam, 0);
            } else {
                P param = createEmptyParameter();
                bodyContent.addParameter(param);
                tvParams.add(param);
                tvParams.editElement(param, 0);
            }
            updateButtonRemoveState();
            fireModifyEvent();
        }
        
        public void addEmptyRowAt(int selectedRowIndex) {
            P param = createEmptyParameter();
            bodyContent.addParameter(selectedRowIndex, param);
            tvParams.insert(param, selectedRowIndex);
            tvParams.editElement(param, 0);
            updateButtonRemoveState();
            fireModifyEvent();
        }
        
        public void removeSelectedRows() {
            Object[] selections = tvParams.getStructuredSelection().toArray();
            for (Object selection : selections) {
                bodyContent.removeParameter((P) selection);
            }
            tvParams.remove(selections);
            updateButtonRemoveState();
            fireModifyEvent();
        }
        
        public void removeEmptyRows() {
            List<P> emptyParams = bodyContent.getParameters()
                    .stream()
                    .filter(p -> isEmptyParameter(p))
                    .collect(Collectors.toList());
            emptyParams.stream()
                    .forEach(p -> bodyContent.removeParameter(p));
            updateButtonRemoveState();
            tvParams.refresh();
        }
    }
}
