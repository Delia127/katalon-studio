package com.kms.katalon.composer.execution.dialog;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.execution.constants.StringConstants;

public class MapPropertyValueBuilderDialog extends Dialog {
    private Map<String, Object> driverPropertyList;

    public MapPropertyValueBuilderDialog(Shell parentShell) {
        super(parentShell);
        driverPropertyList = new LinkedHashMap<String, Object>();
    }

    public MapPropertyValueBuilderDialog(Shell parentShell, Map<String, Object> driverPropertyList) {
        super(parentShell);
        this.driverPropertyList = new LinkedHashMap<String, Object>(driverPropertyList);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        DriverPropertyMapComposite control = new DriverPropertyMapComposite(container);
        control.setInput(driverPropertyList);
        return container;
    }

    public Map<String, Object> getPropertyMap() {
        return driverPropertyList;
    }
  
    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_MAP_PROPERTY_VALUE_NAME);
    }
}
