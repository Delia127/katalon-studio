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

public class KobitonConsoleOptionContributor extends PreferenceOptionContributor {
    
    public static final String KOBITON_QUALIFIER = "com.kms.katalon.integration.kobiton";
    
    public static final String KOBITON_AUTHENTICATION_USERNAME = "kobiton.authentication.username";
    
    public static final String KOBITON_AUTHENTICATION_PASSWORD = "kobiton.authentication.password";
    
    private List<ConsoleOption<?>> options;

    public KobitonConsoleOptionContributor() {
        options = new ArrayList<>();

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return KOBITON_AUTHENTICATION_USERNAME;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return KOBITON_AUTHENTICATION_PASSWORD;
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
                .getPreferenceStore(KOBITON_QUALIFIER);
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
        return KOBITON_QUALIFIER;
    }

}
