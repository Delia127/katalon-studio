package com.kms.katalon.composer.objectrepository.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.dialog.TestObjectReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class DeleteTestObjectHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return WebElementTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof WebElementTreeEntity)) {
                return false;
            }

            String taskName = MessageFormat.format(StringConstants.HAND_DELETE_OBJECT_TASK_NAME,
                    treeEntity.getTypeName(), treeEntity.getText());

            monitor.beginTask(taskName, 1);

            WebElementEntity webElement = (WebElementEntity) treeEntity.getObject();
            if (performDeleteTestObject(webElement, sync, eventBroker, Collections.emptyList())) {
                // remove TestCase part from its partStack if it exists
                EntityPartUtil.closePart(webElement);

                ObjectRepositoryController.getInstance().deleteWebElement(webElement);

                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, ObjectRepositoryController
                        .getInstance().getIdForDisplay(webElement));
                return true;
            } else {
                return false;
            }
        } catch (EntityIsReferencedException e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ);
            return false;
        } finally {
            monitor.done();
        }
    }

    /**
     * Performs a deletion progress of {@link WebElementEntity}.
     * <p>
     * If the given <code>webElement</code> is being referred by some {@link WebElementEntity} then show a confirmation
     * to let user choose continue or not.
     * <p>
     * If user choose continue then delete the given <code>webElement</code> and also remove ref_element
     * {@link WebElementPropertyEntity} out of reference. Otherwise, don't delete the given
     * <code>webElement</code>
     * <p>
     * List of references contains all {@link WebElementEntity} that has parent object is the given
     * <code>webElement</code> but not a member of <code>elementsWillBeDeleted</code>
     * 
     * @param webElement
     *            the given {@link WebElementEntity} needs to delete.
     * @param sync
     * @param eventBroker
     * @param elementsWillBeDeleted
     * @return true if system deleted the given <code>webElement</code>. Otherwise, false.
     */
    protected boolean performDeleteTestObject(final WebElementEntity webElement, final UISynchronize sync,
            final IEventBroker eventBroker, final List<Object> elementsWillBeDeleted) {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        try {
            List<WebElementEntity> testObjectReferences = ObjectRepositoryController.getInstance()
                    .getTestObjectReferences(webElement, currentProject);

            // List of references contains only elements that will not be deleted.
            final List<WebElementEntity> testObjectReferenceWithoutDeleting = new ArrayList<WebElementEntity>();
            for (WebElementEntity reference : testObjectReferences) {
                if (!elementsWillBeDeleted.contains(reference)) {
                    testObjectReferenceWithoutDeleting.add(reference);
                }
            }

            if (testObjectReferenceWithoutDeleting.size() > 0) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }

                    final DeleteTestObjectHandler handler = this;

                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            TestObjectReferencesDialog dialog = new TestObjectReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), webElement, testObjectReferenceWithoutDeleting, handler);
                            dialog.open();
                        }
                    });
                }

                if (canDelete()) {
                    deleteTestObjectReferences(testObjectReferences, eventBroker);
                } else {
                    return false;
                }

            }
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private void deleteTestObjectReferences(final List<WebElementEntity> testObjectReferences,
            final IEventBroker eventBroker) {
        for (WebElementEntity referenceObject : testObjectReferences) {
            ObjectRepositoryController.getInstance();
            WebElementPropertyEntity refElement = ObjectRepositoryController.getRefElementProperty(referenceObject);
            referenceObject.getWebElementProperties().remove(refElement);
            try {
                ObjectRepositoryController.getInstance().updateWebElement(referenceObject);
                eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { referenceObject.getId(),
                        referenceObject });
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }
}
