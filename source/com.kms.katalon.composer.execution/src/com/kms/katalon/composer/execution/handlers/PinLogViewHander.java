package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class PinLogViewHander {

	@SuppressWarnings("restriction")
	@Execute
	public void execute(@Optional MDirectToolItem item) {
		try {
			ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
					PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
			switch (item.getElementId()) {
			case IdConstants.LOG_VIEWER_MENU_ITEM_PIN_ID:
				store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_PIN_LOG, item.isSelected());
				break;
			default:
				break;
			}
			store.save();
		} catch (IOException e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}

	}
}
