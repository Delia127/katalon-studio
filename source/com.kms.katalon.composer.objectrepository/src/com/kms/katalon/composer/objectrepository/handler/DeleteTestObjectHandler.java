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
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestObjectHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected UISynchronize sync;

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
            String testObjectId = webElement.getIdForDisplay();
            List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(testObjectId,
                    webElement.getProject());

            if (performDeleteTestObject(webElement, sync, eventBroker, Collections.emptyList(), affectedTestCaseScripts)) {
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
     * @param affectedTestCaseScripts List of referenced test case scripts
     * @return true if system deleted the given <code>webElement</code>. Otherwise, false.
     */
    protected boolean performDeleteTestObject(final WebElementEntity webElement, final UISynchronize sync,
            final IEventBroker eventBroker, final List<Object> elementsWillBeDeleted,
            List<IFile> affectedTestCaseScripts) {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        try {
            List<WebElementEntity> testObjectReferences = ObjectRepositoryController.getInstance()
                    .getTestObjectReferences(webElement, currentProject);

            // List of references contains only elements that will not be deleted.
            final List<FileEntity> fileEntities = new ArrayList<FileEntity>();
            List<WebElementEntity> testObjectReferenceWithoutDeleting = new ArrayList<WebElementEntity>();
            for (WebElementEntity testObject : testObjectReferences) {
                if (!elementsWillBeDeleted.contains(testObject)) {
                    testObjectReferenceWithoutDeleting.add(testObject);
                }
            }
            fileEntities.addAll(testObjectReferenceWithoutDeleting);
            fileEntities.addAll(TestCaseEntityUtil.getTestCaseEntities(affectedTestCaseScripts));

            if (!fileEntities.isEmpty()) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }

                    final DeleteTestObjectHandler handler = this;

                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            TestObjectReferencesDialog dialog = new TestObjectReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), webElement.getIdForDisplay(), fileEntities, handler);
                            dialog.open();
                        }
                    });
                }

                if (canDelete()) {
                    // remove test object references in other parent test objects
                    removeReferencesInTestObjects(testObjectReferences, eventBroker);

                    // remove references in test case
                    GroovyRefreshUtil.removeReferencesInTestCaseScripts(webElement.getIdForDisplay(),
                            affectedTestCaseScripts);
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

    /**
     * Remove Test Object references in other parent Test Objects
     * 
     * @param testObjectReferences
     * @param eventBroker
     */
    private void removeReferencesInTestObjects(final List<WebElementEntity> testObjectReferences,
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
