package com.kms.katalon.composer.keyword.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

import com.kms.katalon.composer.keyword.constants.StringConstants;

@SuppressWarnings("restriction")
public class NewPackageWizard extends NewElementWizard {

    private CNewPackageWizardPage page;

    public NewPackageWizard() {
        this(null);
    }

    public NewPackageWizard(CNewPackageWizardPage page) {
        super();
        setWindowTitle(StringConstants.WIZ_TITLE_NEW);
        setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWPACK);
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());

        this.page = page;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        if (page == null) {
            page = new CNewPackageWizardPage();
            page.setWizard(this);
            page.init(getSelection());
        }
        addPage(page);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        page.createPackage(monitor); // use the full progress monitor
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean res = super.performFinish();
        if (res) {
            IResource resource = page.getModifiedResource();
            selectAndReveal(resource);
        }
        return res;
    }

    /**
     * Get created package
     * 
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
     */
    @Override
    public IJavaElement getCreatedElement() {
        return page.getNewPackageFragment();
    }

}
