package com.kms.katalon.composer.mobile.objectspy.composites;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementInspectorDialog;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementTreeContentProvider;

public class MobileAllObjectsWithCheckboxComposite extends Composite {

    private MobileElementInspectorDialog parentDialog;

    private CheckboxTreeViewer allElementTreeViewer;

    public CheckboxTreeViewer getAllElementTreeViewer() {
        return allElementTreeViewer;
    }

    public MobileAllObjectsWithCheckboxComposite(MobileElementInspectorDialog parentDialog, Composite parent, int style) {
        super(parent, style);
        this.parentDialog = parentDialog;
        this.createComposite();
    }

    public MobileAllObjectsWithCheckboxComposite(MobileElementInspectorDialog parentDialog, Composite parent) {
        this(parentDialog, parent, SWT.NONE);
    }
    
    private void createComposite() {
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        setLayout(new GridLayout());

        Label lblAllObjects = new Label(this, SWT.NONE);
        lblAllObjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        ControlUtils.setFontToBeBold(lblAllObjects);
        lblAllObjects.setText(StringConstants.DIA_LBL_ALL_OBJECTS);

        allElementTreeViewer = new CheckboxTreeViewer(this,
                SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI) {
            @Override
            public boolean setSubtreeChecked(Object element, boolean state) {
                Widget widget = internalExpand(element, false);
                if (widget instanceof TreeItem) {
                    TreeItem item = (TreeItem) widget;
                    item.setChecked(state);
                    return true;
                }
                return false;
            }
        };
        Tree tree = allElementTreeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        allElementTreeViewer.setLabelProvider(new MobileElementLabelProvider());
        allElementTreeViewer.setContentProvider(new MobileElementTreeContentProvider());

        tree.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(allElementTreeViewer, ToolTip.NO_RECREATE);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) {
                    return;
                }
                Point pt = new Point(e.x, e.y);
                TreeItem item = tree.getItem(pt);
                if (item != null) {
                    parentDialog.highlightElement((MobileElement) item.getData());
                }
            }
        });

        allElementTreeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                TreeMobileElement selectedElement = (TreeMobileElement) event.getElement();
                if (event.getChecked()) {
                    parentDialog.getCapturedObjectsComposite().addElement(selectedElement.newCapturedElement());
                    parentDialog.getPropertiesComposite().focusAndEditCapturedElementName();
                } else {
                    CapturedMobileElement capturedElement = selectedElement.getCapturedElement();
                    if (parentDialog.getCapturedObjectsComposite().containsElement(capturedElement)) {
                        parentDialog.getCapturedObjectsComposite().removeElement(capturedElement);
                        selectedElement.setCapturedElement(null);
                        parentDialog.getPropertiesComposite().setEditingElement(null);
                    }
                }
                allElementTreeViewer.refresh(selectedElement);
            }
        });
    }
}
