package com.kms.katalon.composer.handlers;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.toolbar.PerspectiveSwitcher;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.services.PerspectiveRestoreService;

/**
 * Reset Window Perspective handler
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=404231#c6
 *
 */
public class ResetPerspectiveHandler extends AbstractHandler {

    /** Alias of org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor.MODEL_ELEMENT_ID */
    private static final String COMPATIBILITY_EDITOR_ID = "org.eclipse.e4.ui.compatibility.editor";

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    private MWindow window;

    private PerspectiveSwitcher switcher;

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        window = application.getChildren().get(0);

        // get current active perspective
        MPerspective perspective = modelService.getActivePerspective(window);
        if (perspective == null) {
            return;
        }

        MToolControl toolControl = (MToolControl) modelService.find(IdConstants.PERSPECTIVE_SWITCHER_TOOL_CONTROL_ID,
                application);
        switcher = (PerspectiveSwitcher) toolControl.getObject();

        // get perspective stack
        MPerspectiveStack perspectiveStack = switcher.find(IdConstants.MAIN_PERSPECTIVE_STACK_ID, window);

        // get application context
        IEclipseContext appContext = application.getContext();

        // get perspective restore service
        PerspectiveRestoreService restoreService = appContext.get(PerspectiveRestoreService.class);
        if (perspective == null || perspectiveStack == null || restoreService == null) {
            return;
        }

        // restore the old state (will only work if an add-on added an PerspectiveRestoreService implementation)
        MPerspective cleanPerspective = restoreService.reloadPerspective(perspective.getElementId(), window);
        if (cleanPerspective == null || !perspective.equals(perspectiveStack.getSelectedElement())) {
            return;
        }

        IEclipseContext context = window.getContext();
        if (context == null) {
            context = appContext;
        }
        EPartService partService = context.get(EPartService.class);
        Collection<MPart> parts = partService.getParts();

        // Relocate IComposerPart
        MPartStack composerPartStack = switcher.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        MPlaceholder area = switcher.find(IdConstants.SHARE_AREA_ID, application);
        List<MStackElement> composerPartStackChildren = composerPartStack.getChildren();
        MStackElement firstVisibleTab = null;
        // Move all opened entities back to composer area
        for (MPart p : parts) {
            String elementId = p.getElementId();
            if ((elementId.startsWith("com.kms.katalon.composer.content.") && elementId.endsWith(")")
                    || COMPATIBILITY_EDITOR_ID.equals(elementId)) && modelService.find(elementId, area) == null) {
                // not in MArea
                composerPartStackChildren.add(p);
                if (p.isVisible() && firstVisibleTab == null) {
                    firstVisibleTab = p;
                }
            }
        }
        if (firstVisibleTab != null) {
            // reselect the first visible tab
            composerPartStack.setSelectedElement(firstVisibleTab);
        }

        MPart eventLogPart = partService.findPart(IdConstants.EVENT_LOG_PART_ID);

        if (IdConstants.KEYWORD_PERSPECTIVE_ID.equals(perspective.getElementId())) {
            MPart requestHistoryPart = partService.findPart(IdConstants.COMPOSER_REQUEST_HISTORY_PART_ID);
            cleanPerspective.setToBeRendered(true);
            List<MPerspective> perspectives = perspectiveStack.getChildren();
            int pIndex = perspectives.indexOf(perspective);
            perspectives.remove(pIndex);
            perspectives.add(pIndex, cleanPerspective);

            MPlaceholder expressionsPlaceholder = switcher.find(IdConstants.EVENT_LOG_PLACEHOLDER_ID, cleanPerspective);
            if (eventLogPart != null) {
                expressionsPlaceholder.setRef(eventLogPart);
            }
            perspectiveStack.setSelectedElement(cleanPerspective);

            updatePerspectiveToolbar(pIndex, cleanPerspective);
            partService.switchPerspective(cleanPerspective);
            reselectParts(cleanPerspective, partService);

            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_PARTSTACK_EXPLORER_ID, application);
            if (requestHistoryPart != null) {
                stack.getChildren().add(requestHistoryPart);
            }

            eventBroker.post(EventConstants.EXPLORER_RELOAD_DATA, false);
            return;
        }

        if (IdConstants.DEBUG_PERSPECTIVE_ID.equals(perspective.getElementId())) {
            // relocate the shared parts back into positions
            switcher.reopenPartsIfClosed(perspective);
            relocatePartsBackIntoPosition(perspective);
        }
    }

    private void updatePerspectiveToolbar(int pIndex, MPerspective perspective) {
        ToolItem perspectiveItem = switcher.getToolbar().getItem(pIndex);
        perspectiveItem.setData(perspective);
        perspectiveItem.setSelection(true);
    }

    private void reselectParts(MPerspective perspective, EPartService ps) {
        // Keyword Browser Part
        selectAndActivateChildPart(perspective, ps, IdConstants.COMPOSER_PARTSTACK_LEFT_OUTLINE_ID, 0);

        // Properties Part
        selectAndActivateChildPart(perspective, ps, IdConstants.OUTLINE_PARTSTACK_ID, 0);

        // Explorer Part
        selectAndActivateChildPart(perspective, ps, IdConstants.COMPOSER_PARTSTACK_EXPLORER_ID, 0);
    }

    private void relocatePartsBackIntoPosition(MPerspective perspective) {
        MPartStack topLeftPartStack = switcher.find(IdConstants.DEBUG_TOP_LEFT_PART_STACK_ID, perspective);
        MPlaceholder debugPlaceholder = switcher.find(IdConstants.DEBUG_PLACEHOLDER_ID, perspective);
        relocatePartPlaceholder(topLeftPartStack, debugPlaceholder, -1);

        MPartStack topRightPartStack = switcher.find(IdConstants.DEBUG_TOP_RIGHT_PART_STACK_ID, perspective);
        MPlaceholder variablePlaceholder = switcher.find(IdConstants.DEBUG_VARIABLE_PLACEHOLDER_ID, perspective);
        relocatePartPlaceholder(topRightPartStack, variablePlaceholder, 0);

        MPlaceholder breakpointsPlaceholder = switcher.find(IdConstants.DEBUG_BREAKPOINT_PLACEHOLDER_ID, perspective);
        relocatePartPlaceholder(topRightPartStack, breakpointsPlaceholder, 1);

        MPlaceholder expressionsPlaceholder = switcher.find(IdConstants.DEBUG_EXPRESSION_PLACEHOLDER_ID, perspective);
        relocatePartPlaceholder(topRightPartStack, expressionsPlaceholder, 2);

        MPartStack consolePartStack = switcher.find(IdConstants.CONSOLE_PART_STACK_ID, perspective);
        MPlaceholder consolePlaceholder = switcher.find(IdConstants.ECLIPSE_CONSOLE_PART_ID, perspective);
        relocatePartPlaceholder(consolePartStack, consolePlaceholder, -1);
    }

    /**
     * @param parentPartStack the destination parent part stack
     * @param childPlaceholder the placeholder wants to move
     * @param childIndex childPlaceholder index. Put -1 index will be ignored.
     */
    private void relocatePartPlaceholder(MPartStack parentPartStack, MPlaceholder childPlaceholder, int childIndex) {
        if (childPlaceholder.getParent().equals(parentPartStack)) {
            return;
        }
        List<MStackElement> children = parentPartStack.getChildren();
        if (childIndex == -1) {
            children.add(childPlaceholder);
            return;
        }
        children.add(childIndex, childPlaceholder);
    }

    private void selectAndActivateChildPart(MPerspective perspective, EPartService partService, String partStackId,
            int selectIndex) {
        MPartStack partStack = switcher.find(partStackId, perspective);
        if (partStack == null) {
            return;
        }

        // This is a non-null list
        List<MStackElement> leftOutlineChildren = partStack.getChildren();
        if (leftOutlineChildren.isEmpty()) {
            return;
        }

        MStackElement part = leftOutlineChildren.get(selectIndex);
        partStack.setSelectedElement(part);
        partService.activate((MPart) part);
    }

}
