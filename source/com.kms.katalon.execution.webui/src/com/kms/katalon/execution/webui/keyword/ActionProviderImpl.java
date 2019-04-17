package com.kms.katalon.execution.webui.keyword;

import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.keyword.IActionProvider;
import com.kms.katalon.core.keyword.ICustomProfile;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class ActionProviderImpl implements IActionProvider {

	public void saveCustomProfile(ICustomProfile customProfile) {
		String strPluginDriverType = customProfile.getWebDriverType();
		WebUIDriverType driverType = WebUIDriverType.fromStringValue(strPluginDriverType);
		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		try {
			CustomRunConfiguration runConfig = new CustomRunConfiguration(projectEntity.getFolderLocation(),
					customProfile.getName());
			IDriverConnector driverConnector = WebUIExecutionUtil.getDriverConnector(driverType,
					runConfig.getConfigFolder().getAbsolutePath());
			driverConnector.setUserConfigProperties(customProfile.getDesiredCapabilities());
			runConfig.addDriverConnector(customProfile.getName(), driverConnector);
			runConfig.save();
			CustomKeywordRunConfigurationCollector.getInstance().addCustomKeywordRunConfiguration(runConfig);

			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Operation status",
					"Operation has been executed");
		} catch (IOException | ExecutionException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Operation status",
					"Operation has failed due to " + e.getMessage());
		}
	}
}
