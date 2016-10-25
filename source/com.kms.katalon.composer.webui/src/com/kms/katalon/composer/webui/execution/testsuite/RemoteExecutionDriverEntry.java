package com.kms.katalon.composer.webui.execution.testsuite;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionDriverEntry;
import com.kms.katalon.composer.testsuite.collection.util.MapUtil;
import com.kms.katalon.composer.webui.component.dialogs.RemoteExecutionInputDialog;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.configuration.contributor.RemoteWebRunConfigurationContributor;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteExecutionDriverEntry extends TestExecutionDriverEntry {
    protected RemoteExecutionDriverEntry(String groupName) {
        super(WebUIDriverType.REMOTE_WEB_DRIVER, groupName, ImageConstants.IMG_URL_16_REMOTE_WEB);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(ColumnViewer parent) {
        return new DialogCellEditor((Composite) parent.getControl()) {
            @Override
            protected void updateContents(Object value) {
                Map<String, String> newValueMap = MapUtil.convertObjectToStringMap(value);
                if (newValueMap == null) {
                    super.updateContents(value);
                    return;
                }
                super.updateContents(MapUtil.buildStringForMap(newValueMap));
            }

            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                Map<String, String> newValueMap = MapUtil.convertObjectToStringMap(getValue());
                String remoteDriverTypeData = newValueMap
                        .get(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_TYPE_KEY);
                RemoteExecutionInputDialog dialog = new RemoteExecutionInputDialog(
                        Display.getCurrent().getActiveShell(),
                        newValueMap.get(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_KEY),
                        remoteDriverTypeData != null ? RemoteWebDriverConnectorType.valueOf(remoteDriverTypeData)
                                : null);
                int returnValue = dialog.open();
                if (returnValue != Dialog.OK) {
                    return newValueMap;
                }
                newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_KEY,
                        dialog.getRemoteServerUrl());
                newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_TYPE_KEY,
                        dialog.getRemoveDriverType().toString());
                return newValueMap;
            }
        };
    }
}
