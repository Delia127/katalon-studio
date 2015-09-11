package com.kms.katalon.composer.keyword.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.wizards.NewContainerWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.constants.StringConstants;

/**
 * Custom new package wizard page
 * 
 * @see org.eclipse.jdt.ui.wizards.NewPackageWizardPage
 *
 */
@SuppressWarnings("restriction")
public class CNewPackageWizardPage extends NewContainerWizardPage {

    private static final String PAGE_NAME = CNewPackageWizardPage.class.getSimpleName(); //$NON-NLS-1$

    private static final String PACKAGE = PAGE_NAME + ".package"; //$NON-NLS-1$

    private StringDialogField packageDialogField;

    private IPackageFragment createdPackageFragment;

    /** Status of last validation of the package field */
    private IStatus status;

    public CNewPackageWizardPage() {
        super(PAGE_NAME);
        setTitle(StringConstants.WIZ_TITLE_KEYWORD_PACKAGE);
        setDescription(StringConstants.WIZ_DESC_CREATE_KEYWORD_PACKAGE);

        createdPackageFragment = null;
        PackageFieldAdapter adapter = new PackageFieldAdapter();

        packageDialogField = new StringDialogField();
        packageDialogField.setDialogFieldListener(adapter);
        packageDialogField.setLabelText(NewWizardMessages.NewPackageWizardPage_package_label);

        status = new StatusInfo();
    }

    // -------- Initialization ---------

    /**
     * The wizard owning this page is responsible for calling this method with the current selection. The selection is
     * used to initialize the fields of the wizard page.
     *
     * @param selection used to initialize the fields
     */
    public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);

        initContainerPage(jelem);
        String pName = ""; //$NON-NLS-1$
        if (jelem != null) {
            IPackageFragment pf = (IPackageFragment) jelem.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
            if (pf != null && !pf.isDefaultPackage()) {
                pName = pf.getElementName();
            } else {
                if (jelem.getJavaProject() != null) {
                    final IPackageFragmentRoot pkgFragmentRoot = getPackageFragmentRoot();
                    if (pkgFragmentRoot != null && pkgFragmentRoot.exists()) {
                        try {
                            IJavaElement[] packages = pkgFragmentRoot.getChildren();
                            if (packages.length == 1) { // only default package
                                String prName = jelem.getJavaProject().getElementName();
                                IStatus status = getPackageStatus(prName);
                                if (status.getSeverity() == IStatus.OK) {
                                    pName = prName;
                                }
                            }
                        } catch (JavaModelException e) {
                            // fall through
                        }
                    }
                }
            }
        }
        setPackageText(pName, true);

        updateStatus(new IStatus[] { fContainerStatus, status });
    }

    // -------- UI Creation ---------

    /*
     * @see WizardPage#createControl
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        int nColumns = 3;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        createPackageControls(composite, nColumns);

        setControl(composite);
        Dialog.applyDialogFont(composite);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setFocus();
        }
    }

    /**
     * Sets the focus to the package name input field.
     */
    protected void setFocus() {
        packageDialogField.setFocus();
    }

    private void createPackageControls(Composite composite, int nColumns) {
        packageDialogField.doFillIntoGrid(composite, nColumns - 1);
        Text text = packageDialogField.getTextControl(null);
        LayoutUtil.setWidthHint(text, getMaxFieldWidth());
        LayoutUtil.setHorizontalGrabbing(text);
        DialogField.createEmptySpace(composite);
        TextFieldNavigationHandler.install(text);
        BidiUtils.applyBidiProcessing(packageDialogField.getTextControl(null), BidiUtils.AUTO);
    }

    // -------- PackageFieldAdapter --------

    private class PackageFieldAdapter implements IDialogFieldListener {

        // --------- IDialogFieldListener

        public void dialogFieldChanged(DialogField field) {
            status = getPackageStatus(getPackageText());
            // tell all others
            handleFieldChanged(PACKAGE);
        }
    }

    // -------- update message ----------------

    /*
     * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#handleFieldChanged(String)
     */
    @Override
    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        if (fieldName == CONTAINER) {
            status = getPackageStatus(getPackageText());
        }
        // do status line update
        updateStatus(new IStatus[] { fContainerStatus, status });
    }

    // ----------- validation ----------

    private IStatus validatePackageName(String text) {
        IJavaProject project = getJavaProject();
        if (project == null || !project.exists()) {
            return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
        }
        return JavaConventionsUtil.validatePackageName(text, project);
    }

    /**
     * Validates the package name and returns the status of the validation.
     * 
     * @param packName the package name
     * 
     * @return the status of the validation
     */
    private IStatus getPackageStatus(String packName) {
        StatusInfo status = new StatusInfo();
        if (packName.length() > 0) {
            IStatus val = validatePackageName(packName);
            if (val.getSeverity() == IStatus.ERROR) {
                status.setError(Messages.format(NewWizardMessages.NewPackageWizardPage_error_InvalidPackageName, val
                        .getMessage().replaceAll("Java", StringConstants.KEYWORD)));
                return status;
            } else if (val.getSeverity() == IStatus.WARNING) {
                status.setWarning(Messages.format(
                        NewWizardMessages.NewPackageWizardPage_warning_DiscouragedPackageName, val.getMessage()));
            }
        } else {
            status.setError(NewWizardMessages.NewPackageWizardPage_error_EnterName);
            return status;
        }

        IPackageFragmentRoot root = getPackageFragmentRoot();
        if (root != null && root.getJavaProject().exists()) {
            IPackageFragment pack = root.getPackageFragment(packName);
            try {
                IPath rootPath = root.getPath();
                IPath outputPath = root.getJavaProject().getOutputLocation();
                if (rootPath.isPrefixOf(outputPath) && !rootPath.equals(outputPath)) {
                    // if the bin folder is inside of our root, don't allow to name a package
                    // like the bin folder
                    IPath packagePath = pack.getPath();
                    if (outputPath.isPrefixOf(packagePath)) {
                        status.setError(NewWizardMessages.NewPackageWizardPage_error_IsOutputFolder);
                        return status;
                    }
                }
                if (pack.exists()) {
                    if (pack.containsJavaResources() || !pack.hasSubpackages()) {
                        status.setError(NewWizardMessages.NewPackageWizardPage_error_PackageExists);
                    } else {
                        status.setError(NewWizardMessages.NewPackageWizardPage_error_PackageNotShown);
                    }
                } else {
                    IResource resource = pack.getResource();
                    if (resource != null && !ResourcesPlugin.getWorkspace().validateFiltered(resource).isOK()) {
                        status.setError(NewWizardMessages.NewPackageWizardPage_error_PackageNameFiltered);
                        return status;
                    }
                    URI location = pack.getResource().getLocationURI();
                    if (location != null) {
                        IFileStore store = EFS.getStore(location);
                        if (store.fetchInfo().exists()) {
                            status.setError(NewWizardMessages.NewPackageWizardPage_error_PackageExistsDifferentCase);
                        }
                    }
                }
            } catch (CoreException e) {
                LoggerSingleton.logError(e);
            }
        }
        return status;
    }

    /**
     * Returns the content of the package input field.
     *
     * @return the content of the package input field
     */
    public String getPackageText() {
        return packageDialogField.getText();
    }

    /**
     * Sets the content of the package input field to the given value.
     *
     * @param str the new package input field text
     * @param canBeModified if <code>true</code> the package input field can be modified; otherwise it is read-only.
     */
    public void setPackageText(String str, boolean canBeModified) {
        packageDialogField.setText(str);
        packageDialogField.setEnabled(canBeModified);
    }

    /**
     * Returns the resource handle that corresponds to the element that was created or will be created.
     * 
     * @return A resource or null if the page contains illegal values.
     * @since 3.0
     */
    public IResource getModifiedResource() {
        IPackageFragmentRoot root = getPackageFragmentRoot();
        if (root != null) {
            IPackageFragment pack = root.getPackageFragment(getPackageText());
            IResource packRes = pack.getResource();
            return packRes;
        }
        return null;
    }

    // ---- creation ----------------

    /**
     * Returns a runnable that creates a package using the current settings.
     *
     * @return the runnable that creates the new package
     */
    public IRunnableWithProgress getRunnable() {
        return new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    createPackage(monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }
            }
        };
    }

    /**
     * Returns the created package fragment. This method only returns a valid value after <code>getRunnable</code> or
     * <code>createPackage</code> have been executed.
     *
     * @return the created package fragment
     */
    public IPackageFragment getNewPackageFragment() {
        return createdPackageFragment;
    }

    /**
     * Creates the new package using the entered field values.
     *
     * @param monitor a progress monitor to report progress. The progress monitor must not be <code>null</code>
     * @throws CoreException Thrown if creating the package failed.
     * @throws InterruptedException Thrown when the operation has been canceled.
     * @since 2.1
     */
    public void createPackage(IProgressMonitor monitor) throws CoreException, InterruptedException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        IPackageFragmentRoot root = getPackageFragmentRoot();
        IPackageFragment pack = root.getPackageFragment(getPackageText());

        if (pack.exists()) {
            createdPackageFragment = pack;
        } else {
            createdPackageFragment = root.createPackageFragment(getPackageText(), true, monitor);
        }

        // save whether package documentation should be created
        IDialogSettings dialogSettings = getDialogSettings();
        if (dialogSettings != null) {
            IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
            if (section == null) {
                section = dialogSettings.addNewSection(PAGE_NAME);
            }
        }

        if (monitor.isCanceled()) {
            throw new InterruptedException();
        }
    }

}
