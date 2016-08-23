package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.explorer.handlers.RefreshHandler;

@SuppressWarnings("restriction")
public class CustomCreateBranchWizard extends Wizard {
    private String newBranchName;

    private CustomCreateBranchPage myPage;

    /**
     * @param repository
     *            the repository
     */
    public CustomCreateBranchWizard(Repository repository) {
        this(repository, null);
    }

    /**
     * @param repository
     *            the repository
     * @param base
     *            a {@link Ref} name or {@link RevCommit} id, or null
     */
    public CustomCreateBranchWizard(Repository repository, String base) {
        try (RevWalk rw = new RevWalk(repository)) {
            if (base == null) {
                myPage = new CustomCreateBranchPage(repository, (Ref) null);
            } else if (ObjectId.isId(base)) {
                RevCommit commit = rw.parseCommit(ObjectId
                        .fromString(base));
                myPage = new CustomCreateBranchPage(repository, commit);
            } else {
                if (base.startsWith(Constants.R_HEADS)
                        || base.startsWith(Constants.R_REMOTES)
                        || base.startsWith(Constants.R_TAGS)) {
                    Ref currentBranch = repository.exactRef(base);
                    myPage = new CustomCreateBranchPage(repository, currentBranch);
                } else {
                    // the page only knows some special Refs
                    RevCommit commit = rw.parseCommit(
                            repository.resolve(base + "^{commit}")); //$NON-NLS-1$
                    myPage = new CustomCreateBranchPage(repository, commit);
                }
            }
        } catch (IOException e) {
            // simply don't select the drop down
            myPage = new CustomCreateBranchPage(repository, (Ref) null);
        }
        setWindowTitle(UIText.CreateBranchWizard_NewBranchTitle);
    }

    @Override
    public void addPages() {
        addPage(myPage);
    }

    @Override
    public boolean performFinish() {
        final CustomCreateBranchPage cp = (CustomCreateBranchPage) getPages()[0];
        newBranchName = cp.getBranchName();
        final boolean checkoutNewBranch = cp.checkoutNewBranch();
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    try {
                        cp.createBranch(newBranchName, checkoutNewBranch,
                                monitor);
                    } catch (CoreException ce) {
                        throw new InvocationTargetException(ce);
                    } catch (IOException ioe) {
                        throw new InvocationTargetException(ioe);
                    }
                }
            });
            UISynchronizeService.syncExec(new Runnable() {
                @Override
                public void run() {
                    new RefreshHandler().execute();
                }
            });
        } catch (InvocationTargetException ite) {
            Activator.handleError(UIText.CreateBranchWizard_CreationFailed, ite
                    .getCause(), true);
            return false;
        } catch (InterruptedException ie) {
            // ignore here
        }
        return true;
    }

    /**
     * @return the name (without ref/heads/) of the new branch
     */
    public String getNewBranchName() {
        return newBranchName;
    }
}
