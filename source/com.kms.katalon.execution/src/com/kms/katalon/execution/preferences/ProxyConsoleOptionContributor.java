package com.kms.katalon.execution.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.execution.console.entity.BooleanConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.IntegerConsoleOption;
import com.kms.katalon.execution.console.entity.PreferenceOptionContributor;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ProxyConsoleOptionContributor extends PreferenceOptionContributor {

    private List<ConsoleOption<?>> options;

    public ProxyConsoleOptionContributor() {
        options = new ArrayList<>();

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_OPTION;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_SERVER_ADDRESS;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_SERVER_TYPE;
            }
        });

        options.add(new IntegerConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_SERVER_PORT;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_USERNAME;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_PASSWORD;
            }
        });
        
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.PROXY_EXCEPTION_LIST;
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
                .getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
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
        return ExecutionPreferenceConstants.EXECUTION_QUALIFIER;
    }
}
