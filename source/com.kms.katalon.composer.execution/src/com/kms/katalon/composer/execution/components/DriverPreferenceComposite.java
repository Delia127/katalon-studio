package com.kms.katalon.composer.execution.components;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.execution.configuration.IDriverConnector;

public class DriverPreferenceComposite extends Composite {
    protected Map<String, Object> driverProperties;

    protected DriverPropertyMapComposite driverPropertyMapComposite;
    
    protected IDriverConnector driverConnector;
    
    public DriverPreferenceComposite(Composite parent, int style, IDriverConnector driverConnector) {
        super(parent, style);
        this.driverConnector = driverConnector;
        createContents(driverConnector);
        if (driverConnector != null) {
            driverProperties = driverConnector.getUserConfigProperties();
        } else {
            driverProperties = new LinkedHashMap<String, Object>();
        }
        setInput(driverProperties);
    }

    protected void createContents(IDriverConnector driverConnector) {
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));
        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
    }
    
    public void setInput(Map<String, Object> driverProperties) {
        this.driverProperties = driverProperties;
        driverPropertyMapComposite.setInput(driverProperties);
    }

    public IDriverConnector getResult() {
        driverConnector.setUserConfigProperties(driverProperties);
        return driverConnector;
    }

}
