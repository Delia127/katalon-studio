package com.kms.katalon.composer.handlers;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.services.PerspectiveRestoreService;

/**
 * Reset Window Perspective handler
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=404231#c6
 *
 */
public class ResetPerspectiveHandler implements IHandler {
    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    private IEventBroker eventBroker;

    private MWindow window;

    @SuppressWarnings("unchecked")
    @Execute
    public void execute() {
        window = application.getChildren().get(0);

        MPerspective perspective = modelService.getActivePerspective(window);
        if (perspective == null) return;

        // get perspective stack
        MElementContainer<MUIElement> perspectiveParent = perspective.getParent();

        // get application context
        IEclipseContext appContext = application.getContext();

        PerspectiveRestoreService restoreService = appContext.get(PerspectiveRestoreService.class);
        if (restoreService == null) return;

        // restore the old state (will only work if an add-on added an PerspectiveRestoreService implementation)
        MPerspective state = restoreService.reloadPerspective(perspective.getElementId(), window);
        if (state != null) {
            boolean wasPerspectiveActive = perspective.equals(perspective.getParent().getSelectedElement());

            // switch to perspective only if it was active
            if (wasPerspectiveActive) {
                IEclipseContext context = window.getContext();
                if (context == null) {
                    context = appContext;
                }
                EPartService partService = context.get(EPartService.class);
                Collection<MPart> parts = partService.getParts();

                // Relocate IComposerPart
                MUIElement composerPartStack = modelService
                        .find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
                MUIElement area = modelService.find(IdConstants.SHARE_AREA_ID, application);

                // Move all opened entities back to composer area
                for (MPart p : parts) {
                    if (p.getElementId().startsWith("com.kms.katalon.composer.content.")
                            && p.getElementId().endsWith(")")
                            || "org.eclipse.e4.ui.compatibility.editor".equals(p.getElementId())) {

                        if (modelService.find(p.getElementId(), area) == null) {
                            // not in MArea
                            ((MElementContainer<MUIElement>) composerPartStack).getChildren().add(p);
                        }

                    }
                }

                state.setToBeRendered(true);
                perspectiveParent.getChildren().remove(0);
                perspectiveParent.getChildren().add(0, state);
                perspectiveParent.setSelectedElement(state);

                partService.switchPerspective(state);

                reselectParts(state, partService);

                // reload explorer tree entities
                eventBroker.post(EventConstants.EXPLORER_RELOAD_DATA, null);
            }
        }
    }

    private void reselectParts(MPerspective perspective, EPartService ps) {
        // Explorer Part
        MPartStack explorerPartStack = (MPartStack) modelService.find(IdConstants.COMPOSER_PARTSTACK_EXPLORER_ID,
                perspective);
        if (explorerPartStack != null && explorerPartStack.getChildren() != null
                && !explorerPartStack.getChildren().isEmpty()) {
            MPart explorerPart = (MPart) explorerPartStack.getChildren().get(0);
            explorerPartStack.setSelectedElement(explorerPart);
            ps.activate(explorerPart);
        }

        // Keyword Browser Part
        MPartStack leftOutlinePartStack = (MPartStack) modelService.find(
                IdConstants.COMPOSER_PARTSTACK_LEFT_OUTLINE_ID, perspective);
        if (leftOutlinePartStack != null && leftOutlinePartStack.getChildren() != null
                && !leftOutlinePartStack.getChildren().isEmpty()) {
            MPart keywordBrowserPart = (MPart) leftOutlinePartStack.getChildren().get(0);
            leftOutlinePartStack.setSelectedElement(keywordBrowserPart);
        }

        // Global Variable Part
        MPartStack rightOutlinePartStack = (MPartStack) modelService
                .find(IdConstants.OUTLINE_PARTSTACK_ID, perspective);
        if (rightOutlinePartStack != null && rightOutlinePartStack.getChildren() != null
                && !rightOutlinePartStack.getChildren().isEmpty()) {
            MPart globalVariablePart = (MPart) rightOutlinePartStack.getChildren().get(0);
            rightOutlinePartStack.setSelectedElement(globalVariablePart);
        }
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        execute();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }
}
