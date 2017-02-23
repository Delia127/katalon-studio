package com.kms.katalon.composer.components;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class CustomWorkbenchRendererFactory extends WorkbenchRendererFactory {

    private CustomAreaRenderer renderer;

    private CToolBarManagerRenderer toolbarRenderer;

    private CStackRenderer stackRenderer;

    @Inject
    @Preference(nodePath = "org.eclipse.e4.ui.workbench.renderers.swt")
    private IEclipsePreferences preferences;
    
    @Inject
    private IPresentationEngine presentationEngine;
    
    @Override
    public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
        if (uiElement instanceof MArea && uiElement.getElementId().equals(IdConstants.SHARE_AREA_ID)) {
            if (renderer == null) {
                renderer = new CustomAreaRenderer();
                initRenderer(renderer);
            }
            return renderer;
        }

        // Handle for toolbar
        if (uiElement instanceof MToolBar) {
            if (toolbarRenderer == null) {
                toolbarRenderer = new CToolBarManagerRenderer();
                initRenderer(toolbarRenderer);
            }
            return toolbarRenderer;
        }

        // Handle for main MPartStack
        if (uiElement instanceof MPartStack) {
            if (stackRenderer == null) {
                stackRenderer = new CStackRenderer(presentationEngine, preferences);
                initRenderer(stackRenderer);
            }
            return stackRenderer;
        }

        return super.getRenderer(uiElement, parent);
    }

}
