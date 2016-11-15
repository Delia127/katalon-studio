package com.kms.katalon.processors;

import java.util.Comparator;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

public class ToolbarProcessor {

    public static final String KATALON_TOOLBAR_ID = "com.kms.katalon.composer.toolbar";

    private static final String INDEX_KEY = "index";

    @Inject
    EModelService modelService;

    @Execute
    public void run(@Optional IEclipseContext context, MApplication app) {
        MUIElement uiElement = modelService.find(KATALON_TOOLBAR_ID, app);
        if (uiElement == null) {
            return;
        }

        EList<MToolBarElement> toolItems = (EList<MToolBarElement>) ((MToolBar) uiElement).getChildren();
        final int numberOfItem = toolItems.size();
        ECollections.sort(toolItems, new Comparator<MToolBarElement>() {

            @Override
            public int compare(MToolBarElement item1, MToolBarElement item2) {
                Map<String, String> persistedState1 = item1.getPersistedState();
                Map<String, String> persistedState2 = item2.getPersistedState();
                int index1 = numberOfItem;
                int index2 = numberOfItem;
                if (persistedState1.containsKey(INDEX_KEY)) {
                    index1 = Integer.valueOf(persistedState1.get(INDEX_KEY));
                }
                if (persistedState2.containsKey(INDEX_KEY)) {
                    index2 = Integer.valueOf(persistedState2.get(INDEX_KEY));
                }
                return index1 - index2;
            }
        });

        // Initial disabled icon
        toolItems.forEach(item -> {
            String disabledIconURI = item.getPersistedState().get(IPresentationEngine.DISABLED_ICON_IMAGE_KEY);
            if (disabledIconURI == null) {
                return;
            }
            item.getTransientData().put(IPresentationEngine.DISABLED_ICON_IMAGE_KEY, disabledIconURI);
        });
    }

}
