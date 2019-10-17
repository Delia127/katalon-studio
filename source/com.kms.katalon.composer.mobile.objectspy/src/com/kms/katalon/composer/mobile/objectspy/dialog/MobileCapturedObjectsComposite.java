package com.kms.katalon.composer.mobile.objectspy.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.provider.CapturedElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.provider.SelectableElementEditingSupport;
import com.kms.katalon.composer.mobile.objectspy.viewer.CapturedObjectTableViewer;

public class MobileCapturedObjectsComposite extends Composite {

    private Dialog parentDialog;

    private CapturedObjectTableViewer capturedObjectsTableViewer;

    public CapturedObjectTableViewer getCapturedObjectsTableViewer() {
        return capturedObjectsTableViewer;
    }

    private Table capturedObjectsTable;

    private TableColumn tableIndexColumn;

    private Composite innerTableComposite;

    public TableColumn getCapturedObjectsColumn() {
        return tableIndexColumn;
    }

    public MobileCapturedObjectsComposite(Dialog parentDialog, Composite parent, int style) {
        super(parent, style);
        this.parentDialog = parentDialog;
        this.createComposite();
    }

    public MobileCapturedObjectsComposite(Dialog parentDialog, Composite parent) {
        this(parentDialog, parent, SWT.NONE);
    }

    private void createComposite() {
        setLayout(new GridLayout());

        createCompositeLabel();
        createInnerTableComposite();
        createCapturedObjectsTableViewer();
        buildCapturedObjectsTable();
        createTableViewerColumns();
        setupTableEventListeners();
    }

    private void createCompositeLabel() {
        Label lblCapturedObjects = new Label(this, SWT.NONE);
        lblCapturedObjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);
        ControlUtils.setFontToBeBold(lblCapturedObjects);
    }

    private void createInnerTableComposite() {
        innerTableComposite = new Composite(this, SWT.NONE);
        innerTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    private void createCapturedObjectsTableViewer() {
        capturedObjectsTableViewer = new CapturedObjectTableViewer(innerTableComposite,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION,
                (MobileElementDialog) this.parentDialog);
        capturedObjectsTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(capturedObjectsTableViewer);
    }

    private void buildCapturedObjectsTable() {
        capturedObjectsTable = capturedObjectsTableViewer.getTable();
        capturedObjectsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        capturedObjectsTable.setHeaderVisible(true);
        capturedObjectsTable.setLinesVisible(ControlUtils.shouldLineVisble(capturedObjectsTable.getDisplay()));
        capturedObjectsTable.setToolTipText(StringUtils.EMPTY);
    }
    
    private void createTableViewerColumns() {
        TableViewerColumn tableViewerIndexColumn = new TableViewerColumn(capturedObjectsTableViewer, SWT.NONE);
        tableViewerIndexColumn
                .setLabelProvider(new CapturedElementLabelProvider(CapturedElementLabelProvider.SELECTION_COLUMN_IDX));
        tableViewerIndexColumn
                .setEditingSupport(new SelectableElementEditingSupport(capturedObjectsTableViewer));
        tableIndexColumn = tableViewerIndexColumn.getColumn();

        TableViewerColumn tableViewerNameColumn = new TableViewerColumn(capturedObjectsTableViewer, SWT.NONE);
        tableViewerNameColumn
                .setLabelProvider(new CapturedElementLabelProvider(CapturedElementLabelProvider.ELEMENT_COLUMN_IDX));
        TableColumn tableNameColumn = tableViewerNameColumn.getColumn();
        tableNameColumn.setText(StringConstants.NAME);

        TableColumnLayout tbclCapturedObjects = new TableColumnLayout();
        int selectionColMinWidth = Platform.OS_MACOSX.equals(Platform.getOS()) ? 21 : 30;
        tbclCapturedObjects.setColumnData(tableIndexColumn, new ColumnWeightData(0, selectionColMinWidth, false));
        tbclCapturedObjects.setColumnData(tableNameColumn, new ColumnWeightData(60, 250 - selectionColMinWidth));
        
        innerTableComposite.setLayout(tbclCapturedObjects);
    }

    private void setupTableEventListeners() {
        capturedObjectsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                CapturedMobileElement firstElement = (CapturedMobileElement) selection.getFirstElement();
                ((MobileElementInspectorDialog) parentDialog).getPropertiesComposite().setEditingElement(firstElement);
            }
        });

        capturedObjectsTableViewer.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) {
                    return;
                }
                Point pt = new Point(e.x, e.y);
                TableItem item = capturedObjectsTableViewer.getTable().getItem(pt);
                if (item != null) {
                    ((MobileObjectSpyDialog) parentDialog).highlightObject((CapturedMobileElement) item.getData());
                }
            }
        });

        capturedObjectsTableViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                CapturedMobileElement mobileElement = capturedObjectsTableViewer.getSelectedElement();
                ((MobileObjectSpyDialog) parentDialog).setSelectedElement(mobileElement);
            }
        });

        capturedObjectsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                CapturedMobileElement[] elements = capturedObjectsTableViewer.getSelectedElements();
                if (elements == null || elements.length == 0) {
                    return;
                }
                switch (e.keyCode) {
                    case SWT.DEL: {
                        ((MobileObjectSpyDialog) parentDialog).removeSelectedCapturedElements(elements);
                        break;
                    }
                    case SWT.F5: {
                        ((MobileObjectSpyDialog) parentDialog).verifyCapturedElementsStates(elements);
                        break;
                    }
                    case SWT.F2: {
                        if (elements.length == 1) {
                            ((MobileElementInspectorDialog) parentDialog).getPropertiesComposite()
                                    .focusAndEditCapturedElementName();
                        }
                        break;
                    }
                }
            }
        });

        tableIndexColumn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                capturedObjectsTableViewer.checkAllElements(!capturedObjectsTableViewer.isAllElementChecked());
            }
        });
    }
}
