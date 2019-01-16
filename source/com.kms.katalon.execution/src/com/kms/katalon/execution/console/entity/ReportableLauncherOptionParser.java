package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kms.katalon.execution.entity.DefaultReportSetting;
import com.kms.katalon.execution.entity.DefaultRerunSetting;

public abstract class ReportableLauncherOptionParser implements LauncherOptionParser {
    protected DefaultReportSetting reportableSetting;

    protected DefaultRerunSetting rerunSetting;
        
    public ReportableLauncherOptionParser() {
        reportableSetting = new DefaultReportSetting();
        rerunSetting = new DefaultRerunSetting();
    }

    protected List<ConsoleOptionContributor> getContributors() {
        return Arrays.asList(reportableSetting, rerunSetting);
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = new ArrayList<>();
        getContributors().forEach(contributor -> allOptions.addAll(
                contributor.getConsoleOptionList()));
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        for (ConsoleOptionContributor contributor : getContributors()) {
            if (contributor.getConsoleOptionList().contains(consoleOption)) {
                contributor.setArgumentValue(consoleOption, argumentValue);
            }
        }
    }
	
}
