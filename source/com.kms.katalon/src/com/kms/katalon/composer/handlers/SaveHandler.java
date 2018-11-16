package com.kms.katalon.composer.handlers;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;

public class SaveHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        MPart part = getPartService().getActivePart();
        if (getCompositeParentPart(part) != null) {
            return true;
        } else if (part != null) {
            return part.isDirty();
        }
        return false;
    }

    private SavableCompositePart getCompositeParentPart(MPart part) {
        SavableCompositePart parentCompositePart = null;
        for (MPart dirtyPart : getPartService().getDirtyParts()) {
            if (dirtyPart.getObject() instanceof SavableCompositePart) {
                SavableCompositePart compositePart = (SavableCompositePart) dirtyPart.getObject();
                if (compositePart.getChildParts() != null && compositePart.getChildParts().contains(part)) {
                    return compositePart;
                }
            }
        }
        return parentCompositePart;
    }

    @Override
    public void execute() {
        MPart part = getPartService().getActivePart();
        try {
            SavableCompositePart parentCompositePart = getCompositeParentPart(part);
            if (parentCompositePart != null) {
                if (parentCompositePart.getChildParts().contains(part)) {
                    parentCompositePart.save();
                }
            } else {
                if (GroovyEditorUtil.isGroovyEditorPart(part)) {
                    GroovyEditorUtil.saveEditor(part);
                    eventBroker.post(EventConstants.ECLIPSE_EDITOR_SAVED, part);
                } else {
                    getPartService().savePart(part, false);
                }
            }

//            Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
//                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.SAVE_ALL, RunningMode.GUI)));

        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_SAVE_DIA_MSG);
            LoggerSingleton.logError(e);
        }
    }
}
