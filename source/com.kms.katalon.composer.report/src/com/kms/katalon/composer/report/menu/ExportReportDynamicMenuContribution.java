package com.kms.katalon.composer.report.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class ExportReportDynamicMenuContribution {

    private static final String EXPORT_CSV_SUMMARY_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.csv.summary";

    private static final String EXPORT_CSV_DETAILS_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.csv";

    private static final String EXPORT_HTML_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.html";

    private static final String EXPORT_TSC_HTML_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.html.tsc";

    private static final String EXPORT_PDF_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.pdf";

    private static final String EXPORT_JUNIT_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.junit";

    private static final String CSV_SUMMARY_REPORT_POPUPMENUITEM_LABEL = "CSV (Summary Report)";

    private static final String CSV_DETAILS_REPORT_POPUPMENUITEM_LABEL = "CSV (Details Report)";

    private static final String HTML_REPORT_POPUPMENUITEM_LABEL = "HTML";

    private static final String PDF_REPORT_POPUPMENUITEM_LABEL = "PDF";

    private static final String JUNIT_REPORT_POPUPMENUITEM_LABEL = "JUnit";

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
                showMenuInTestSuiteReport(menuItems);
                return;
            }

            if (getFirstSelectedCollectionReport(selectedObjects) != null) {
                showMenuInTestSuiteCollectionReport(menuItems);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public void showMenuInTestSuiteReport(List<MMenuElement> menuItems) {
        MMenu newMenu = MenuFactory.createPopupMenu(EXPORT_POPUPMENU_LABEL, ConstantsHelper.getApplicationURI());
        if (newMenu != null) {
            MHandledMenuItem exportHTML = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_HTML_REPORT_COMMAND_ID, null), HTML_REPORT_POPUPMENUITEM_LABEL,
                    ConstantsHelper.getApplicationURI());
            if (exportHTML != null) {
                newMenu.getChildren().add(exportHTML);
            }

            MHandledMenuItem exportCSVSummaryMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_CSV_SUMMARY_REPORT_COMMAND_ID, null),
                    CSV_SUMMARY_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (exportCSVSummaryMenuItem != null) {
                newMenu.getChildren().add(exportCSVSummaryMenuItem);
            }

            MHandledMenuItem exportCSVMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_CSV_DETAILS_REPORT_COMMAND_ID, null),
                    CSV_DETAILS_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (exportCSVMenuItem != null) {
                newMenu.getChildren().add(exportCSVMenuItem);
            }

            MHandledMenuItem exportPDFMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_PDF_REPORT_COMMAND_ID, null), PDF_REPORT_POPUPMENUITEM_LABEL,
                    ConstantsHelper.getApplicationURI());
            if (exportPDFMenuItem != null) {
                newMenu.getChildren().add(exportPDFMenuItem);
            }

            MHandledMenuItem exportJUnitMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_JUNIT_REPORT_COMMAND_ID, null),
                    JUNIT_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (exportJUnitMenuItem != null) {
                newMenu.getChildren().add(exportJUnitMenuItem);
            }
            menuItems.add(newMenu);
        }
    }

    public void showMenuInTestSuiteCollectionReport(List<MMenuElement> menuItems) {
        MMenu newMenu = MenuFactory.createPopupMenu(EXPORT_POPUPMENU_LABEL, ConstantsHelper.getApplicationURI());
        if (newMenu != null) {
            MHandledMenuItem exportHTML = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(EXPORT_TSC_HTML_REPORT_COMMAND_ID, null),
                    HTML_REPORT_POPUPMENUITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (exportHTML != null) {
                newMenu.getChildren().add(exportHTML);
            }
            menuItems.add(newMenu);
        }
    }

    public boolean canExecute(Object[] selectedObjects) {
        if (selectedObjects == null || selectedObjects.length == 0) {
            return false;
        }

        boolean isHandled = true;
        for (Object selectedObject : selectedObjects) {
            if (!(selectedObject instanceof ReportTreeEntity)) {
                isHandled = false;
                break;
            }
        }
        return isHandled;
    }

    public static ReportCollectionTreeEntity getFirstSelectedCollectionReport(Object[] selectedObjects) {
        if (selectedObjects.length == 0 || !(selectedObjects[0] instanceof ReportCollectionTreeEntity)) {
            return null;
        }

        return (ReportCollectionTreeEntity) selectedObjects[0];
    }

}
