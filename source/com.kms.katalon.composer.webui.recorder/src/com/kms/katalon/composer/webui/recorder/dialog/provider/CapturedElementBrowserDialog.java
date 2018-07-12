package com.kms.katalon.composer.webui.recorder.dialog.provider;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.webui.recorder.constants.ComposerWebuiRecorderMessageConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.WebElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.WebElementTreeContentProvider;

public class CapturedElementBrowserDialog extends AbstractDialog implements IAstDialogBuilder {

    private TreeViewer treeViewer;

    private List<WebElement> capturedElements;

    private WebElement selectedElement;

    public CapturedElementBrowserDialog(Shell parentShell, List<WebElement> capturedElements,
            WebElement selectedElement) {
        super(parentShell);
        this.capturedElements = capturedElements;
        this.selectedElement = selectedElement;
    }

    @Override
    protected void registerControlModifyListeners() {
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                WebElement webElement = (WebElement) treeViewer.getStructuredSelection().getFirstElement();
                if (webElement != null) {
                    selectedElement = webElement;
                }
                getButton(OK).setEnabled(!(webElement instanceof WebPage));
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void doubleClick(DoubleClickEvent event) {
                WebElement webElement = (WebElement) treeViewer.getStructuredSelection().getFirstElement();
                if (webElement instanceof WebPage) {
                    treeViewer.setExpandedState(webElement, true);
                } else {
                    okPressed();
                }
            }
        });
    }

    @Override
    protected void setInput() {
        treeViewer.setInput(capturedElements);
        if (selectedElement != null) {
            treeViewer.setSelection(new StructuredSelection(selectedElement));
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
        treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        treeViewer.setContentProvider(new WebElementTreeContentProvider());
        treeViewer.setLabelProvider(new WebElementLabelProvider());
        treeViewer.setAutoExpandLevel(2);

        return container;
    }

    @Override
    public Object getReturnValue() {
        return selectedElement;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    @Override
    public String getDialogTitle() {
        return ComposerWebuiRecorderMessageConstants.DIA_TITLE_CAPTURED_OBJECTS;
    }
}
