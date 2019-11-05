package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.List;

public class OsgiConsoleOptionContributor implements ConsoleOptionContributor {
    public static final String OSGI_CONSOLE_LOG_OPTION = "consoleLog";
    public static final String DEFAULT_CONSOLE_LOG_OPTION = Boolean.TRUE.toString();

    public static final String OSGI_NO_EXIT_OPTION = "noExit";
    public static final String DEFAULT_NO_EXIT_OPTION = Boolean.FALSE.toString();

    public static final String API_KEY_OPTION = "apiKey";

    public static final String API_KEY_ON_PREMISE_OPTION = "apiKeyOnPremise";

    public static final String ANALYTICS_PROJECT_ID = "analyticsProjectId";
    
    public static final StringConsoleOption CONSOLE_LOG_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return OSGI_CONSOLE_LOG_OPTION;
        }

        @Override
        public String getDefaultArgumentValue() {
            return DEFAULT_CONSOLE_LOG_OPTION;
        }
    };

    public static final StringConsoleOption NO_EXIT_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return OSGI_NO_EXIT_OPTION;
        }

        @Override
        public String getDefaultArgumentValue() {
            return DEFAULT_NO_EXIT_OPTION;
        }
    };

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(CONSOLE_LOG_CONSOLE_OPTION);
        consoleOptionList.add(NO_EXIT_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        // Do nothing for OSGI console options
    }
}