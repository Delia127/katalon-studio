package com.kms.katalon.zendesk;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.ParseException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.activation.dialog.ActivationDialog;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class ZendeskSubmitTicketDialog extends TitleAreaDialog {
    private static final int MAXIMUM_ATTACHMENT_SIZE = 7;

    private static final String[] ATTACHMENT_FILTER_FILES_EXTENSIONS = new String[] { "*.*" }; //$NON-NLS-1$

    private static final String[] ATTACHMENT_FILTER_FILES_NAMES = new String[] { "All Files (*.*)" }; //$NON-NLS-1$

    private Text textSubject;

    private Text textDescription;

    private ListViewer listAttachments;

    private List<File> attachments = new ArrayList<>();

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public ZendeskSubmitTicketDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Label lblSubject = new Label(container, SWT.NONE);
        lblSubject.setText(MessageConstants.LBL_DLG_SUBJECT);

        textSubject = new Text(container, SWT.BORDER);
        textSubject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        new Label(container, SWT.NONE);

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setText(MessageConstants.LBL_DLG_DESCRIPTION);

        textDescription = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_textDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_textDescription.heightHint = 107;
        textDescription.setLayoutData(gd_textDescription);

        new Label(container, SWT.NONE);

        Label lblAttachments = new Label(container, SWT.NONE);
        lblAttachments.setText(MessageConstants.LBL_DLG_ATTACHMENTS);

        ToolBar attachmentToolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
        attachmentToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        ToolItem tltmAdd = new ToolItem(attachmentToolBar, SWT.NONE);
        tltmAdd.setText(MessageConstants.TOOLITEM_DLG_ADD);
        tltmAdd.setImage(ImageManager.getImage(IImageKeys.ADD_16));
        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addAttachment();
            }
        });

        ToolItem tltmDelete = new ToolItem(attachmentToolBar, SWT.NONE);
        tltmDelete.setText(MessageConstants.TOOLITEM_DLG_DELETE);
        tltmDelete.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
        tltmDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteSelectedAttachments();
            }
        });

        ToolItem tltmClear = new ToolItem(attachmentToolBar, SWT.NONE);
        tltmClear.setText(MessageConstants.TOOLITEM_DLG_CLEAR);
        tltmClear.setImage(ImageManager.getImage(IImageKeys.CLEAR_16));
        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearAttachments();
            }
        });

        listAttachments = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_listAttachments = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_listAttachments.heightHint = 74;
        listAttachments.getList().setLayoutData(gd_listAttachments);
        listAttachments.setContentProvider(new ArrayContentProvider());
        listAttachments.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof File) {
                    return ((File) element).getName();
                }
                return super.getText(element);
            }
        });
        listAttachments.setInput(attachments);

        setMessage(MessageConstants.MSG_DLG_REPORT_ISSUE);
        setTitle(MessageConstants.DLG_TITLE_DLG_REPORT_ISSUE);
        return container;
    }

    private void clearAttachments() {
        attachments.clear();
        listAttachments.refresh();
    }

    private void deleteSelectedAttachments() {
        ISelection selection = listAttachments.getSelection();
        if (!(selection instanceof StructuredSelection)) {
            return;
        }
        StructuredSelection structuredSelection = (StructuredSelection) selection;
        Iterator<?> iterator = structuredSelection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (!(object instanceof File)) {
                continue;
            }
            attachments.remove((File) object);
        }
        listAttachments.refresh();
    }

    private void addAttachment() {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.MULTI);
        fileDialog.setFilterNames(ATTACHMENT_FILTER_FILES_NAMES);
        fileDialog.setFilterExtensions(ATTACHMENT_FILTER_FILES_EXTENSIONS);
        String result = fileDialog.open();
        if (result == null) {
            return;
        }

        List<File> selectedFiles = getSelectedFiles(fileDialog);
        List<File> duplicatedFiles = new ArrayList<>();
        selectedFiles.stream().forEach((selectedFile) -> {
            if (verifySelectedFile(selectedFile)) {
                attachments.add(selectedFile);
            } else {
                duplicatedFiles.add(selectedFile);
            }
        });
        if (!duplicatedFiles.isEmpty()) {
            setErrorMessage(MessageFormat.format(MessageConstants.ERR_MSG_FILES_DUPLICATED_NAMES_X,
                    duplicatedFiles.stream().map(file -> file.getName()).collect(Collectors.joining(", ")))); //$NON-NLS-1$
        }
        listAttachments.refresh();
    }

    private boolean verifySelectedFile(File selectedFile) {
        return !attachments.stream()
                .filter(attachment -> attachment.getName().equals(selectedFile.getName()))
                .findFirst()
                .isPresent();
    }

    // Append all the selected files. Since getFileNames() returns only
    // the names, and not the path, prepend the path, normalizing
    // if necessary
    private static List<File> getSelectedFiles(FileDialog fileDialog) {
        String filterPath = fileDialog.getFilterPath();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : fileDialog.getFileNames()) {
            StringBuffer stringBuilder = new StringBuffer();
            stringBuilder.append(filterPath);
            if (stringBuilder.charAt(stringBuilder.length() - 1) != File.separatorChar) {
                stringBuilder.append(File.separatorChar);
            }
            stringBuilder.append(fileName);
            selectedFiles.add(new File(stringBuilder.toString()));
        }
        return selectedFiles;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = (Composite) super.createButtonBar(parent);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
        composite.setLayoutData(data);
        return composite;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, MessageConstants.BTN_DLG_SUBMIT_TICKET, true);
    }

    @Override
    protected void okPressed() {
        if (textSubject.getText().isEmpty()) {
            setErrorMessage(MessageConstants.ERR_MSG_SUBJECT_CANNOT_BE_BLANK);
            return;
        }
        if (textDescription.getText().isEmpty()) {
            setErrorMessage(MessageConstants.ERR_MSG_DSC_CANNOT_BE_BLANK);
            return;
        }
        if (!verifyAttachmentsSize()) {
            setErrorMessage(MessageFormat.format(MessageConstants.ERR_MSG_ATTACHMENTS_SIZE_MUST_BE_LESS_OR_EQUAL_X_MB,
                    MAXIMUM_ATTACHMENT_SIZE));
            return;
        }
        if (doCreateTicket(textSubject.getText(), textDescription.getText(), attachments)) {
            MessageDialog.openInformation(getShell(), StringConstants.INFO, MessageConstants.MSG_DLG_SUBMIT_ISSUE_SUCCESS);
            super.okPressed();
        }
    }

    private boolean verifyAttachmentsSize() {
        double sizeInMB = attachments.stream().mapToLong(attachment -> attachment.length()).sum() / (1024 * 1024);
        return sizeInMB <= MAXIMUM_ATTACHMENT_SIZE;
    }

    private boolean doCreateTicket(String subject, String description, List<File> attachments) {
        try {
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(MessageConstants.MSG_PRG_SUBMITTING_TICKET, IProgressMonitor.UNKNOWN);
                        ZendeskHTTPRequestHelper.createTicket(subject, description, attachments);
                    } catch (ParseException | IOException | ZendeskRequestException | GeneralSecurityException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return true;
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof EmailNotValidException) {
                return requireActivationAndTryAgain(subject, description, attachments);
            }
            setErrorMessage(targetException.getMessage());
        } catch (InterruptedException e) {
            // ignore this
        }
        return false;
    }

    private boolean requireActivationAndTryAgain(String subject, String description, List<File> attachments) {
        MessageDialog.openError(getShell(), StringConstants.ERROR, MessageConstants.ERR_MSG_MISSING_EMAIL);
        ActivationDialog activationDialog = new ActivationDialog(getShell());
        activationDialog.setAllowOfflineActivation(false);
        if (activationDialog.open() == Window.CANCEL) {
            return false;
        }
        return doCreateTicket(subject, description, attachments);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(489, 482);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConstants.TITLE_DLG_REPORT_ISSUE);
    }

}
