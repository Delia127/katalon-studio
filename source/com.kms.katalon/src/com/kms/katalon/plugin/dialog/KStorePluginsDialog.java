package com.kms.katalon.plugin.dialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.ReloadItem;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.store.PluginPreferenceStore;

public class KStorePluginsDialog extends Dialog {
    
    private static final int CLMN_PLUGIN_NAME_IDX = 0;
    
    private static final int CLMN_REVIEW_IDX = 3;
    
    private static final int CLMN_PURCHASE_IDX = 4;
    
    private static final int CLMN_ERROR_IDX = 5;

    private List<ReloadItem> results;
    
    private Label lblWarning;

    protected KStorePluginsDialog(Shell parentShell) {
        super(parentShell);
    }

    public KStorePluginsDialog(Shell shell, List<ReloadItem> results) {
        this(shell);
        this.results = results;
    }

    @Override
    protected Control createDialogArea(Composite parent) { 
        Composite body = new Composite(parent, SWT.BORDER);
        body.setLayout(new GridLayout(1, false));
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        body.setLayoutData(gdBody);
        
        lblWarning = new Label(body, SWT.WRAP);
        GridData gdWarning = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdWarning.widthHint = 430;
        lblWarning.setLayoutData(gdWarning);
        lblWarning.setText(StringConstants.KStorePluginsDialog_LBL_WARNING);
        ControlUtils.setFontStyle(lblWarning, SWT.ITALIC, -1);
        boolean visible = shouldShowExpiryWarningMessage();
        gdWarning.exclude = !visible;
        lblWarning.setVisible(visible);
        
        Composite tableComposite = new Composite(body, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableComposite.setLayout(new FillLayout());
        
        TableViewer pluginTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        pluginTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table pluginTable = pluginTableViewer.getTable();
        pluginTable.setHeaderVisible(true);
        pluginTable.setLinesVisible(true);
        
        TableViewerColumn tableViewerColumnPluginName = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnPluginName = tableViewerColumnPluginName.getColumn();
        tableColumnPluginName.setText(StringConstants.KStorePluginsDialog_COL_PLUGIN);
        tableViewerColumnPluginName.setLabelProvider(new PluginNameColumnLabelProvider(CLMN_PLUGIN_NAME_IDX));
        
        TableViewerColumn tableViewerColumnLicense = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnLicense = tableViewerColumnLicense.getColumn();
        tableColumnLicense.setText(StringConstants.KStorePluginsDialog_COL_LICENSE);
        tableViewerColumnLicense.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ReloadItem item = (ReloadItem) element;
                KStorePlugin plugin = item.getPlugin();
                if (plugin.isFree()) {
                    return StringConstants.KStorePluginsDialog_LICENSE_FREE;
                }
                
                if (plugin.isPaid()) {
                    return StringConstants.KStorePluginsDialog_LICENSE_PAID;
                }
                
                if (plugin.isExpired()) {
                    return StringConstants.KStorePluginsDialog_LICENSE_EXPIRED;
                }
                
                if (plugin.isTrial()) {
                    return StringConstants.KStorePluginsDialog_LICENSE_TRIAL;
                }

                return StringUtils.EMPTY;
            }
            
            @Override
            public Color getForeground(Object element) {
                ReloadItem item = (ReloadItem) element;
                KStorePlugin plugin = item.getPlugin();
                Color colorWarning = new Color(Display.getCurrent(), 255, 165, 0); //orange
                if (checkExpire(plugin)) {
                    return colorWarning;
                }
                return super.getForeground(element);
            }
        });
      
        TableViewerColumn tableViewerColumnVersion = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnVersion = tableViewerColumnVersion.getColumn();
        tableColumnVersion.setText(StringConstants.KStorePluginsDialog_COL_VERSION);
        tableViewerColumnVersion.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ReloadItem item = (ReloadItem) element;
                return item.getPlugin().getLatestCompatibleVersion().getNumber();
            }
        });
        
        TableViewerColumn tableViewerColumnReview = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnReview = tableViewerColumnReview.getColumn();
        tableViewerColumnReview.setLabelProvider(new ReviewColumnLabelProvider(CLMN_REVIEW_IDX));
        
        TableViewerColumn tableViewerColumnPurchase = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnPurchase = tableViewerColumnPurchase.getColumn();
        tableViewerColumnPurchase.setLabelProvider(new PurchaseColumnLabelProvider(CLMN_PURCHASE_IDX));
        
        TableViewerColumn tableViewerColumnError = new TableViewerColumn(pluginTableViewer, SWT.LEFT);
        TableColumn tableColumnError = tableViewerColumnError.getColumn();
        tableViewerColumnError.setLabelProvider(new ErrorColumnLabelProvider(CLMN_ERROR_IDX));
        
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tableColumnPluginName, new ColumnWeightData(30, 40));
        tableLayout.setColumnData(tableColumnLicense, new ColumnWeightData(20, 10));
        tableLayout.setColumnData(tableColumnVersion, new ColumnWeightData(10, 20));
        tableLayout.setColumnData(tableColumnReview, new ColumnWeightData(15, 30));
        tableLayout.setColumnData(tableColumnPurchase, new ColumnWeightData(15, 30));
        tableLayout.setColumnData(tableColumnError, new ColumnWeightData(10, 20));
        tableComposite.setLayout(tableLayout);
        
        pluginTableViewer.setInput(results);
        
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
    
    private boolean shouldShowExpiryWarningMessage() {
        return results.stream()
            .filter(r -> checkExpire(r.getPlugin()))
            .findAny()
            .isPresent();         
    }
    
    private boolean checkExpire(KStorePlugin plugin) {
         return plugin.isExpired() || (plugin.isTrial() && plugin.getRemainingDay() <= 14);
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
    
    private class PluginNameColumnLabelProvider extends HyperLinkColumnLabelProvider<ReloadItem> {

        public PluginNameColumnLabelProvider(int columnIndex) {
            super(columnIndex);
        }
                
        @Override
        protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
            try {
                ReloadItem resultItem = (ReloadItem) cell.getElement();
                PluginPreferenceStore pluginPrefStore = new PluginPreferenceStore();
                KStoreUsernamePasswordCredentials credentials = pluginPrefStore.getKStoreUsernamePasswordCredentials();
                
                KStoreRestClient restClient = new KStoreRestClient(credentials);
                restClient.goToProductPage(resultItem.getPlugin().getProduct());
            } catch (GeneralSecurityException | IOException | KStoreClientException ex) {
                LoggerSingleton.logError(ex);
            }
        }

        @Override
        protected Class<ReloadItem> getElementType() {
            return ReloadItem.class;
        }


        @Override
        protected Image getImage(ReloadItem element) {
            return null;
        }


        @Override
        protected String getText(ReloadItem element) {
            return element.getPlugin().getProduct().getName();
        }
    }
    
    private class ReviewColumnLabelProvider extends HyperLinkColumnLabelProvider<ReloadItem> {

        public ReviewColumnLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
            try {
                ReloadItem resultItem = (ReloadItem) cell.getElement();
                PluginPreferenceStore pluginPrefStore = new PluginPreferenceStore();
                KStoreUsernamePasswordCredentials credentials = pluginPrefStore.getKStoreUsernamePasswordCredentials();
                
                KStoreRestClient restClient = new KStoreRestClient(credentials);
                restClient.goToProductReviewPage(resultItem.getPlugin().getProduct());
            } catch (GeneralSecurityException | IOException | KStoreClientException ex) {
                LoggerSingleton.logError(ex);
            }
        }

        @Override
        protected Class<ReloadItem> getElementType() {
            return ReloadItem.class;
        }

        @Override
        protected Image getImage(ReloadItem element) {
            return null;
        }

        @Override
        protected String getText(ReloadItem element) {
            return StringConstants.KStorePluginsDialog_LNK_REVIEW;
        }
    }
    
    private class PurchaseColumnLabelProvider extends HyperLinkColumnLabelProvider<ReloadItem> {

        public PurchaseColumnLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
            try {
                ReloadItem resultItem = (ReloadItem) cell.getElement();
                PluginPreferenceStore pluginPrefStore = new PluginPreferenceStore();
                KStoreUsernamePasswordCredentials credentials = pluginPrefStore.getKStoreUsernamePasswordCredentials();
                
                KStoreRestClient restClient = new KStoreRestClient(credentials);
                restClient.goToProductPricingPage(resultItem.getPlugin().getProduct());
            } catch (GeneralSecurityException | IOException | KStoreClientException ex) {
                LoggerSingleton.logError(ex);
            }
        }

        @Override
        protected Class<ReloadItem> getElementType() {
            return ReloadItem.class;
        }

        @Override
        protected Image getImage(ReloadItem element) {
            return null;
        }

        @Override
        protected String getText(ReloadItem element) {
            KStorePlugin plugin = element.getPlugin();
            if (plugin.isTrial() || plugin.isExpired()) {
                return StringConstants.KStorePluginsDialog_LNK_PURCHASE;
            }
            return StringUtils.EMPTY;
        }
    }
    
    private class ErrorColumnLabelProvider extends HyperLinkColumnLabelProvider<ReloadItem> {
        public ErrorColumnLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
            ReloadItem reloadItem = (ReloadItem) cell.getElement();
            if (reloadItem.getException() != null) {
                Exception exception = reloadItem.getException();
                MultiStatusErrorDialog.showErrorDialog(exception, "Failed to reload plugin", exception.getMessage());
            }
        }

        @Override
        protected Class<ReloadItem> getElementType() {
            return ReloadItem.class;
        }

        @Override
        protected Image getImage(ReloadItem element) {
            return null;
        }

        @Override
        protected String getText(ReloadItem element) {
            if (element.getException() != null) {
                return StringConstants.KStorePluginsDialog_LNK_ERROR;
            }
            return StringUtils.EMPTY;
        }
        
        @Override
        public void update(ViewerCell cell) {
            super.update(cell);

            cell.setStyleRanges(new StyleRange[] { getHyperLinkStyleRange(cell) });
        }
        
        private StyleRange getHyperLinkStyleRange(ViewerCell cell) {
            StyleRange hyperLinkStyle = new StyleRange();
            hyperLinkStyle.foreground = cell.getItem().getDisplay().getSystemColor(SWT.COLOR_RED);
            hyperLinkStyle.underline = true;
            hyperLinkStyle.start = 0;
            hyperLinkStyle.length = cell.getText().length();
            return hyperLinkStyle;
        }
    }
}
