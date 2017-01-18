package com.kms.katalon.processors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;

/**
 * This menu processor is actually a workaround to fix the issue in Eclipse 4 RCP (4.2) where the application main menu
 * being NULL in the TrimmedWindow after the workbench is reloaded.
 * <p>
 * This issue happened when we need to use PersistedState to restore last opened session.
 * <p>
 * Note:This processor will be useless if we put <code>-clearPersistedState</code> in launching argument.
 * <p>
 * This Processor should run before fragment.
 * 
 * @author antruongnguyen
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=388808
 */
public class MenuProcessor {
    @Inject
    private EModelService modelService;

    @Execute
    public void run(MApplication app) {
        try {
            MWindow window = app.getChildren().get(0);
            // Fix an unknown bug "Application do not have active window"
            app.setSelectedElement(window);
            MMenu menu = window.getMainMenu();
            if (menu != null) {
                return;
            }

            menu = (MMenu) modelService.cloneSnippet(app, IdConstants.MAIN_MENU_ID, window);
            if (menu == null) {
                return;
            }
            window.setMainMenu(menu);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
