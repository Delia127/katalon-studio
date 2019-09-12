package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.constants.DocumentationMessageConstants;

public class NewCheckpointWizardDialog extends WizardDialog {

    private Text messageLabel;

    private Label messageImageLabel;

    private Point textLocation;

    private static final int DIFF_SIZE = 2;

    private static final int H_GAP_IMAGE = 5;

    private ShellAdapter onShellOpenListener;

    @Override
    protected Control createContents(Composite parent) {
        Composite content = (Composite) super.createContents(parent);
        findMessageControl(content);
        getShell().addShellListener(onShellOpenListener);
        return content;
    }

    private void findMessageControl(Composite parent) {

        for (Control control : parent.getChildren()) {
            if (!(control instanceof Text)) {
                continue;
            }
            messageLabel = (Text) control;
            break;
        }
        Object layoutData = messageLabel.getLayoutData();
        if (layoutData instanceof FormData) {
            messageImageLabel = (Label) ((FormData) layoutData).left.control;
        }
    }

    public NewCheckpointWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
        addListeners();
    }

    private void addListeners() {
        addPageChangingListener(new IPageChangingListener() {

            @Override
            public void handlePageChanging(PageChangingEvent e) {
                Shell shell = ((WizardDialog) e.getSource()).getShell();
                if (e.getTargetPage() instanceof AbstractCheckpointWizardPage) {
                    Point pageSize = ((AbstractCheckpointWizardPage) e.getTargetPage()).getPageSize();
                    shell.setSize(pageSize);
                    shell.layout(true, true);
                    return;
                }
                shell.pack(true);
            }
        });
        onShellOpenListener = new ShellAdapter() {

            @Override
            public void shellActivated(ShellEvent e) {
                setTextCenterVerticalWithImage();
            }
        };
    }

    @Override
    protected Point getInitialSize() {
        IWizardPage startingPage = getWizard().getStartingPage();
        if (startingPage instanceof AbstractCheckpointWizardPage) {
            return ((AbstractCheckpointWizardPage) startingPage).getPageSize();
        }
        return super.getInitialSize();
    }

    @Override
    public void setErrorMessage(String newErrorMessage) {
        super.setErrorMessage(newErrorMessage);
        setTextCenterVerticalWithImage();
    }

    @Override
    public void setMessage(String newMessage, int newType) {
        super.setMessage(newMessage, newType);
        setTextCenterVerticalWithImage();
    }

    private void setTextCenterVerticalWithImage() {
        if (messageLabel == null || messageImageLabel == null) {
            return;
        }
        if (textLocation == null) {
            textLocation = messageLabel.getLocation();
        }
        messageLabel.setLocation(messageImageLabel.getLocation().x + DIFF_SIZE + messageImageLabel.getBounds().width + H_GAP_IMAGE, textLocation.y + DIFF_SIZE);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        new HelpCompositeForDialog(parent, DocumentationMessageConstants.NEW_CHECKPOINT) {
            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginBottom = 0;
                return layout;
            }
        };
        super.createButtonsForButtonBar(parent);
    }
}
