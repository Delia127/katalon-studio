package com.kms.katalon.composer.keyword.wizard;

import org.codehaus.groovy.eclipse.core.model.GroovyRuntime;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.codehaus.jdt.groovy.model.GroovyNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class NewKeywordWizardPage extends NewTypeWizardPage {
    private final static String PAGE_NAME = NewKeywordWizardPage.class.getSimpleName(); //$NON-NLS-1$

    private IStatus status;

    public NewKeywordWizardPage() {
        super(true, PAGE_NAME);
        setTitle(StringConstants.KEYWORD);
        setDescription(StringConstants.WIZ_DESC_CREATE_KEYWORD);
    }

    /**
     * The wizard owning this page is responsible for calling this method with the current selection. The selection is
     * used to initialize the fields of the wizard page.
     *
     * @param selection used to initialize the fields
     */
    public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        doStatusUpdate();
    }

    private void doStatusUpdate() {
        // status of all used components
        IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus, fTypeNameStatus };

        // the mode severe status will be displayed and the OK button enabled/disabled.
        updateStatus(status);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#handleFieldChanged(java.lang.String)
     */
    @Override
    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        doStatusUpdate();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        int nColumns = 4;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        createPackageControls(composite, nColumns);
        createTypeNameControls(composite, nColumns);

        setControl(composite);
        Dialog.applyDialogFont(composite);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewElementWizardPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) setFocus();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#getCompilationUnitName(java.lang.String)
     */
    @Override
    protected String getCompilationUnitName(String typeName) {
        return typeName + GroovyConstants.GROOVY_FILE_EXTENSION;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#typeNameChanged()
     */
    @Override
    protected IStatus typeNameChanged() {
        StatusInfo status = (StatusInfo) super.typeNameChanged();
        IPackageFragment pack = getPackageFragment();
        if (pack == null) {
            return status;
        }

        IJavaProject project = pack.getJavaProject();
        String typeName = getTypeNameWithoutParameters();
        // must not exist as a .groovy file
        if (!isEnclosingTypeSelected() && (status.getSeverity() < IStatus.ERROR)) {
            if (pack != null) {
                IType type = null;
                try {
                    type = project.findType(pack.getElementName(), typeName);
                } catch (JavaModelException e) {
                    // can ignore
                }
                if (type != null && type.getPackageFragment().equals(pack)) {
                    status.setError(StringConstants.WIZ_ERROR_MSG_KEYWORD_ALREADY_EXISTS);
                }
            }
        }

        // lastly, check exclusion filters to see if Groovy files are allowed in
        // the source folder
        if (status.getSeverity() < IStatus.ERROR) {
            try {
                ClasspathEntry entry = (ClasspathEntry) ((IPackageFragmentRoot) pack.getParent())
                        .getRawClasspathEntry();
                if (entry != null) {
                    char[][] inclusionPatterns = entry.fullInclusionPatternChars();
                    char[][] exclusionPatterns = entry.fullExclusionPatternChars();
                    if (Util.isExcluded(pack.getResource().getFullPath().append(getCompilationUnitName(typeName)),
                            inclusionPatterns, exclusionPatterns, false)) {
                        status.setError(StringConstants.WIZ_ERROR_MSG_CANNOT_CREATE_KEYWORD_BC_OF_EXCLUSION_PATTERNS);
                    }

                }
            } catch (JavaModelException e) {
                status.setError(e.getLocalizedMessage());
                LoggerSingleton.logError(e, StringConstants.WIZ_ERROR_MSG_EXCEPTION_INSIDE_KEYWORD_WIZ);
            }
        }

        return status;
    }

    @Override
    public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
        IPackageFragment pack = getPackageFragment();
        if (pack != null) {
            IProject project = pack.getJavaProject().getProject();
            if (!GroovyNature.hasGroovyNature(project)) {
                // add groovy nature
                GroovyRuntime.addGroovyNature(project);
            }
        }

        super.createType(monitor);
        monitor = new SubProgressMonitor(monitor, 1);

        GroovyCompilationUnit unit = (GroovyCompilationUnit) pack
                .getCompilationUnit(getCompilationUnitName(getTypeName()));
        try {
            monitor.beginTask("Remove semi-colons", 1);
            unit.becomeWorkingCopy(new SubProgressMonitor(monitor, 1));

            // remove ';' on package declaration
            IPackageDeclaration[] packs = unit.getPackageDeclarations();
            char[] contents = unit.getContents();
            MultiTextEdit multi = new MultiTextEdit();
            if (packs.length > 0) {
                ISourceRange range = packs[0].getSourceRange();
                int position = range.getOffset() + range.getLength();
                if (contents[position] == ';') {
                    multi.addChild(new ReplaceEdit(position, 1, ""));
                }
            }

            if (multi.hasChildren()) {
                unit.applyTextEdit(multi, new SubProgressMonitor(monitor, 1));
                unit.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
            }
            monitor.worked(1);
        } finally {
            if (unit != null) {
                unit.discardWorkingCopy();
            }
            monitor.done();
        }
    }

    private String getTypeNameWithoutParameters() {
        String typeNameWithParameters = getTypeName();
        int angleBracketOffset = typeNameWithParameters.indexOf('<');
        if (angleBracketOffset == -1) {
            return typeNameWithParameters;
        } else {
            return typeNameWithParameters.substring(0, angleBracketOffset);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#getModifiers()
     */
    @Override
    public int getModifiers() {
        return F_PUBLIC;
    }

    /**
     * Retrieve the current status, as last set by updateStatus.
     */
    public IStatus getStatus() {
        return status;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewElementWizardPage#updateStatus(org.eclipse.core.runtime.IStatus)
     */
    @Override
    protected void updateStatus(IStatus status) {
        msgFilter(status);
        super.updateStatus(status);
        this.status = status;
    }

    private void msgFilter(IStatus status) {
        if (status == null || status.getMessage() == null) {
            return;
        }
        String msg = status.getMessage().replaceAll(" Java", "").replaceAll("(?i)type", StringConstants.KEYWORD);
        if (((StatusInfo) status).isError()) {
            ((StatusInfo) status).setError(msg);
        } else if (((StatusInfo) status).isWarning()) {
            ((StatusInfo) status).setWarning(msg);
        }
    }

}
