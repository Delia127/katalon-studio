package com.kms.katalon.composer.global.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.tracking.service.Trackings;

public class ExecutionProfileCompositePart implements IComposerPartEvent, SavableCompositePart {
	    @Inject
	    protected MApplication application;

	    @Inject
	    protected EModelService modelService;

	    @Inject
	    protected IEventBroker eventBroker;

	    @Inject
	    protected IStylingEngine styleEngine;

	    @Inject
	    protected EPartService partService;
	    
	    @Inject
	    private MDirtyable dirty;

	    public MDirtyable getDirty() {
	        return dirty;
	    }
	    
	    protected MCompositePart compositePart;
	    
	    private MPartStack subPartStack;
	    
	    protected ExecutionProfileEntity executionProfileEntity;
	    
	    protected GlobalVariablePart globalVariablePart;
	    
	    protected GlobalVariableEditorPart globalVariableEditorPart;
	    
	    protected ExecutionProfilePartUI ui;
	    
	    private CTabFolder tabFolder;
	    
	    protected Composite parent;
	    	    
	    private boolean invalidSchema;
	    
	    private boolean variableTab = true;

		@PostConstruct
	    public void init(Composite parent, MCompositePart part) {
	        this.compositePart = part;
	        this.executionProfileEntity = (ExecutionProfileEntity) part.getObject();
	        this.parent = parent;
	        dirty.setDirty(false);
	        new HelpToolBarForMPart(part, DocumentationMessageConstants.GLOBAL_VARIABLES);
	        invalidSchema = false;
	    }
	    
	    public void initComponents(ExecutionProfilePartUI executionProfilePartUi) {
	        this.ui = executionProfilePartUi;
	        initComponent();
	    }
	    
		public void initComponent() {
			compositePart.setIconURI(ImageManager.getImageURLString(IImageKeys.GLOBAL_VARIABLE_16));
	        List<MPartSashContainerElement> compositePartChildren = compositePart.getChildren();
	        if (compositePartChildren.size() == 1 && compositePartChildren.get(0) instanceof MPartStack) {
	            subPartStack = (MPartStack) compositePartChildren.get(0);
	            if (subPartStack.getChildren().size() == 2) {
	                for (MStackElement stackElement : subPartStack.getChildren()) {
	                    if (!(stackElement instanceof MPart)) {
	                        continue;
	                    }

	                    Object partObject = ((MPart) stackElement).getObject();

	                    if (partObject instanceof GlobalVariablePart) {
	                    	globalVariablePart = (GlobalVariablePart) partObject;
	                        continue;
	                    }

	                    if (partObject instanceof GlobalVariableEditorPart) {
	                    	globalVariableEditorPart = (GlobalVariableEditorPart) partObject;
	                        continue;
	                    }
	                   
	                }
	            }

	            if (subPartStack.getWidget() instanceof CTabFolder) {
	            	tabFolder = ui.getTabFolder();
	                tabFolder.setTabPosition(SWT.BOTTOM);
	                tabFolder.setBorderVisible(false);
	                tabFolder.setMaximizeVisible(false);
	                tabFolder.setMinimizeVisible(false);

	                if (tabFolder.getItemCount() == 2) {
	                    CTabItem globalVariablePartTab = ui.getGlobalVariableTab();
	                    globalVariablePartTab.setImage(ImageConstants.IMG_16_MANUAL);
	                    globalVariablePartTab.setShowClose(false);

	                    CTabItem globalVariableEditorPartTab = ui.getGlobalVariableEditorTab();
	                    globalVariableEditorPartTab.setImage(ImageConstants.IMG_16_SCRIPT);
	                    globalVariableEditorPartTab.setShowClose(false);
	                    
	                    CTabItem globalVariableAddTab = ui.getGlobalVariableAddTab();
	                    globalVariableAddTab.setImage(ImageConstants.IMG_16_MANUAL);
	                    globalVariableAddTab.setShowClose(false);
	                    
	                    
	                }

	            	//TODO: Handle these cases
	                tabFolder.addSelectionListener(new SelectionAdapter() {
						@Override
	                    public void widgetSelected(SelectionEvent event) {
	                        if (tabFolder == null) {	                        
	                            return;
	                        }
	                        
	                        if (tabFolder.getSelectionIndex() == 0) {
	                            if(dirty.isDirty())
	                                updateVariableManualView();
	                            variableTab = true;
	                            return;
	                        }

	                        if (tabFolder.getSelectionIndex() == 1) {
	                            if(dirty.isDirty())
	                                updateVariableScriptView();
	                            variableTab = false;
	                            return;
                        	}
	                    

					
						}
	                });
	                tabFolder.layout();
	            }
	            // Initialize editor's view
	            updateVariableScriptView();	
	        }	
		}

        private void updateVariableScriptView() {
            try {
                globalVariableEditorPart.setScriptContentFrom(globalVariablePart.getExecutionProfileEntity());
                setInvalidScheme(false);
            } catch (Exception e) {
                setInvalidScheme(true);
            }
        }
        

        private void updateVariableManualView() {
            try {
                globalVariablePart.setVariablesFromScriptContent(globalVariableEditorPart.getScriptContent());
                setInvalidScheme(false);
            } catch (Exception e) {    
                setInvalidScheme(true);
            }
        }
        
    	
		private void setInvalidScheme(boolean value){
		    invalidSchema = value;
		}
		
	    @Persist
	    @Override
		public void save(){
	        try {
	           
	            if(variableTab == true){
	                updateVariableScriptView();
	            }
	            // If users perform "save operation" from variableScriptView,
	        	// we update VariableManualView to sync the variables, but since the
	        	// user can copy/paste all content from another execution
	        	// profile's script, the execution profile's name in the script may not be in
	        	// sync with executionProfileEntity, we update VariableScriptView to sync the name
	            else{
	                updateVariableManualView();
	                updateVariableScriptView();
	              
	            }
	            
	        	if(invalidSchema == true){
	    			MessageDialog.openError(null, StringConstants.ERROR_TITLE,
	                        StringConstants.PA_ERROR_MSG_UNABLE_TO_UPDATE_PROFILE);
	    			return;
	        	}
	        	globalVariablePart.updateVariableReferences();
	            GlobalVariableController.getInstance().updateExecutionProfile(executionProfileEntity);  
	            setDirty(false);
	            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
	        	
	        } catch (Exception e) {	         
	            LoggerSingleton.logError(e);
	        }
		}		


		public void dispose() {
	        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
	        mStackPart.getChildren().remove(compositePart);
	    }

		public void setDirty(boolean isDirty) {
			dirty.setDirty(isDirty);
		}
		

		
		@Override
		public String getEntityId() {
			return executionProfileEntity.getIdForDisplay();
		}

	    @Override
	    @Inject
	    @Optional
		public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
	    	 MPart part = EventUtil.getPart(event);
	         if (part == null || !part.getElementId().equals(compositePart.getElementId())) {
	             return;
	         }
	         EventUtil.post(EventConstants.PROPERTIES_ENTITY, executionProfileEntity);
		}

	    @Override
	    @Inject
	    @Optional
		public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
            Object eventData = EventUtil.getData(event);
            if (!(eventData instanceof ExecutionProfileEntity)) {
                return;
            }

            ExecutionProfileEntity updatedEntity = (ExecutionProfileEntity) eventData;
            if (updatedEntity.getIdForDisplay().equals(getEntityId())) {
                return;
            }
            
            ExecutionProfileEntity oldExecutionProfileEntity = executionProfileEntity;
            executionProfileEntity = updatedEntity;
    		executionProfileEntity.setProject(oldExecutionProfileEntity.getProject());
    		executionProfileEntity.setParentFolder(oldExecutionProfileEntity.getParentFolder());
    		
            eventBroker.post(EventConstants.PROPERTIES_ENTITY, executionProfileEntity);
            setDirty(true);
		}

	    @Override
	    @PreDestroy
	    public void onClose() {
	    	EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
	        dispose();
	    }

		public boolean isDirty() {
			return dirty.isDirty();
		}

		@Override
		public List<MPart> getChildParts() {
			return Arrays.asList(ui.getGlobalVariablePart(), ui.getGlobalVariableEditorPart());
		}
}
