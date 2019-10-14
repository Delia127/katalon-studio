package com.kms.katalon.composer.integration.kobiton.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerIntegrationKobitonMessageConstants;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.session.RemoteMobileExecutionSession;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public class KobitonExecutionDynamicContribution extends AbstractExecutionMenuContribution {
    private static final String KOBITON_ICON_URI = ImageManager.getImageURLString(IImageKeys.KOBITON_16);

    private static final String KOBITON_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.integration.kobiton.execution.command.kobiton"; //$NON-NLS-1$

    @Override
    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (!KobitonPreferencesProvider.isKobitonIntegrationAvailable()) {
            return;
        }
        List<ExecutionSession> exisingDrivers = getAllAvailableKobitonSession(getDriverTypeName());
        if (exisingDrivers.isEmpty()) {
            aboutToShowDefault(items);
        } else {
            aboutToShowWithExisting(items, exisingDrivers);
        }
    }

    public List<ExecutionSession> getAllAvailableKobitonSession(String driverTypeName) {
        List<ExecutionSession> executionSessions = new ArrayList<>();
        for (ExecutionSession executionSession : ExecutionSessionSocketServer.getInstance().getAllExecutionSession()) {
            if (executionSession instanceof RemoteMobileExecutionSession
                    && driverTypeName.equals(((RemoteMobileExecutionSession) executionSession).getRemoteType())
                    && executionSession.isAvailable()) {
                executionSessions.add(executionSession);
            }
        }
        return executionSessions;
    }

    @Override
    protected String getIconUri() {
        return KOBITON_ICON_URI;
    }

    @Override
    protected String getMenuLabel() {
        return ComposerIntegrationKobitonMessageConstants.LBL_MENU_EXECUTION_KOBITON;
    }

    @Override
    protected String getCommandId() {
        return KOBITON_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getDriverTypeName() {
        return WebUIDriverType.KOBITON_WEB_DRIVER.getName();
    }

}
