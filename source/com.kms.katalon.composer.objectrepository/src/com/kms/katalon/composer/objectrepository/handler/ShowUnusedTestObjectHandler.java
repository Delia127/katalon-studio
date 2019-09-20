package com.kms.katalon.composer.objectrepository.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.objectrepository.constant.ImageConstants;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.part.UnusedTestObjectsPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;
import com.kms.katalon.tracking.service.Trackings;

public class ShowUnusedTestObjectHandler {

    private static final String UNUSED_TEST_OBJECTS_PART_URI = "bundleclass://com.kms.katalon.composer.objectrepository/"
            + UnusedTestObjectsPart.class.getName();

    @Inject
    MApplication application;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(Shell shell) throws IOException, InterruptedException {
        ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
        List<WebElementEntity> unusedTestObject = new ArrayList<WebElementEntity>();
        try {
            monitor.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                        FolderEntity folder = FolderController.getInstance().getObjectRepositoryRoot(project);

                        List<Object> descendantEntities = FolderController.getInstance()
                                .getAllDescentdantEntities(folder);

                        monitor.beginTask(StringConstants.DIA_TITLE_FIND_UNUSED_TEST_OBJECT, descendantEntities.size());
                        for (Object entity : descendantEntities) {
                            if (entity instanceof WebElementEntity) {
                                WebElementEntity webElement = (WebElementEntity) entity;
                                String testObjectId = webElement.getIdForDisplay();
                                List<IFile> affectedScripts = TestArtifactScriptRefactor
                                        .createForTestObjectEntity(testObjectId).findReferrersInScripts(project);
                                if (affectedScripts.isEmpty()) {
                                    unusedTestObject.add(webElement);
                                }
                            }
                            monitor.worked(1);
                        }
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException exception) {
            MultiStatusErrorDialog.showErrorDialog(exception, StringConstants.ERROR, exception.getMessage());
        }
        openTab(unusedTestObject);

    }

    private void openTab(List<WebElementEntity> unusedTestObjects) {
        String partId = EntityPartUtil.getUnusedTestObjectsPartId();
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        MPart mPart = (MPart) modelService.find(partId, application);
        boolean alreadyOpened = true;
        if (mPart == null) {
            mPart = modelService.createModelElement(MPart.class);
            mPart.setElementId(partId);
            mPart.setLabel(StringConstants.UNUSED_TEST_OBJECT_LABEL);
            mPart.setContributionURI(UNUSED_TEST_OBJECTS_PART_URI);
            mPart.setIconURI(ImageConstants.URL_16_UNUSED_TEST_OBJECT);
            mPart.setCloseable(true);
            mPart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
            stack.getChildren().add(mPart);
            alreadyOpened = false;
        } else {
            eventBroker.post(EventConstants.UNUSED_TEST_OBJECTS_UPDATED, unusedTestObjects);
        }

        if (mPart.getObject() == null) {
            mPart.setObject(unusedTestObjects);
        }
        partService.showPart(mPart, PartState.ACTIVATE);
        partService.bringToTop(mPart);
        stack.setSelectedElement(mPart);

        if (!alreadyOpened) {
            Trackings.trackOpenObject("unusedTestObject");
        }

    }
}
