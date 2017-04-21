package com.kms.katalon.composer.handlers;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;

@SuppressWarnings("restriction")
public class CloseHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        MPart part = partService.getActivePart();

        if (getCompositeParentPart(part, partService) != null) {
            return true;
        } else if (part != null) {
            return true;
        }
        return false;
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
        MPart part = partService.getActivePart();

        MPart parentCompositePart = getCompositeParentPart(part, partService);
        if (parentCompositePart != null) {
            if (partService.savePart(parentCompositePart, true)) {
                partService.hidePart(parentCompositePart);
            }
        } else {
            if (part.getObject() instanceof CompatibilityEditor) {
                part = ((CompatibilityEditor) part.getObject()).getModel();
            }
            if (partService.savePart(part, true)) {
                partService.hidePart(part);
            }
        }
    }

}
