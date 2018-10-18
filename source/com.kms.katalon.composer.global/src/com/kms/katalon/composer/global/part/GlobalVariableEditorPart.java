package com.kms.katalon.composer.global.part;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.parts.SavableCompositePart;
import com.kms.katalon.composer.webservice.components.MirrorEditor;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class GlobalVariableEditorPart extends CPart implements EventHandler, SavableCompositePart {
	
	MirrorEditor mirrorEditor;
	
	Composite composite;
	
	ExecutionProfileCompositePart parentExecutionProfileCompositePart;

    @Inject
    private EPartService partService;

    @Inject
    private IEventBroker eventBroker;

	MPart mpart;
	
    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof ExecutionProfileCompositePart) {
            	parentExecutionProfileCompositePart = ((ExecutionProfileCompositePart) compositePart.getObject());
            	
            }
        }
        initialize(mpart, partService);
        createComposite(parent);
        registerEventListeners();
    }

	private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
		
        mirrorEditor = new MirrorEditor(composite, SWT.NONE);
        mirrorEditor.setEditable(true);
	}
	
    private void setDirty(boolean isDirty) {
        mpart.setDirty(isDirty);
    }
    
    public boolean isDirty(){
    	return mpart.isDirty();
    }
	

	@SuppressWarnings("static-access")
	public void updateScriptFrom(ExecutionProfileEntity entity) {
		if(entity != null){
	        try
	        {
	        	String content = GlobalVariableController.getInstance().toXmlString(entity);
	        	if (content != null && !content.equals(mirrorEditor.getText())){
	        		mirrorEditor.setText(content);
	        	}
	        } catch (Exception e) {
	            e.printStackTrace();
	            LoggerSingleton.getInstance().logError("Invalid execution profile!");
	        }
		}
	}

	public MPart getMPart() {
		return mpart;
	}
	
    @Persist
    @Override
    @SuppressWarnings("restriction")
	public void save() {
        try {
        	if(isDirty()){
                eventBroker.post(EventConstants.EXECUTION_PROFILE_UPDATED, mirrorEditor.getText());
        	}        
        	setDirty(false);
        } catch (Exception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_ALL_VAR);
            LoggerSingleton.getInstance().getLogger().error(e);
        }
	}
    

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_UPDATED, this);
        mirrorEditor.addListener(SWT.Modify, event -> {
        	setDirty(true);
        });        
    }
    
	@Override
	public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.EXECUTION_PROFILE_UPDATED: {
            	Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            	if(!(object instanceof ExecutionProfileEntity)){
            		return;
            	}
            	updateScriptFrom((ExecutionProfileEntity) object);
            	break;
            }
        }		
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
	
}
