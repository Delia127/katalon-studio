package com.kms.katalon.composer.integration.cucumber.handler;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.EditorPartWithHelp;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class OpenFeatureEntityHandler {
    
    private static final String EDITOR_COMPOSITE_PART_URI = "bundleclass://com.kms.katalon.composer.components/" + 
            EditorPartWithHelp.class.getName();
    
    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private EModelService modelService;
    
    @Inject
    private EPartService partService;
    
    @Inject
    private MApplication application;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object instanceof SystemFileEntity) {
                    openEditor((SystemFileEntity) object);
                }
            }
        });
    }

    MPart openEditor(SystemFileEntity object) {
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        if (stack == null) {
            return null;
        }
        
        String compositePartId = getCompositePartId(object);
        MCompositePart compositePart = (MCompositePart) modelService.find(compositePartId, application);
        if (compositePart != null) {
            stack.setSelectedElement(compositePart);
        } else {
            compositePart = modelService.createModelElement(MCompositePart.class);
            compositePart.setElementId(compositePartId);
            compositePart.setCloseable(true);
            compositePart.setContributionURI(EDITOR_COMPOSITE_PART_URI);
            compositePart.setLabel(object.getName());
            compositePart.setIconURI(ImageManager.getImageURLString(IImageKeys.FEATURE_16));
            compositePart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
            stack.getChildren().add(compositePart);
            
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            IFile iFile = GroovyUtil.getGroovyProject(currentProject).getFile(object.getRelativePath());

            try {
                iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
                MPart editorPart;
                if (iFile.getFileExtension().equals("feature")) {
                    editorPart = GroovyEditorUtil.createCucumberEditorPart(iFile, partService);
                } else {
                    editorPart = GroovyEditorUtil.createDefaultEditorPart(iFile, partService);
                }
                if (editorPart != null) {
                    compositePart.getChildren().add(editorPart);
                    partService.activate(compositePart);
                    partService.activate(editorPart);
                    
                    EditorPartWithHelp partObject = (EditorPartWithHelp) compositePart.getObject();
                    partObject.init(editorPart, DocumentationMessageConstants.CUCUMBER_FEATURE_FILE);
                }
                
               
            } catch (CoreException e) {
                LoggerSingleton.logError(e);
                return null;
            }
        }
        return compositePart;
    }

    private String getCompositePartId(SystemFileEntity object) {
        return EntityPartUtil.getFeaturePartId(object.getId());
    }
}
