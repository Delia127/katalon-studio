package com.kms.katalon.composer.mobile.recorder.components;

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
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;

public class CapturedMobileElementBrowserDialog extends AbstractDialog implements IAstDialogBuilder {

    private CTableViewer tableViewer;

    private List<CapturedMobileElement> capturedElements;

    private CapturedMobileElement selectedElement;

    public CapturedMobileElementBrowserDialog(Shell parentShell, List<CapturedMobileElement> capturedElements,
            CapturedMobileElement selectedElement) {
        super(parentShell);
        this.capturedElements = capturedElements;
        this.selectedElement = selectedElement;
    }

    @Override
    protected void registerControlModifyListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                CapturedMobileElement webElement = (CapturedMobileElement) tableViewer.getStructuredSelection()
                        .getFirstElement();
                if (webElement != null) {
                    selectedElement = webElement;
                }
                getButton(OK).setEnabled(webElement instanceof CapturedMobileElement);
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
        tableViewer.setLabelProvider(new MobileElementLabelProvider(0));

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

    private class MobileElementLabelProvider extends TypeCheckStyleCellTableLabelProvider<CapturedMobileElement> {

        public MobileElementLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        protected Class<CapturedMobileElement> getElementType() {
            return CapturedMobileElement.class;
        }

        @Override
        protected Image getImage(CapturedMobileElement element) {
            return null;
        }

        @Override
        protected String getText(CapturedMobileElement element) {
            return element.getName();
        }

    }
}
