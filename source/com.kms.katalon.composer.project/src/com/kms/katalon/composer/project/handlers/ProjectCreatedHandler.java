package com.kms.katalon.composer.project.handlers;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.osgi.service.event.Event;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class ProjectCreatedHandler {
    @Inject
    private IEventBroker eventBroker;
    
	@PostConstruct
	public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.PROJECT_CREATED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
            	Object object = getObject(event);
            	execute(object);
            }
        });
	}
	
    @Execute
    public void execute(@UIEventTopic(EventConstants.PROJECT_CREATED) Object object) {
    	if(object != null){
        	// Set default selector method of newly created projects to XPath
        	ProjectEntity projectEntity = (ProjectEntity) object;
        	WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(projectEntity);
        	try {
    			store.setCapturedTestObjectSelectorMethod(SelectorMethod.XPATH);
    		} catch (IOException e) {
    			LoggerSingleton.logError(e);
    		}
    	}
    }
}
