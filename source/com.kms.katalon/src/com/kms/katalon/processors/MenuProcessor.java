package com.kms.katalon.processors;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.StringConstants;

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
    @Execute
    public void run(@Optional IEclipseContext context, MApplication app) {
        try {
            MMenu menu = app.getChildren().get(0).getMainMenu();
            if (menu == null) {
                menu = initMainMenu();
                app.getChildren().get(0).setMainMenu(menu);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MMenu initMainMenu() {
        // Main menu
        MMenu menu = MMenuFactory.INSTANCE.createMenu();
        menu.setElementId(IdConstants.MAIN_MENU_ID);

        // File menu
        MMenu mFile = createMenu(IdConstants.MENU_ID_FILE, StringConstants.MENU_LBL_FILE);
        mFile.getChildren().add(createMenu(IdConstants.MENU_ID_FILE_NEW, StringConstants.MENU_LBL_FILE_NEW));
        mFile.getChildren().add(
                createHanldedMenuItem(IdConstants.MENU_ID_FILE_SAVE, StringConstants.MENU_LBL_FILE_SAVE,
                        StringConstants.MENU_ICON_URI_FILE_SAVE,
                        createCommand(IdConstants.SAVE_COMMAND_ID, StringConstants.MENU_CMD_NAME_FILE_SAVE)));
        mFile.getChildren().add(
                createHanldedMenuItem(
                        IdConstants.MENU_ID_FILE_SAVE_ALL,
                        StringConstants.MENU_LBL_FILE_SAVE_ALL,
                        StringConstants.MENU_ICON_URI_FILE_SAVE_ALL,
                        createCommand(IdConstants.MENU_CMD_ID_FILE_SAVE_ALL,
                                StringConstants.MENU_CMD_NAME_FILE_SAVE_ALL)));
        mFile.getChildren().add(
                createHanldedMenuItem(IdConstants.MENU_ID_FILE_QUIT, StringConstants.MENU_LBL_FILE_QUIT,
                        StringConstants.MENU_ICON_URI_FILE_QUIT,
                        createCommand(IdConstants.QUIT_COMMAND_ID, StringConstants.MENU_CMD_NAME_FILE_QUIT)));

        // Edit menu
        MMenu mEdit = createMenu(IdConstants.MENU_ID_EDIT, StringConstants.MENU_LBL_EDIT);

        // Report menu
        MMenu mReport = createMenu(IdConstants.MENU_ID_REPORT, StringConstants.MENU_LBL_REPORT);

        // Settings menu
        MMenu mSettings = createMenu(IdConstants.MENU_ID_SETTINGS, StringConstants.MENU_LBL_SETTINGS);

        // Settings menu
        MMenu mSearch = createMenu(IdConstants.MENU_ID_SEARCH, StringConstants.MENU_LBL_SEARCH);
        mSearch.getChildren().add(
                createHanldedMenuItem(IdConstants.MENU_ID_SEARCH_, StringConstants.MENU_LBL_SEARCH_, null,
                        createCommand(IdConstants.MENU_CMD_ID_SEARCH_, StringConstants.MENU_CMD_NAME_SEARCH_)));

        // Settings menu
        MMenu mProject = createMenu(IdConstants.MENU_ID_PROJECT, StringConstants.MENU_LBL_PROJECT);

        // Window menu
        MMenu mWindow = createMenu(IdConstants.MENU_ID_WINDOW, StringConstants.MENU_LBL_WINDOW);
        mWindow.getChildren().add(
                createHanldedMenuItem(
                        IdConstants.MENU_ID_WINDOW_RESET_PERSPECTIVE,
                        StringConstants.MENU_LBL_WINDOW_RESET_PERSPECTIVE,
                        null,
                        createCommand(IdConstants.MENU_CMD_ID_WINDOW_RESET_PERSPECTIVE,
                                StringConstants.MENU_CMD_NAME_WINDOW_RESET_PERSPECTIVE)));

        // Settings menu
        MMenu mHelp = createMenu(IdConstants.MENU_ID_HELP, StringConstants.MENU_LBL_HELP);
        mHelp.getChildren().add(
                createHanldedMenuItem(IdConstants.MENU_ID_HELP_ABOUT, StringConstants.MENU_LBL_HELP_ABOUT, null,
                        createCommand(IdConstants.MENU_CMD_ID_HELP_ABOUT, StringConstants.MENU_CMD_NAME_HELP_ABOUT)));

        menu.getChildren().add(mFile);
        menu.getChildren().add(mEdit);
        menu.getChildren().add(mReport);
        menu.getChildren().add(mSettings);
        menu.getChildren().add(mSearch);
        menu.getChildren().add(mProject);
        menu.getChildren().add(mWindow);
        menu.getChildren().add(mHelp);

        return menu;
    }

    private MMenu createMenu(String id, String label) {
        MMenu menu = MMenuFactory.INSTANCE.createMenu();
        menu.setElementId(id);
        menu.setLabel(label);
        return menu;
    }

    private MHandledMenuItem createHanldedMenuItem(String id, String label, String iconURI, MCommand command) {
        MHandledMenuItem menu = MMenuFactory.INSTANCE.createHandledMenuItem();
        menu.setElementId(id);
        menu.setLabel(label);
        menu.setIconURI(iconURI);
        menu.setCommand(command);
        return menu;
    }

    private MCommand createCommand(String id, String name) {
        MCommand command = MCommandsFactory.INSTANCE.createCommand();
        command.setElementId(id);
        command.setCommandName(name);
        return command;
    }
}
