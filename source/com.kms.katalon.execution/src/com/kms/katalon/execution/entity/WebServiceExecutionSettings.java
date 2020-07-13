package com.kms.katalon.execution.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.LongConsoleOption;
import com.kms.katalon.execution.setting.WebServiceExecutionSettingStore;
import com.kms.katalon.logging.LogUtil;

public class WebServiceExecutionSettings implements ConsoleOptionContributor {

    private Long maxResponseSize;

    private List<ConsoleOption<?>> options;
    {
        options = new ArrayList<>();
        options.add(new LongConsoleOption() {
            @Override
            public String getOption() {
                return RunConfiguration.REQUEST_MAX_RESPONSE_SIZE;
            }

            @Override
            public void setValue(String rawValue) {
                if (StringUtils.isBlank(rawValue)) {
                    return;
                }
                setMaxResponseSize(Long.valueOf(rawValue));
            }
        });
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return options;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (!hasArgument(consoleOption)) {
            return;
        }
        consoleOption.setValue(argumentValue);
    }

    private boolean hasArgument(ConsoleOption<?> consoleOption) {
        return options.contains(consoleOption);
    }

    private WebServiceExecutionSettingStore getStore() {
        return WebServiceExecutionSettingStore.getStore();
    }

    public long getMaxResponseSize() {
        if (maxResponseSize == null) {
            try {
                maxResponseSize = getStore().getMaxResponseSize();
            } catch (IOException exception) {
                LogUtil.logError(exception);
            }
        }
        return maxResponseSize;
    }

    public void setMaxResponseSize(long maxResponseSize) {
        this.maxResponseSize = maxResponseSize;
    }
}
