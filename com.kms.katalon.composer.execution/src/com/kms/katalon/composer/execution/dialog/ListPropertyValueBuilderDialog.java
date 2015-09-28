package com.kms.katalon.composer.execution.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.components.DriverPropertyListComposite;
import com.kms.katalon.composer.execution.constants.StringConstants;

public class ListPropertyValueBuilderDialog extends Dialog {
    private List<Object> valueList;

    public ListPropertyValueBuilderDialog(Shell parentShell) {
        super(parentShell);
        valueList = new ArrayList<Object>();
    }

    public ListPropertyValueBuilderDialog(Shell parentShell, List<Object> driverPropertyList) {
        super(parentShell);
        this.valueList = new ArrayList<Object>(driverPropertyList);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        DriverPropertyListComposite control = new DriverPropertyListComposite(container);
        control.setInput(valueList);
        return container;
    }

    public List<Object> getPropertyList() {
        return valueList;
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_LIST_PROPERTY_VALUE_NAME);
    }
}
