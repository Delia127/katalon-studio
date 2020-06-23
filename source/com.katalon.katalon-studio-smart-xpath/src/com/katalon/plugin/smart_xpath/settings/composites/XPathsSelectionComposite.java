package com.katalon.plugin.smart_xpath.settings.composites;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class XPathsSelectionComposite extends Composite {

    private List<Pair<String, Boolean>> selectedXPaths = Collections.emptyList();

    private TableViewer tvXpath;

    public XPathsSelectionComposite(Composite parent, int style) {
        super(parent, style);
        createContents();
    }

    private void createContents() {
        this.setLayout(new GridLayout());
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createXpathTableToolbar(this);
        createXPathTable(this);
    }

    private void createXpathTableToolbar(Composite parent) {
        Composite compositeXpathTableToolBar = new Composite(parent, SWT.NONE);
        compositeXpathTableToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeXpathTableToolBar.setLayout(new GridLayout(1, false));

        Button resetDefault = new Button(compositeXpathTableToolBar, SWT.WRAP);
        resetDefault.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        resetDefault.setText(StringConstants.RESET_DEFAULT);

        Label lblDragDropXPath = new Label(compositeXpathTableToolBar, SWT.NONE);
        lblDragDropXPath.setText(SmartXPathMessageConstants.LBL_TIPS_FOR_XPATH_TEST_DESIGN_SETTING);

        resetDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();
                    setInput(store.getDefaultCapturedObjectXpathLocators());
                    handleSelectionChange(null);
                } catch (IOException exception) {
                    LoggerSingleton.logError(exception);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Control createXPathTable(Composite parent) {
        Composite tableXPathComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.minimumHeight = 70;
        ldTableComposite.heightHint = 300;
        tableXPathComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableXPathComposite.setLayout(tableColumnLayout);

        tvXpath = new TableViewer(tableXPathComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvXpath.setContentProvider(ArrayContentProvider.getInstance());

        tvXpath.addDragSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() }, new DragSourceAdapter() {

            @Override
            public void dragSetData(DragSourceEvent event) {
                StructuredSelection selection = (StructuredSelection) tvXpath.getSelection();
                Pair<String, Boolean> xpath = ((Pair<String, Boolean>) selection.getFirstElement());
                event.data = String.valueOf(selectedXPaths.indexOf(xpath));
            }
        });
        tvXpath.addDropSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() }, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetEvent event) {
                Pair<String, Boolean> item = (Pair<String, Boolean>) ((TableItem) event.item).getData();
                int newIndex = selectedXPaths.indexOf(item);
                String index = (String) event.data;
                if (index != null && newIndex >= 0) {
                    int indexVal = Integer.parseInt(index);
                    Pair<String, Boolean> xpath = selectedXPaths.get(indexVal);
                    selectedXPaths.remove(indexVal);
                    selectedXPaths.add(newIndex, xpath);
                    tvXpath.setSelection(new StructuredSelection(xpath));
                    tvXpath.refresh();

                    handleSelectionChange(event);
                }
            }
        });

        Table tXpath = tvXpath.getTable();
        tXpath.setHeaderVisible(true);
        tXpath.setLinesVisible(ControlUtils.shouldLineVisble(tXpath.getDisplay()));

        TableViewerColumn cvXpathName = new TableViewerColumn(tvXpath, SWT.LEFT);
        TableColumn cName = cvXpathName.getColumn();
        cName.setText(StringConstants.NAME);
        cvXpathName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }
        });

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(80, false));

        return tableXPathComposite;
    }

    public void setInput(List<Pair<String, Boolean>> input) {
        selectedXPaths = input;
        tvXpath.setInput(selectedXPaths);
    }

    public List<Pair<String, Boolean>> getInput() {
        List<Pair<String, Boolean>> emptyXpathItems = selectedXPaths.stream()
                .filter(i -> i.getLeft().isEmpty())
                .collect(Collectors.toList());
        selectedXPaths.removeAll(emptyXpathItems);

        return selectedXPaths;
    }

    public boolean compareInput(List<Pair<String, Boolean>> selectedXPaths) {
        List<Pair<String, Boolean>> _selectedXPaths = getInput();
        return _selectedXPaths != null && _selectedXPaths.equals(selectedXPaths);
    }

    private void handleSelectionChange(TypedEvent selectionEvent) {
        dispatchSelectionEvent(selectionEvent);
    }

    private void dispatchSelectionEvent(TypedEvent selectionEvent) {
        notifyListeners(SWT.Selection, null);
        notifyListeners(SWT.DefaultSelection, null);
    }

    public void addSelectionListener(SelectionListener listener) {
        checkWidget();
        if (listener == null) {
            return;
        }
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Selection, typedListener);
        addListener(SWT.DefaultSelection, typedListener);
    }
}
