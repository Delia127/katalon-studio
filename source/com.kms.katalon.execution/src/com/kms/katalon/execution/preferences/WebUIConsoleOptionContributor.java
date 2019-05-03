package com.kms.katalon.execution.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.execution.console.entity.BooleanConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.IntegerConsoleOption;
import com.kms.katalon.execution.console.entity.PreferenceOptionContributor;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class WebUIConsoleOptionContributor extends PreferenceOptionContributor {
    public static final String WEB_UI_QUALIFIER = "com.kms.katalon.execution.webui";
    
    public static final String WEB_UI_AUTO_UPDATE_DRIVERS = "webui.autoUpdateDrivers";

    private List<ConsoleOption<?>> options; 
    {
        options = new ArrayList<>();
        options.add(new BooleanConsoleOption() {
            
            @Override
            public String getOption() {
                return WEB_UI_AUTO_UPDATE_DRIVERS;
            }
        });
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return options;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(WEB_UI_QUALIFIER);
        if (consoleOption instanceof StringConsoleOption) {
            store.setValue(consoleOption.getOption(), argumentValue);
            return;
        }
        if (consoleOption instanceof IntegerConsoleOption) {
            store.setValue(consoleOption.getOption(), Integer.valueOf(argumentValue));
            return;
        }

        if (consoleOption instanceof BooleanConsoleOption) {
            store.setValue(consoleOption.getOption(), Boolean.valueOf(argumentValue));
            return;
        }
    }

    @Override
    public String getPreferenceId() {
        return WEB_UI_QUALIFIER;
    }

}
