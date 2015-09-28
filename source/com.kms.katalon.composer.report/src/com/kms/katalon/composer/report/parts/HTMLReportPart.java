package com.kms.katalon.composer.report.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.report.ReportEntity;

public class HTMLReportPart implements EventHandler {

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;


    @Inject
    private IEventBroker eventBroker;
   
    private Browser browser;
    
    private ReportEntity report;
    private MPart mPart;
    
    @PostConstruct
    public void createComposite(Composite parent, MPart part) {        
        mPart = part;
        registerEventBrokerListers();
        
    	if(part.getObject() != null && part.getObject() instanceof ReportEntity){
    	    report = (ReportEntity) part.getObject();
        	showReport(parent, report);
    	}
    }
    
    private void registerEventBrokerListers() {
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }

    public void showReport(Composite parent, ReportEntity report) {
    	parent.setLayout(new FillLayout(SWT.VERTICAL));
		browser = new Browser(parent, SWT.BORDER);
		loadReport();
	}
    
    private void loadReport() {
        //browser.setUrl(report.getId());
    	browser.setUrl(report.getHtmlFile());
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ITreeEntity) {
                    if (object instanceof ReportTreeEntity) {
                        ReportTreeEntity reportTreeEntity = (ReportTreeEntity) object;
                        ReportEntity reportEntity = (ReportEntity) (reportTreeEntity).getObject();
                        if (reportEntity != null && reportEntity.getId().equals(report.getId())) {
                            if (ReportController.getInstance().getReportEntity(reportEntity.getId()) != null) {
                                loadReport();
                            } else {
                                dispose();
                            }
                        }
                    } else if (object instanceof FolderTreeEntity) {
                        FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                        if (folder != null && FolderController.getInstance().isFolderAncestorOfEntity(folder, report)) {
                            if (ReportController.getInstance().getReportEntity(report.getId()) == null) {
                                dispose();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }
    
    private void dispose() {
        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        mStackPart.getChildren().remove(mPart);
        eventBroker.unsubscribe(this);
    }
}
