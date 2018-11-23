package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.PlatformUtil;

public class AddTestObjectXpathDialog extends Dialog {

    private String xpath;
    private Text txtValue;
    
    public AddTestObjectXpathDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        GridData gd_container = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_container.widthHint = 450;
        container.setLayoutData(gd_container);
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.marginWidth = 0;
        gl_container.marginHeight = 0;
        container.setLayout(gl_container);

        Label lblXpath = new Label(container, SWT.NONE);
        lblXpath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblXpath.setText(StringConstants.VALUE);

        txtValue = new Text(container, SWT.BORDER);
        txtValue.setLayoutData(platformGridData(new GridData(SWT.FILL, SWT.CENTER, true, false)));
        
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }
    
    private GridData platformGridData(GridData gridData) {
        if (PlatformUtil.isMacOS()) {
            gridData.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        }
        return gridData;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ComposerComponentsImplMessageConstants.VIEW_LBL_ADD_XPATH);
    }
    
    public Point getSize() {
        return getInitialSize();
    }

    @Override
    protected void okPressed() {
    	xpath = txtValue.getText();       
        super.okPressed();
    }
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}
