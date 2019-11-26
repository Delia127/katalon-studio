package com.kms.katalon.composer.mobile.recorder.composites;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementInspectorDialog;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementTreeContentProvider;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;

public class MobileAllObjectsComposite extends Composite {

    private MobileElementInspectorDialog parentDialog;

    private TreeViewer allElementTreeViewer;

    public TreeViewer getAllElementTreeViewer() {
        return allElementTreeViewer;
    }

    public MobileAllObjectsComposite(MobileElementInspectorDialog parentDialog, Composite parent, int style) {
        super(parent, style);
        this.parentDialog = parentDialog;
        this.createComposite(parent);
    }

    public MobileAllObjectsComposite(MobileElementInspectorDialog parentDialog, Composite parent) {
        this(parentDialog, parent, SWT.NONE);
    }
    
    private void createComposite(Composite parent) {
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        setLayout(new GridLayout());
        
        createCompositeLabel(this);
        createAllObjectsTreeComposite(this);
    }

    private void createCompositeLabel(Composite parent) {
        Label lblAllObjects = new Label(parent, SWT.NONE);
        lblAllObjects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ControlUtils.setFontToBeBold(lblAllObjects);
        lblAllObjects.setText(MobileRecoderMessagesConstants.LBL_ALL_OBJECTS);
    }
    
    private void createAllObjectsTreeComposite(Composite parent) {
        Composite allObjectsTreeComposite = new Composite(parent, SWT.NONE);
        allObjectsTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        allObjectsTreeComposite.setLayout(new GridLayout(1, false));

        createAllObjectsTreeViewer(allObjectsTreeComposite);
    }
    
    private void createAllObjectsTreeViewer(Composite allObjectsTreeComposite) {
        allElementTreeViewer = new CTreeViewer(allObjectsTreeComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        allElementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        allElementTreeViewer.setLabelProvider(new MobileElementLabelProvider());
        allElementTreeViewer.setContentProvider(new MobileElementTreeContentProvider());

        allElementTreeViewer.getTree().setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(allElementTreeViewer, ToolTip.NO_RECREATE);

        allElementTreeViewer.getTree().addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) {
                    return;
                }
                Point pt = new Point(e.x, e.y);
                TreeItem item = allElementTreeViewer.getTree().getItem(pt);
                if (item != null) {
                    TreeMobileElement treeSnapshotItem = (TreeMobileElement) item.getData();
                    parentDialog.setSelectedElement(treeSnapshotItem);
                }
            }
        });

        Tree tree = (Tree) allElementTreeViewer.getControl();

        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                TreeItem treeItem = (TreeItem) event.item;
                final TreeColumn[] treeColumns = treeItem.getParent().getColumns();
                
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        for (TreeColumn treeColumn : treeColumns) {
                            treeColumn.pack();
                        }
                    }
                });
            }
        };

        tree.addListener(SWT.Expand, listener);
    }
    
    public void setInput(TreeMobileElement rootElement) {
        allElementTreeViewer.setInput(new Object[] { rootElement });
        allElementTreeViewer.refresh();
        allElementTreeViewer.expandAll();
    }
    
    public void focusToElementsTree() {
        allElementTreeViewer.getTree().setFocus();
    }

    public void setSelection(TreeMobileElement selection) {
        allElementTreeViewer.setSelection(selection != null
                ? new StructuredSelection(selection)
                : StructuredSelection.EMPTY);
    }

    public void clearAllSelections() {
        allElementTreeViewer.setSelection(StructuredSelection.EMPTY);
    }
    
    public void clearAllElements() {
        allElementTreeViewer.setInput(new Object[] {});
        allElementTreeViewer.refresh();
    }
    
    public void refreshTree() {
        allElementTreeViewer.refresh();
    }
    
    public void refreshTree(TreeMobileElement element) {
        allElementTreeViewer.refresh(element);
    }

    public TreeMobileElement getSelectedElement() {
    	return (TreeMobileElement) allElementTreeViewer.getStructuredSelection().getFirstElement();
    }
}
