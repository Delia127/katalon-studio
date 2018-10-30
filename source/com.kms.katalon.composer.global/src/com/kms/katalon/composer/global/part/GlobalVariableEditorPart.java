package com.kms.katalon.composer.global.part;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.parts.SavableCompositePart;

import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class GlobalVariableEditorPart extends CPart implements SavableCompositePart {
	
	MirrorEditor mirrorEditor;
	
	Composite composite;
	
	ExecutionProfileEntity executionProfileEntity;
	
	ExecutionProfileCompositePart parentExecutionProfileCompositePart;
	
	String contentScript = StringUtils.EMPTY;
	
	boolean contentChanged = false;

    @Inject
    private EPartService partService;

    MPart mpart;
	
    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        this.executionProfileEntity = (ExecutionProfileEntity) mpart.getObject();
        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof ExecutionProfileCompositePart) {
            	parentExecutionProfileCompositePart = ((ExecutionProfileCompositePart) compositePart.getObject());
            	
            }
        }
        initialize(mpart, partService);        
        createComposite(parent);
        updateProfileEntityFrom(executionProfileEntity);
    }

	private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
		
        mirrorEditor = new MirrorEditor(composite, SWT.NONE);
        mirrorEditor.setEditable(true);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {
            
            @Override
            public void onDocumentReady() {
                mirrorEditor.changeMode(TextContentType.XML.getText());
            }
        });
        
        mirrorEditor.addListener(SWT.Modify, event ->{
        	if(contentChanged == false){
        		contentChanged = true;
        	}else{
        		setDirty(true);
        	}        		
        });       
	}
	
    public void setDirty(boolean isDirty) {
    	parentExecutionProfileCompositePart.setDirty(isDirty);
    }

	public void updateProfileEntityFrom(ExecutionProfileEntity entity) {
		if(entity != null){
			executionProfileEntity = entity;
		}
		updateScriptContent();
	}

	private void updateScriptContent() {
		try {
			String incomingContentScript = GlobalVariableController.getInstance().toXmlString(executionProfileEntity);
			if(!contentScript.equals(incomingContentScript)){
				mirrorEditor.setText(incomingContentScript);	
				if(!contentScript.equals(StringUtils.EMPTY))
					contentChanged = true;
				contentScript = incomingContentScript;
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	public MPart getMPart() {
		return mpart;
	}
	
    @Persist
    @Override
	public void save() {
    	
	}
    
    @PreDestroy
    @Override
    public void dispose() {
        super.dispose();
        partService.hidePart(mpart);
    }

	public MirrorEditor getEditor() {
		return mirrorEditor;
	}

	@Override
	public List<MPart> getChildParts() {
		List<MPart> res = new ArrayList<>();
		res.add(getMPart());
		return res;
	}
	

    private boolean isExecutionProfileEntityValid() {
    	List<String> globalVariableNames = executionProfileEntity.getGlobalVariableEntities()
    			.stream()
    			.map(var -> var.getName())
    			.collect(Collectors.toList());
    	
    	for(GlobalVariableEntity var: executionProfileEntity.getGlobalVariableEntities()){
    		if(validate(var, globalVariableNames) == false){
    			return false;
    		}
    	}
    	return true;
	}
    
    
    private boolean validate(GlobalVariableEntity fVariableEntity, List<String> globalVariableNames) {
        String newVariableName = fVariableEntity.getName();
        
        if (!GroovyConstants.VARIABLE_NAME_REGEX.matcher(newVariableName).find()) {
        	MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.DIA_ERROR_MSG_INVALID_VAR_NAME);
        	return false;
        }
        
        if (globalVariableNames.indexOf(newVariableName) != globalVariableNames.lastIndexOf(newVariableName)) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_WARN_MSG_DUPLICATE_VAR_NAME);
            return false;
        }
        return true;
    }

	public ExecutionProfileEntity getEntityFromScript(){
		boolean failedToParseScriptToEntity = false;
		try {
			ExecutionProfileEntity retEntity = 
					GlobalVariableController.getInstance().toExecutionProfileEntity(mirrorEditor.getText());
			ExecutionProfileEntity oldExecutionProfileEntity = executionProfileEntity;
			if(retEntity != null){
				executionProfileEntity = retEntity;
				executionProfileEntity.setName(oldExecutionProfileEntity.getName());
				executionProfileEntity.setProject(oldExecutionProfileEntity.getProject());
				executionProfileEntity.setParentFolder(oldExecutionProfileEntity.getParentFolder());
				failedToParseScriptToEntity = !isExecutionProfileEntityValid();
			}
		} catch (DALException e) {
			LoggerSingleton.logError(e);
			failedToParseScriptToEntity = true;
		} finally {
			if(failedToParseScriptToEntity){
				return null;
			}		
		}
		return executionProfileEntity;
	}	
}
