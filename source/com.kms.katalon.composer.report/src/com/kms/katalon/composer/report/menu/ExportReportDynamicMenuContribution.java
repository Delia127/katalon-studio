package com.kms.katalon.composer.report.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class ExportReportDynamicMenuContribution {

	private static final String EXPORT_CSV_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.csv";
	private static final String EXPORT_HTML_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.html";
	private static final String EXPORT_PDF_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.pdf";
	
	private static final String CSV_REPORT_POPUPMENUITEM_LABEL = "CSV";

	private static final String HTML_REPORT_POPUPMENUITEM_LABEL = "HTML";
	
	private static final String PDF_REPORT_POPUPMENUITEM_LABEL = "PDF";

	

	private static final String EXPORT_POPUPMENU_LABEL = "Export as";

	@Inject
	private ECommandService commandService;

	@Inject
	private ESelectionService selectionService;

	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
		try {
			Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			if (canExecute(selectedObjects)) {
				MMenu newMenu = MenuFactory
						.createPopupMenu(EXPORT_POPUPMENU_LABEL, ConstantsHelper.getApplicationURI());
				if (newMenu != null) {
					MHandledMenuItem exportHTML = MenuFactory.createPopupMenuItem(
							commandService.createCommand(EXPORT_HTML_REPORT_COMMAND_ID, null),
							HTML_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
					if (exportHTML != null) {
						newMenu.getChildren().add(exportHTML);
					}

					MHandledMenuItem exportCSVMenuItem = MenuFactory.createPopupMenuItem(
							commandService.createCommand(EXPORT_CSV_REPORT_COMMAND_ID, null),
							CSV_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
					if (exportCSVMenuItem != null) {
						newMenu.getChildren().add(exportCSVMenuItem);
					}					
					
					MHandledMenuItem exportPDFMenuItem = MenuFactory.createPopupMenuItem(
                            commandService.createCommand(EXPORT_PDF_REPORT_COMMAND_ID, null),
                            PDF_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
                    if (exportPDFMenuItem != null) {
                        newMenu.getChildren().add(exportPDFMenuItem);
                    }
                    menuItems.add(newMenu);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}

	public static boolean canExecute(Object[] selectedObjects) {
		if (selectedObjects == null) return false;
		
		boolean isHandled = true;
		for (Object selectedObject : selectedObjects) {
			if (!(selectedObject instanceof ReportTreeEntity)) {
				isHandled = false;
				break;
			}
		}
		return isHandled;
	}

}