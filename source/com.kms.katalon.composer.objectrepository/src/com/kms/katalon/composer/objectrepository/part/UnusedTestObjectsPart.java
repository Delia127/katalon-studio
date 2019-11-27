package com.kms.katalon.composer.objectrepository.part;

import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.objectrepository.constant.ImageConstants;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.testdata.reader.CsvWriter;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;

public class UnusedTestObjectsPart extends CPart implements EventHandler {

    private static final String[] FILTER_NAMES = { "Comma Separated Values Files (*.csv)" };

    private static final String[] FILTER_EXTS = { "*.csv" };

    private MPart mPart;

    private Composite testObjectComposite;

    private TableViewer tableViewer;

    private ToolItem toolItemDeleteAll, toolItemExportCSV;

    List<FileEntity> unusedTestObjects;

    @Inject
    private Shell shell;

    @Inject
    protected EPartService partService;

    @Inject
    protected MApplication application;

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        this.mPart = part;
        initialize(mPart, partService);
        eventBroker.subscribe(EventConstants.UNUSED_TEST_OBJECTS_UPDATED, this);

        createTestObjectTable(parent);
        updateContent((List<FileEntity>) mPart.getObject());

    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.UNUSED_TEST_OBJECTS_UPDATED)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof List) {
                    updateContent((List<FileEntity>) object);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void createTestObjectTable(Composite parent) {
        testObjectComposite = new Composite(parent, SWT.NONE);
        testObjectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout glCompositeTable = new GridLayout();
        glCompositeTable.marginWidth = 0;
        glCompositeTable.marginHeight = 0;
        testObjectComposite.setLayout(glCompositeTable);

        createTestObjectTableToolbar(testObjectComposite);
        createTestObjectTableDetails(testObjectComposite);
    }

    private void createTestObjectTableDetails(Composite parent) {
        Composite compositeTableDetails = new Composite(parent, SWT.NONE);
        compositeTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTableDetails = new GridLayout(1, false);
        glCompositeTableDetails.marginWidth = 0;
        glCompositeTableDetails.marginHeight = 0;
        compositeTableDetails.setLayout(glCompositeTableDetails);

        tableViewer = new TableViewer(compositeTableDetails, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        GridData gridDataTable = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gridDataTable.minimumHeight = 150;
        table.setLayoutData(gridDataTable);

        TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnOrder = tableViewerColumnOrder.getColumn();
        tblclmnOrder.setText(StringConstants.UNUSED_TEST_OBJECT_TABLE_COL_ORDER);
        tblclmnOrder.setWidth(50);
        tableViewerColumnOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return String.valueOf(unusedTestObjects.indexOf((FileEntity) element) + 1);
            }
        });

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn trclmnColumnName = tableViewerColumnName.getColumn();
        trclmnColumnName.setText(StringConstants.UNUSED_TEST_OBJECT_TABLE_COL_NAME);
        trclmnColumnName.setWidth(600);
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FileEntity) element).getIdForDisplay();
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setInput(unusedTestObjects);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object element = selection.getFirstElement();
                eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, element);
            }
        });

        
        tableViewer.getTable().addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                Table table = tableViewer.getTable();
                Menu menu = table.getMenu();
                if (menu != null) {
                    menu.dispose();
                }
                menu = new Menu(table);
                MenuItem openTestObjectMenuItem = new MenuItem(menu, SWT.PUSH);
                openTestObjectMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_OPEN_TEST_OBJECT);
                openTestObjectMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                        Object element = selection.getFirstElement();
                        eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, element);
                    }
                });
                table.setMenu(menu);
            }
        });
    }

    private void createTestObjectTableToolbar(Composite parent) {
        Composite compositeTableToolBar = new Composite(parent, SWT.NONE);
        compositeTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeTableToolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        ToolBar tableToolbar = new ToolBar(compositeTableToolBar, SWT.FLAT | SWT.RIGHT);
        tableToolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        toolItemDeleteAll = new ToolItem(tableToolbar, SWT.NONE);
        toolItemDeleteAll.setText(StringConstants.UNUSED_TEST_OBJECT_TOOLBAR_DELETE_ALL);
        toolItemDeleteAll.setToolTipText(StringConstants.UNUSED_TEST_OBJECT_TOOLBAR_DELETE_ALL);
        toolItemDeleteAll.setImage(ImageConstants.IMG_16_REMOVE);

        toolItemDeleteAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (MessageDialog.openQuestion(shell, StringConstants.DELETE,
                        StringConstants.DIA_UNUSED_TEST_OBJECT_DELETE_MESSAGE)) {
                    deleteAll();
                }
            }
        });

        toolItemExportCSV = new ToolItem(tableToolbar, SWT.NONE);
        toolItemExportCSV.setText(StringConstants.UNUSED_TEST_OBJECT_TOOLBAR_EXPORT_CSV);
        toolItemExportCSV.setToolTipText(StringConstants.UNUSED_TEST_OBJECT_TOOLBAR_EXPORT_CSV);
        toolItemExportCSV.setImage(ImageConstants.IMG_16_EXPORT_CSV);

        toolItemExportCSV.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                exportCSV();
            }
        });
    }

    private void updateContent(List<FileEntity> content) {
        unusedTestObjects = content;
        tableViewer.setInput(unusedTestObjects);
    }

    private void deleteAll() {
        try {
            for (FileEntity element : unusedTestObjects) {
                EntityFileServiceManager.delete(element);
            }
            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
            updateContent(Collections.emptyList());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            openError(shell, StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    private void exportCSV() {
        FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
        fileDialog.setText("Export");
        fileDialog.setFilterExtensions(FILTER_EXTS);
        fileDialog.setFilterNames(FILTER_NAMES);
        fileDialog.setFileName("Unused Test Objects.csv");
        String filePath = fileDialog.open();

        if (filePath == null) {
            return;
        }

        try {
            File exportedFile = new File(filePath);
            String[] headers = new String[] { StringConstants.UNUSED_TEST_OBJECT_TABLE_COL_ORDER,
                    StringConstants.UNUSED_TEST_OBJECT_TABLE_COL_NAME };
            List<Object[]> datas = new ArrayList<Object[]>();
            CellProcessor[] cellProcessor = new CellProcessor[] { new NotNull(), new NotNull() };
            int index = 1;
            unusedTestObjects.stream().map(element -> element.getIdForDisplay()).toArray();
            for (FileEntity element : unusedTestObjects) {
                datas.add(new String[] { String.valueOf(index++), element.getIdForDisplay() });
            }
            CsvWriter.writeArraysToCsv(headers, datas, exportedFile, cellProcessor);
            UISynchronizeService.syncExec(() -> Program.launch(exportedFile.toURI().toString()));

        } catch (IOException e) {
            LoggerSingleton.logError(e);
            openError(shell, StringConstants.ERROR_TITLE, e.getMessage());
        }

    }

    @PreDestroy
    public void onClose() {
        eventBroker.unsubscribe(this);

    }
}
