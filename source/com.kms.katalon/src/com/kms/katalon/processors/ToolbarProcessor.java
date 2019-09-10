package com.kms.katalon.processors;

import java.util.Comparator;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

public class ToolbarProcessor {

    public static final String KATALON_TOOLITEM_NEW_ID = "com.kms.katalon.composer.toolbar.new";

    public static final String KATALON_MAIN_TOOLBAR_ID = "com.kms.katalon.composer.toolbar";

    public static final String KATALON_MAIN_GENERIC_TOOLBAR_ID = "com.kms.katalon.composer.toolbar.generic";

    public static final String KATALON_MAIN_WEBSERVICE_TOOLBAR_ID = "com.kms.katalon.composer.toolbar.webservice";

    public static final String KATALON_EXECUTION_TOOLBAR_ID = "com.kms.katalon.composer.toolbar.execution";
    
    public static final String KATALON_ACCOUNT_TOOLBAR_ID = "com.kms.katalon.composer.toolbar.account";
    
    public static final String KATALON_ACCOUNT_ID = "com.kms.katalon.composer.toolbar.account";
    
    public static final String KATALON_TOOLITEM_ACCOUNT_ID = "com.kms.katalon.composer.toolbar.account.toolitem";

    public static final String KATALON_MENUITEM_ACCOUNT_ID = "com.kms.katalon.composer.toolbar.account.toolitem.menu.username";
    
    public static final String[] TOOLBAR_IDS = new String[] {
            KATALON_MAIN_TOOLBAR_ID,
            KATALON_MAIN_WEBSERVICE_TOOLBAR_ID,
            KATALON_MAIN_GENERIC_TOOLBAR_ID,
            KATALON_EXECUTION_TOOLBAR_ID
    };

    private static final String INDEX_KEY = "index";

    private MHandledToolItem newToolItem;

    @Inject
    private EModelService modelService;

    @Execute
    public void run(@Optional IEclipseContext context, MApplication app) {
        for (String id : TOOLBAR_IDS) {
            MUIElement uiElement = modelService.find(id, app);
            if (uiElement == null) {
                return;
            }

            EList<MToolBarElement> toolItems = (EList<MToolBarElement>) ((MToolBar) uiElement).getChildren();
            ECollections.sort(toolItems, new CustomComparator(toolItems.size()));

            // Initial disabled icon
            toolItems.forEach(item -> {
                String disabledIconURI = item.getPersistedState().get(IPresentationEngine.DISABLED_ICON_IMAGE_KEY);
                if (disabledIconURI == null) {
                    return;
                }
                item.getTransientData().put(IPresentationEngine.DISABLED_ICON_IMAGE_KEY, disabledIconURI);
                if (newToolItem == null && KATALON_TOOLITEM_NEW_ID.equals(item.getElementId())) {
                    newToolItem = (MHandledToolItem) item;
                }
            });

            if (newToolItem == null) {
                return;
            }

            MMenu mMenu = newToolItem.getMenu();
            if (mMenu == null) {
                return;
            }
            EList<MMenuElement> menuItems = (EList<MMenuElement>) mMenu.getChildren();
            ECollections.sort(menuItems, new CustomComparator(menuItems.size()));

            // cache New tool item in eclipse context
            context.set(KATALON_TOOLITEM_NEW_ID, newToolItem);
        }
    }

    private class CustomComparator implements Comparator<MUIElement> {

        private int numberOfItem;

        public CustomComparator(int numberOfItem) {
            this.numberOfItem = numberOfItem;
        }

        @Override
        public int compare(MUIElement item1, MUIElement item2) {
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
    }

}
