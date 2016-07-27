package com.kms.katalon.composer.objectrepository.handler;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.composer.testcase.util.TestCaseEntityUtil.getTestCaseEntities;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.dialog.TestObjectReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

public class DeleteTestObjectHandler extends AbstractDeleteReferredEntityHandler {

    protected ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

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

            monitor.beginTask(
                    format(StringConstants.HAND_DELETE_OBJECT_TASK_NAME, treeEntity.getTypeName(), treeEntity.getText()),
                    1);

            WebElementEntity webElement = (WebElementEntity) treeEntity.getObject();
            String testObjectId = webElement.getIdForDisplay();
            List<IFile> affectedTestCaseScripts = TestArtifactScriptRefactor.createForTestObjectEntity(testObjectId)
                    .findReferrersInTestCaseScripts(ProjectController.getInstance().getCurrentProject());

            if (performDeleteTestObject(webElement, Collections.emptyList(), affectedTestCaseScripts)) {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testObjectId);
                return true;
            }
        } catch (Exception e) {
            logError(e);
            openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ);
        } finally {
            monitor.done();
        }
        return false;
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
    protected boolean performDeleteTestObject(final WebElementEntity webElement,
            final List<Object> elementsWillBeDeleted, final List<IFile> affectedTestCaseScripts) {
        isDeleted = false;
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    List<WebElementEntity> testObjectReferences = toController.getTestObjectReferences(webElement,
                            ProjectController.getInstance().getCurrentProject());

                    // List of references contains only elements that will not be deleted.
                    List<FileEntity> fileEntities = new ArrayList<FileEntity>();
                    for (WebElementEntity testObject : testObjectReferences) {
                        if (!elementsWillBeDeleted.contains(testObject)) {
                            fileEntities.add(testObject);
                        }
                    }
                    fileEntities.addAll(getTestCaseEntities(affectedTestCaseScripts));

                    if (!fileEntities.isEmpty()) {
                        String testObjectIdForDisplay = webElement.getIdForDisplay();
                        if (isDefaultResponse()) {
                            TestObjectReferencesDialog dialog = new TestObjectReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), testObjectIdForDisplay, fileEntities, needYesNoToAllButtons());
                            setResponse(dialog.open());
                        }

                        if (isCancelResponse()) {
                            return;
                        }

                        if (isYesResponse()) {
                            // remove test object references in other parent test objects
                            removeReferencesInTestObjects(testObjectReferences);

                            // remove references in test case
                            TestArtifactScriptRefactor.createForTestObjectEntity(testObjectIdForDisplay)
                                    .removeReferences(affectedTestCaseScripts);
                        }
                    }

                    // remove TestCase part from its partStack if it exists
                    EntityPartUtil.closePart(webElement);

                    // remove Test Object
                    toController.deleteWebElement(webElement);

                    if (!isYesNoToAllResponse()) {
                        resetResponse();
                    }

                    isDeleted = true;
                } catch (Exception e) {
                    logError(e);
                }
            }
        });
        return isDeleted;
    }

    /**
     * Remove Test Object references in other parent Test Objects
     * 
     * @param affectedTestObjects
     * @param eventBroker
     * @throws Exception
     */
    private void removeReferencesInTestObjects(List<WebElementEntity> affectedTestObjects) throws Exception {
        for (WebElementEntity testObject : affectedTestObjects) {
            WebElementPropertyEntity refElement = toController.getRefElementProperty(testObject);
            testObject.getWebElementProperties().remove(refElement);
            toController.updateTestObject(testObject);
            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { testObject.getId(), testObject });
        }
    }
}
