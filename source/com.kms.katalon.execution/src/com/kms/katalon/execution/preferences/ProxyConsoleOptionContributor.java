package com.kms.katalon.execution.preferences;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.execution.console.entity.BooleanConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.IntegerConsoleOption;
import com.kms.katalon.execution.console.entity.PreferenceOptionContributor;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.util.CryptoUtil;

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

        // Authentication Proxy
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_OPTION;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_SERVER_ADDRESS;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_SERVER_TYPE;
            }
        });

        options.add(new IntegerConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_SERVER_PORT;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_USERNAME;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_PASSWORD;
            }
        });
        
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.AUTH_PROXY_EXCEPTION_LIST;
            }
        });

        // System Proxy
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_OPTION;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_ADDRESS;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_TYPE;
            }
        });

        options.add(new IntegerConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_PORT;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_USERNAME;
            }
        });

        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD;
            }
        });
        
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_EXCEPTION_LIST;
            }
        });
        
        options.add(new StringConsoleOption() {
            @Override
            public String getOption() {
                return ProxyPreferenceConstants.SYSTEM_PROXY_APPLY_TO_DESIRED_CAPABILITIES;
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
            String value = isSensitiveInfo(consoleOption.getOption())
                    ? decodeSensitiveInfo(argumentValue)
                    : argumentValue;
            store.setValue(consoleOption.getOption(), value);
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

    private boolean isSensitiveInfo(String sensityInfoName) {
        @SuppressWarnings({ "serial" })
        List<String> sensitiveArgs = new ArrayList<String>() {
            {
                add(ProxyPreferenceConstants.AUTH_PROXY_PASSWORD);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD);
            }
        };
        return sensitiveArgs.contains(sensityInfoName);
    }

    private String decodeSensitiveInfo(String sensitiveInfo) {
        if (StringUtils.isBlank(sensitiveInfo)) {
            return StringUtils.EMPTY;
        }

        try {
            CryptoUtil.CrytoInfo cryptoInfo = CryptoUtil.getDefault(sensitiveInfo);
            return CryptoUtil.decode(cryptoInfo);
        } catch (GeneralSecurityException | IOException error) {
            LogUtil.logError(error);
            return StringUtils.EMPTY;
        }
    }
}
