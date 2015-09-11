package com.kms.katalon.composer.keyword.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;

import com.kms.katalon.composer.keyword.constants.StringConstants;

@SuppressWarnings("restriction")
public class NewKeywordWizard extends NewElementWizard {

    private NewKeywordWizardPage page;

    public NewKeywordWizard() {
        super();
        setWindowTitle(StringConstants.WIZ_TITLE_NEW);
        setDefaultPageImageDescriptor(JFaceResources.getImageRegistry().getDescriptor(
                TitleAreaDialog.DLG_IMG_TITLE_BANNER));
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        page = new NewKeywordWizardPage();
        addPage(page);
        page.init(getSelection());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
     */
    @Override
    protected boolean canRunForked() {
        return !page.isEnclosingTypeSelected();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean res = super.performFinish();
        if (res) {
            IResource resource = page.getModifiedResource();
            if (resource != null) {
                // Open the new Keyword class
                selectAndReveal(resource);
                openResource((IFile) resource);
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        // Use progress monitor in page
        page.createType(monitor);
    }

    /**
     * Get created Keyword class
     * 
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
     */
    @Override
    public IJavaElement getCreatedElement() {
        return page.getCreatedType();
    }

}
