package com.kms.katalon.composer.integration.git.internal.push;

import org.eclipse.egit.core.op.PushOperationResult;
import org.eclipse.egit.ui.internal.commit.RepositoryCommit;
import org.eclipse.ui.model.WorkbenchContentProvider;

/**
 * Content provided for push result table viewer.
 * <p>
 * Input of this provided must be {@link PushOperationResult} instance, while returned elements are instances of
 * {@link RefUpdateElement}. Null input is allowed, resulting in no elements.
 *
 * @see PushOperationResult
 * @see RefUpdateElement
 */
@SuppressWarnings("restriction")
public class CustomRefUpdateContentProvider extends WorkbenchContentProvider {

    @Override
    public Object[] getElements(final Object element) {
        return element instanceof Object[] ? (Object[]) element : new Object[0];
    }

    @Override
    public Object[] getChildren(Object element) {
        if (element instanceof RepositoryCommit) {
            return ((RepositoryCommit) element).getDiffs();
        }
        return super.getChildren(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof RepositoryCommit) {
            // always return true for commits to avoid commit diff calculation
            // in UI thread, see bug 458839
            return true;
        }
        return super.hasChildren(element);
    }
}
