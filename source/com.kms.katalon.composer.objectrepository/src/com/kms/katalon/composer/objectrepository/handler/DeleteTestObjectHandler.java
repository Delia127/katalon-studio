package com.kms.katalon.composer.objectrepository.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
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
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestObjectHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    protected boolean isRemovingRef = false;

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
            String testObjectId = ObjectRepositoryController.getInstance().getIdForDisplay(webElement);
            final boolean hasRef = GroovyRefreshUtil.hasReferencesInTestCaseScripts(testObjectId,
                    webElement.getProject());
            sync.syncExec(new Runnable() {

                @Override
                public void run() {
                    // Give a confirmation message for current Test Object to remove its references
                    if (hasRef) {
                        // Depend on the user response, references will be removed or not
                        isRemovingRef = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                                StringConstants.HAND_TITLE_DELETE, StringConstants.HAND_MSG_REMOVE_ENTITY_REF);
                    }
                }
            });

            if (performDeleteTestObject(webElement, sync, eventBroker, Collections.emptyList(), isRemovingRef)) {
                // remove TestCase part from its partStack if it exists
                EntityPartUtil.closePart(webElement);

                ObjectRepositoryController.getInstance().deleteWebElement(webElement);

                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testObjectId);
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
     * {@link WebElementPropertyEntity} out of reference. Otherwise, don't delete the given <code>webElement</code>
     * <p>
     * List of references contains all {@link WebElementEntity} that has parent object is the given
     * <code>webElement</code> but not a member of <code>elementsWillBeDeleted</code>
     * 
     * @param webElement the given {@link WebElementEntity} needs to delete.
     * @param sync
     * @param eventBroker
     * @param elementsWillBeDeleted
     * @param isRemovingRefInTestCase
     * @return true if system deleted the given <code>webElement</code>. Otherwise, false.
     */
    protected boolean performDeleteTestObject(final WebElementEntity webElement, final UISynchronize sync,
            final IEventBroker eventBroker, final List<Object> elementsWillBeDeleted,
            final boolean isRemovingRefInTestCase) {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        try {
            List<WebElementEntity> testObjectReferences = ObjectRepositoryController.getInstance()
                    .getTestObjectReferences(webElement, currentProject);

            // List of references contains only elements that will not be deleted.
            final List<WebElementEntity> testObjectReferenceWithoutDeleting = new ArrayList<WebElementEntity>();
            for (WebElementEntity testObject : testObjectReferences) {
                if (!elementsWillBeDeleted.contains(testObject)) {
                    testObjectReferenceWithoutDeleting.add(testObject);
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

            if (isRemovingRefInTestCase) {
                // Find all Test Case script which has relationship with the Test Object
                String testObjectId = ObjectRepositoryController.getInstance().getIdForDisplay(webElement);
                List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(testObjectId,
                        currentProject);
                GroovyRefreshUtil.removeReferencesInTestCaseScripts(testObjectId, affectedTestCaseScripts);
            }

            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    /**
     * Remove Test Object references in other Test Objects
     * 
     * @param testObjectReferences
     * @param eventBroker
     */
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
