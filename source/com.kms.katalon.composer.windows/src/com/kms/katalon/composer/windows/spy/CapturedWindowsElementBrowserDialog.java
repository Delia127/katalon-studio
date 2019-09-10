package com.kms.katalon.composer.windows.spy;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public class CapturedWindowsElementBrowserDialog extends AbstractDialog implements IAstDialogBuilder {

    private CTableViewer tableViewer;

    private List<CapturedWindowsElement> capturedElements;

    private CapturedWindowsElement selectedElement;

    public CapturedWindowsElementBrowserDialog(Shell parentShell, List<CapturedWindowsElement> capturedElements,
            CapturedWindowsElement selectedElement) {
        super(parentShell);
        this.capturedElements = capturedElements;
        this.selectedElement = selectedElement;
    }

    @Override
    protected void registerControlModifyListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                CapturedWindowsElement webElement = (CapturedWindowsElement) tableViewer.getStructuredSelection()
                        .getFirstElement();
                if (webElement != null) {
                    selectedElement = webElement;
                }
                getButton(OK).setEnabled(webElement instanceof CapturedWindowsElement);
            }
        });
    }

    @Override
    protected void setInput() {
        tableViewer.setInput(capturedElements);
        if (selectedElement != null) {
            tableViewer.setSelection(new StructuredSelection(selectedElement));
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        tableViewer = new CTableViewer(container, SWT.BORDER | SWT.MULTI);
        tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setLabelProvider(new WindowsElementLabelProvider(0));

        return container;
    }

    @Override
    public Object getReturnValue() {
        return selectedElement;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 400);
    }

    @Override
    public String getDialogTitle() {
        return "Captured Windows Object";
    }

    private class WindowsElementLabelProvider extends TypeCheckStyleCellTableLabelProvider<CapturedWindowsElement> {

        public WindowsElementLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected Class<CapturedWindowsElement> getElementType() {
            return CapturedWindowsElement.class;
        }

        @Override
        protected Image getImage(CapturedWindowsElement element) {
            return null;
        }

        @Override
        protected String getText(CapturedWindowsElement element) {
            return element.getName();
        }

    }
}
