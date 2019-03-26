package com.kms.katalon.composer.intro;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.execution.constants.StringConstants;

public class QuickStartDialog extends Dialog {
    
    public static final int NEW_PROJECT_ID = 1025;
    
    public static final int OPEN_PROJECT_ID = 1026;

    private static final Point IMG_SIZE = new Point(960, 610);

    public QuickStartDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void initializeBounds() {
        super.initializeBounds();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        org.eclipse.swt.layout.GridLayout containerLayout = new GridLayout();
        containerLayout.marginWidth = 0;
        containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);
        
        Composite imageComposite = new Composite(container, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = IMG_SIZE.x;
        gridData.heightHint = IMG_SIZE.y;
        imageComposite.setLayoutData(gridData);
        
        imageComposite.setBackgroundImage(ImageConstants.IMG_INTRO_SCREEN_WELCOME);

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, OPEN_PROJECT_ID, StringConstants.DIA_OPEN_PROJECT, false);
        createButton(parent, NEW_PROJECT_ID, StringConstants.DIA_NEW_PROJECT, true);
    }
    
    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

    @Override
    public void create() {
        super.create();
    }
}
