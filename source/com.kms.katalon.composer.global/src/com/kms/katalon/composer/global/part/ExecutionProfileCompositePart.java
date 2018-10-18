package com.kms.katalon.composer.global.part;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileCompositePart {
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
	    
	    @Inject
	    protected MDirtyable dirtyable;

		@PostConstruct
	    public void init(Composite parent, MCompositePart part) {
	        this.compositePart = part;
	        this.executionProfileEntity = (ExecutionProfileEntity) part.getObject();
	        this.parent = parent;
	        dirty.setDirty(false);
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
	                tabFolder.setTabPosition(SWT.TOP);
	                tabFolder.setBorderVisible(false);
	                tabFolder.setMaximizeVisible(false);
	                tabFolder.setMinimizeVisible(false);

	                if (tabFolder.getItemCount() == 2) {
	                    CTabItem globalVariablePartTab = ui.getGlobalVariableTab();
	                    globalVariablePartTab.setImage(ImageConstants.IMG_16_MANUAL);
	                    globalVariablePartTab.setShowClose(false);

	                    CTabItem globalVariableEditorPartTab = ui.getGlobalVariableEditorTab();
	                    globalVariableEditorPartTab.setText("Script view");
	                    globalVariableEditorPartTab.setImage(ImageConstants.IMG_16_SCRIPT);
	                    globalVariableEditorPartTab.setShowClose(false);
	                }

	            	//TODO: Handle these cases
	                tabFolder.addSelectionListener(new SelectionAdapter() {
	                    @Override
	                    public void widgetSelected(SelectionEvent event) {
	                        if (tabFolder == null) {	                        
	                            return;
	                        }
	                        
	                        if (tabFolder.getSelectionIndex() == 0) {
	                    		eventBroker.post(EventConstants.EXECUTION_PROFILE_UPDATED, globalVariableEditorPart.getEditor().getText());		                    	
	                        	return;	
	                        }

	                        if (tabFolder.getSelectionIndex() == 1) {
	                    		eventBroker.post(EventConstants.EXECUTION_PROFILE_UPDATED, globalVariablePart.getEntity());		                    	
	                        	return;
	                        }
	                    }
	                });
	                tabFolder.layout();
	            }
	        }	
		}
		
	    @PreDestroy
	    public void dispose() {
	    	globalVariablePart.dispose();
	    	globalVariableEditorPart.dispose();
	    }
		
}
