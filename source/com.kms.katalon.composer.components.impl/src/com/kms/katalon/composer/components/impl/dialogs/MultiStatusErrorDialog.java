package com.kms.katalon.composer.components.impl.dialogs;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;

public class MultiStatusErrorDialog extends AbstractDialog {

    private static final int DF_HEIGHT_HINT_FOR_DETAILS_COMPOSITE = 150;

    private Label lblTitle;

    private Label lblDescription;

    private String title;

    private String errorDescription;

    private String detailsMessage;

    private GridData detailsCompositeGridData;

    private Composite mainComposite;

    private StyledText txtDetails;
    
    private boolean detailsShowed;

    private Composite detailsComposite;

    private MultiStatusErrorDialog(Shell parentShell, String errorMessage, String errorReason, String detailsMessage) {
        super(parentShell);
        this.title = errorMessage;
        this.errorDescription = errorReason;
        this.detailsMessage = detailsMessage;
        
        detailsShowed = false;
    }

    @Override
    protected Control createContents(Composite parent) {
        mainComposite = (Composite) super.createContents(parent);
       
        createDetailsComposite(mainComposite);
        return mainComposite;
    }
    
    private void createDetailsComposite(Composite parentComposite) {
        detailsComposite = new Composite(parentComposite, SWT.NONE);
        detailsComposite.setLayout(new GridLayout(1, false));
        
        detailsCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        detailsCompositeGridData.heightHint = DF_HEIGHT_HINT_FOR_DETAILS_COMPOSITE;
        detailsComposite.setLayoutData(detailsCompositeGridData);
        
        txtDetails = new StyledText(detailsComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
        txtDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtDetails.setEditable(false);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
        Composite imageComposite = new Composite(composite, SWT.NONE);
        imageComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        imageComposite.setLayout(new GridLayout(1, false));
        
        Label lblImage = new Label(imageComposite, SWT.NONE);
        lblImage.setImage(getShell().getDisplay().getSystemImage(SWT.ICON_WARNING));

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.marginHeight = 0;
        glContainer.marginWidth = 0;
        container.setLayout(glContainer);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        lblTitle = new Label(container, SWT.WRAP);
        lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblReasonTitle = new Label(container, SWT.NONE);
        lblReasonTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblReasonTitle.setText(StringConstants.DIA_TITLE_REASON);

        lblDescription = new Label(container, SWT.WRAP);
        lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        return composite;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtDetails.addListener(SWT.Resize, ControlUtils.getAutoHideStyledTextScrollbarListener);

        getButton(IDialogConstants.DETAILS_ID).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                detailsShowed = !detailsShowed;
                updateDetailsCompositeAndButton();
            }
        });
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.WARN;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.DETAILS_ID,
                getDetailsButtonText(), false);
    }

    @Override
    protected void setInput() {
        lblTitle.setText(StringUtils.defaultString(title));
        lblDescription.setText(StringUtils.defaultString(errorDescription));
        txtDetails.setText(StringUtils.defaultString(detailsMessage));
        updateDetailsCompositeAndButton();
    }

    private void updateDetailsCompositeAndButton() {
        detailsComposite.setVisible(detailsShowed);
        detailsCompositeGridData.exclude = !detailsShowed;

        String detailsButtonText = getDetailsButtonText();
        getButton(IDialogConstants.DETAILS_ID).setText(detailsButtonText);
        
        updateDialogSize();
    }
    
    private void updateDialogSize() {
        Point currentSize = getShell().getSize();
        int detailsHeight = detailsComposite.getSize().y;
        int newY = detailsShowed ? currentSize.y + detailsHeight : currentSize.y - detailsHeight;
        getShell().setSize(currentSize.x, newY);
    }

    private String getDetailsButtonText() {
        String detailsButtonText = StringConstants.DIA_TITLE_DETAILS;
        return detailsShowed ? "<< " + detailsButtonText : detailsButtonText + " >>";
    }

    public static void showErrorDialog(Throwable e, String title, String errorDescription) {
        MultiStatusErrorDialog dialog = new MultiStatusErrorDialog(Display.getCurrent().getActiveShell(), title,
                errorDescription, ExceptionUtils.getFullStackTrace(e));
        dialog.open();
    }

    public static void showErrorDialog(String message, String reason, String details) {
        MultiStatusErrorDialog dialog = new MultiStatusErrorDialog(Display.getCurrent().getActiveShell(), message,
                reason, details);
        dialog.open();
    }
}
