package com.kms.katalon.composer.report.menu;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.DirectMenuItemImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.report.handlers.ExportTestSuiteCollectionReportHandler;
import com.kms.katalon.composer.report.handlers.ExportTestSuiteReportHandler;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.composer.report.platform.ExportReportProviderReflection;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class ExportReportDynamicMenuContribution {

    public static final String REPORT_ITEM_TRANSIENT_DATA = "menuItemObject";

    private static final String BUNDLECLASS_URI = "bundleclass://com.kms.katalon.composer.report/";

    private static final String EXPORT_TSC_HTML_REPORT_COMMAND_ID = "com.kms.katalon.composer.report.command.export.html.tsc";

    private static final String HTML_REPORT_POPUPMENUITEM_LABEL = "HTML";

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
        List<ExportReportProviderPlugin> exportReportPluginProviders = ReportComposerIntegrationFactory.getInstance()
                .getExportReportPluginProviders();
        if (exportReportPluginProviders.isEmpty()) {
            return;
        }

        MMenu newMenu = MenuFactory.createPopupMenu(EXPORT_POPUPMENU_LABEL, ConstantsHelper.getApplicationURI());
        if (newMenu == null) {
            return;
        }
        for (ExportReportProviderPlugin provider : exportReportPluginProviders) {
            ExportReportProviderReflection reflection = new ExportReportProviderReflection(provider);
            try {
                for (String supportedType : reflection.getSupportedFormatTypeForTestSuite()) {
                    String contributionUri = BUNDLECLASS_URI + ExportTestSuiteReportHandler.class.getName();
                    DirectMenuItemImpl directMenu = (DirectMenuItemImpl) MenuFactory.createDirectMenuItem(supportedType,
                            ConstantsHelper.getApplicationURI());
                    Map<String, Object> trasientData = new HashMap<>();
                    trasientData.put(REPORT_ITEM_TRANSIENT_DATA,
                            new ExportReportMenuItemObject(supportedType, provider));
                    directMenu.eSet(MenuPackageImpl.DIRECT_MENU_ITEM__TRANSIENT_DATA, trasientData);
                    directMenu.setContributionURI(contributionUri);
                    newMenu.getChildren().add(directMenu);
                }
            } catch (MalformedURLException | CoreException e) {
                LoggerSingleton.logError(e);
            }
        }

        menuItems.add(newMenu);
    }

    public void showMenuInTestSuiteCollectionReport(List<MMenuElement> menuItems) {
        List<ExportReportProviderPlugin> exportReportPluginProviders = ReportComposerIntegrationFactory.getInstance()
                .getExportReportPluginProviders();
        if (exportReportPluginProviders.isEmpty()) {
            return;
        }

        MMenu newMenu = MenuFactory.createPopupMenu(EXPORT_POPUPMENU_LABEL, ConstantsHelper.getApplicationURI());
        if (newMenu == null) {
            return;
        }
        for (ExportReportProviderPlugin provider : exportReportPluginProviders) {
            ExportReportProviderReflection reflection = new ExportReportProviderReflection(provider);
            try {
                for (String supportedType : reflection.getSupportedFormatTypeForTestSuiteCollection()) {
                    String contributionUri = BUNDLECLASS_URI + ExportTestSuiteCollectionReportHandler.class.getName();
                    DirectMenuItemImpl directMenu = (DirectMenuItemImpl) MenuFactory.createDirectMenuItem(supportedType,
                            ConstantsHelper.getApplicationURI());
                    Map<String, Object> trasientData = new HashMap<>();
                    trasientData.put(REPORT_ITEM_TRANSIENT_DATA,
                            new ExportReportMenuItemObject(supportedType, provider));
                    directMenu.eSet(MenuPackageImpl.DIRECT_MENU_ITEM__TRANSIENT_DATA, trasientData);
                    directMenu.setContributionURI(contributionUri);
                    newMenu.getChildren().add(directMenu);
                }
            } catch (MalformedURLException | CoreException e) {
                LoggerSingleton.logError(e);
            }
        }

        menuItems.add(newMenu);
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
