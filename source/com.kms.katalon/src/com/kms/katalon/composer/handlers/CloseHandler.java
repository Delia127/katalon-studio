package com.kms.katalon.composer.handlers;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;

/**
 * Handle close action when user hit Ctrl(Command) + W<br>
 * This should prevent the unwanted close action on unclosable part
 */
@SuppressWarnings("restriction")
public class CloseHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        MPart part = getPartService().getActivePart();
        return getCompositeParentPart(part, getPartService()) != null || (part != null && part.isCloseable());
    }

    private MPart getCompositeParentPart(MPart part, EPartService partService) {
        for (MPart dirtyPart : partService.getParts()) {
            if (dirtyPart.getObject() instanceof MultipleTabsCompositePart) {
                MultipleTabsCompositePart compositePart = (MultipleTabsCompositePart) dirtyPart.getObject();
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
        MPart part = getPartService().getActivePart();

        MPart parentCompositePart = getCompositeParentPart(part, getPartService());
        if (parentCompositePart != null) {
            if (getPartService().savePart(parentCompositePart, true)) {
                getPartService().hidePart(parentCompositePart);
            }
        } else {
            if (part.getObject() instanceof CompatibilityEditor) {
                part = ((CompatibilityEditor) part.getObject()).getModel();
            }
            if (getPartService().savePart(part, true)) {
                getPartService().hidePart(part);
            }
        }
    }

}
