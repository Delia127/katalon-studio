package com.kms.katalon.composer.handlers;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.parts.SavableCompositePart;
import com.kms.katalon.constants.EventConstants;

/**
 * Handle close action when user hit Ctrl(Command) + W<br>
 * This should prevent the unwanted close action on unclosable part
 */
@SuppressWarnings("restriction")
public class CloseHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        MPart part = partService.getActivePart();
        return getCompositeParentPart(part, partService) != null || (part != null && part.isCloseable());
    }

    private MPart getCompositeParentPart(MPart part, EPartService partService) {
        for (MPart dirtyPart : partService.getParts()) {
            if (dirtyPart.getObject() instanceof SavableCompositePart) {
                SavableCompositePart compositePart = (SavableCompositePart) dirtyPart.getObject();
                List<MPart> childrenParts = compositePart.getChildParts();
                if (childrenParts != null && compositePart.getChildParts().contains(part)) {
                    return dirtyPart;
                }
            }
        }
        return null;
    }

    @Override
    public void execute() {
        MPart part = partService.getActivePart();

        MPart parentCompositePart = getCompositeParentPart(part, partService);
        if (parentCompositePart != null) {
            if (partService.savePart(parentCompositePart, true)) {
                partService.hidePart(parentCompositePart);

                if (parentCompositePart instanceof IComposerPart) {
                    eventBroker.post(EventConstants.WORKSPACE_DRAFT_PART_CLOSED,
                            ((IComposerPart) parentCompositePart).getPartId());
                }
            }
        } else {
            if (part.getObject() instanceof CompatibilityEditor) {
                part = ((CompatibilityEditor) part.getObject()).getModel();
            }
            if (partService.savePart(part, true)) {
                partService.hidePart(part);
            }
            if (part instanceof IComposerPart) {
                eventBroker.post(EventConstants.WORKSPACE_DRAFT_PART_CLOSED,
                        ((IComposerPart) part).getPartId());
            }
        }
    }

}
