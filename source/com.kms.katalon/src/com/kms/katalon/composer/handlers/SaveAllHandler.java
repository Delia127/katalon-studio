package com.kms.katalon.composer.handlers;

import java.util.Collection;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;


public class SaveAllHandler {

    @CanExecute
    private boolean canExecute(@Optional EPartService partService) {
        if (partService != null) {
            return !partService.getDirtyParts().isEmpty();
        }
        return false;
    }

    private SavableCompositePart getCompositeParentPart(MPart part, EPartService partService) {
        SavableCompositePart parentCompositePart = null;
        for (MPart dirtyPart : partService.getDirtyParts()) {
            if (dirtyPart.getObject() instanceof SavableCompositePart) {
                SavableCompositePart compositePart = (SavableCompositePart) dirtyPart.getObject();
                if (compositePart.getChildParts() != null && compositePart.getChildParts().contains(part)) {
                    return compositePart;
                }
            }
        }
        return parentCompositePart;
    }
    
    @Execute
    void execute(EPartService partService) {
        try {
            Collection<MPart> partsToPersist = partService.getDirtyParts();
            
            for (MPart part : partsToPersist) {
                if (!part.isDirty()) {
                    continue;
                }
                if (part.getObject() instanceof SavableCompositePart) {
                    ((SavableCompositePart) part.getObject()).save();
                } else {
                    SavableCompositePart compositeParentPart = getCompositeParentPart(part, partService);
                    if (compositeParentPart != null) {
                        compositeParentPart.save();
                    } else if (GroovyEditorUtil.isGroovyEditorPart(part)) {
                        GroovyEditorUtil.saveEditor(part);
                        EventBrokerSingleton.getInstance()
                                            .getEventBroker()
                                            .post(EventConstants.ECLIPSE_EDITOR_SAVED, part);
                    } else {
                        partService.savePart(part, false);
                    }
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_SAVE_ALL_DIA_MSG);
            LoggerSingleton.logError(e);
        } finally {
//            Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
//                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.SAVE_ALL, RunningMode.GUI)));
//            sendEventForTracking();
        }
    }
}
