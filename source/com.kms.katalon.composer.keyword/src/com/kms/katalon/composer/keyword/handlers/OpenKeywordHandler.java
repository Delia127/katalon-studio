package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class OpenKeywordHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ICompilationUnit
                        && ((ICompilationUnit) object).getElementName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    excute((ICompilationUnit) object);
                }
            }
        });
    }

    /**
     * Open a custom keyword file and validate that file after user save it
     * 
     * @param keywordFile
     */
    private void excute(ICompilationUnit keywordFile) {
        if (keywordFile != null && keywordFile.exists()) {
            try {
                IFile iFile = (IFile) keywordFile.getResource();
                if (!keywordFile.isWorkingCopy()) {
                    keywordFile.becomeWorkingCopy(null);
                }
                
                 IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
                 .getDefaultEditor(iFile.getName());
                 desc.getImageDescriptor().createFromImage(ImageConstants.IMG_16_KEYWORD);
                 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                 .openEditor(new FileEditorInput(iFile), desc.getId());

            } catch (Exception e) {
                LoggerSingleton.logError(e);
                MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                        StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_KEYWORD_FILE);
            }
        }
    }
}
