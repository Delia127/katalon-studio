package com.kms.katalon.composer.components.menu;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class MenuFactory {
    
    public static MDirectMenuItem createDirectMenuItem(String label, String contributorURI) {
        MDirectMenuItem directMenuItem = MMenuFactory.INSTANCE.createDirectMenuItem();
        directMenuItem.setLabel(label);

        return directMenuItem;
    }

	public static MHandledMenuItem createPopupMenuItem(ParameterizedCommand command, String label, String contributorURI) {
		if (command != null) {
			MHandledMenuItem popupMenuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
			popupMenuItem.setLabel(label);
			popupMenuItem.setWbCommand(command);
			
			// Create a blank command to avoid warning message
			popupMenuItem.setCommand(MCommandsFactory.INSTANCE.createCommand());
			
			popupMenuItem.setContributorURI(contributorURI);
			return popupMenuItem;
		}
		return null;
	}
	
	public static MMenu createPopupMenu(String label, String contributorURI) {
		MMenu popUpMenu = MMenuFactory.INSTANCE.createMenu();
		popUpMenu.setLabel(label);
		popUpMenu.setContributorURI(contributorURI);
		return popUpMenu;
	}
}
