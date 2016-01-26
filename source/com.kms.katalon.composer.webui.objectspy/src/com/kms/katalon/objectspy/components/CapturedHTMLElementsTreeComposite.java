package com.kms.katalon.objectspy.components;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.tree.HTMLElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;

public class CapturedHTMLElementsTreeComposite extends Composite {

    private TreeViewer elementTreeViewer;

    public CapturedHTMLElementsTreeComposite(Composite parent, int style) {
        super(parent, style);
        
        setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
        GridLayout gl_objectTreeComposite = new GridLayout(1, false);
        gl_objectTreeComposite.marginWidth = 0;
        gl_objectTreeComposite.marginHeight = 0;
        gl_objectTreeComposite.horizontalSpacing = 0;
        setLayout(gl_objectTreeComposite);
        
        Label lblCapturedObjects = new Label(this, SWT.NONE);
        lblCapturedObjects.setFont(getFontBold(lblCapturedObjects));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);

        elementTreeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI);
        elementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        elementTreeViewer.setContentProvider(new HTMLElementTreeContentProvider());
        elementTreeViewer.setLabelProvider(new HTMLElementLabelProvider());
        
        ColumnViewerToolTipSupport.enableFor(elementTreeViewer, ToolTip.NO_RECREATE);
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }
    
    public void refreshElementTree(Object object) {
        elementTreeViewer.getControl().setRedraw(false);
        Object[] expandedElements = elementTreeViewer.getExpandedElements();
        if (object == null) {
            elementTreeViewer.refresh();
        } else {
            elementTreeViewer.refresh(object);
        }
        for (Object element : expandedElements) {
            elementTreeViewer.setExpandedState(element, true);
        }
        elementTreeViewer.getControl().setRedraw(true);
    }
    
    public TreeViewer getElementTreeViewer() {
        return elementTreeViewer;
    }
}
