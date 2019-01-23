package com.kms.katalon.plugin.dialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.ResultItem;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.store.PluginPreferenceStore;

public class KStorePluginsDialog extends Dialog {
    
    private static final int CLMN_PLUGIN_NAME_IDX = 0;

    private List<ResultItem> result;

    protected KStorePluginsDialog(Shell parentShell) {
        super(parentShell);
    }

    public KStorePluginsDialog(Shell shell, List<ResultItem> result) {
        this(shell);
        this.result = result;
    }

    @Override
    protected Control createDialogArea(Composite parent) { 
        Composite body = new Composite(parent, SWT.BORDER);
        body.setLayout(new GridLayout(1, false));
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Composite tableComposite = new Composite(body, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableComposite.setLayout(new FillLayout());
        
        TableViewer pluginTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        pluginTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table pluginTable = pluginTableViewer.getTable();
        pluginTable.setHeaderVisible(true);
        pluginTable.setLinesVisible(true);
        
        TableViewerColumn tableViewerColumnPluginName = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn colPluginName = tableViewerColumnPluginName.getColumn();
        colPluginName.setWidth(200);
        colPluginName.setText(StringConstants.KStorePluginsDialog_COL_PLUGIN);
        tableViewerColumnPluginName.setLabelProvider(new PluginNameColumnLabelProvider(CLMN_PLUGIN_NAME_IDX));
        
        TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn colStatus = tableViewerColumnStatus.getColumn();
        colStatus.setText(StringConstants.KStorePluginsDialog_COL_STATUS);
        colStatus.setWidth(100);
        tableViewerColumnStatus.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ResultItem item = (ResultItem) element;
                if (item.isPluginInstalled()) {
                    return StringConstants.KStorePluginsDialog_STATUS_INSTALLED;
                } else {
                    return StringConstants.KStorePluginsDialog_STATUS_UNINSTALLED;
                }
            }
        });
        
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(colPluginName, new ColumnWeightData(80, 70));
        tableLayout.setColumnData(colStatus, new ColumnWeightData(20, 30));
        tableComposite.setLayout(tableLayout);
        
        pluginTableViewer.setInput(result);
        
        Button btnClose = new Button(body, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        btnClose.setText(IDialogConstants.CLOSE_LABEL);
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                KStorePluginsDialog.this.setReturnCode(Dialog.CANCEL);
                KStorePluginsDialog.this.close();
            }
        });
        
        return body;
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.KStorePluginsDialog_DIA_TITLE);
    }
    
    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), Math.max(300, initialSize.y));
    }
    
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    private class PluginNameColumnLabelProvider extends HyperLinkColumnLabelProvider<ResultItem> {

        public PluginNameColumnLabelProvider(int columnIndex) {
            super(columnIndex);
        }
                
        @Override
        protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
            try {
                ResultItem resultItem = (ResultItem) cell.getElement();
                PluginPreferenceStore pluginPrefStore = new PluginPreferenceStore();
                KStoreUsernamePasswordCredentials credentials = pluginPrefStore.getKStoreUsernamePasswordCredentials();
                
                KStoreRestClient restClient = new KStoreRestClient(credentials);
                restClient.goToProductPage(resultItem.getPlugin().getProduct());
            } catch (GeneralSecurityException | IOException | KStoreClientException ex) {
                LoggerSingleton.logError(ex);
            }
        }

        @Override
        protected Class<ResultItem> getElementType() {
            return ResultItem.class;
        }


        @Override
        protected Image getImage(ResultItem element) {
            return null;
        }


        @Override
        protected String getText(ResultItem element) {
            return element.getPlugin().getProduct().getName();
        }
    }
}
