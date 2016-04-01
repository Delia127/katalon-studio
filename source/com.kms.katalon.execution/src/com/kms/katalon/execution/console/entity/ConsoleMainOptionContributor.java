package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.console.ConsoleMain;

public class ConsoleMainOptionContributor implements ConsoleOptionContributor {
    public static final StringConsoleOption PROJECT_PATH_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.PROJECT_PK_OPTION;
        }

        @Override
        public boolean isRequired() {
            return true;
        }

        public String getDefaultArgumentValue() {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject != null) {
                return currentProject.getLocation();
            }
            return null;
        };
    };

    public static final StringConsoleOption TEST_SUITE_PATH_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.TESTSUITE_ID_OPTION;
        }

        @Override
        public boolean isRequired() {
            return true;
        }
    };

    public static final StringConsoleOption RUN_CONFIG_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.BROWSER_TYPE_OPTION;
        }

        @Override
        public boolean isRequired() {
            return true;
        }
    };

    public static final IntegerConsoleOption STATUS_DELAY_CONSOLE_OPTION = new IntegerConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.SHOW_STATUS_DELAY_OPTION;
        }

        @Override
        public String getDefaultArgumentValue() {
            return String.valueOf(ConsoleMain.DEFAULT_SHOW_PROGRESS_DELAY);
        }
    };

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(PROJECT_PATH_CONSOLE_OPTION);
        consoleOptionList.add(TEST_SUITE_PATH_CONSOLE_OPTION);
        consoleOptionList.add(RUN_CONFIG_CONSOLE_OPTION);
        consoleOptionList.add(STATUS_DELAY_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        // Do nothing for ConsoleMain console options
    }
}
