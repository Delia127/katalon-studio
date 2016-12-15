package com.kms.katalon.execution.launcher;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.util.StringUtil;

public interface IConsoleLauncher extends ILauncher {

    static final int ONE_HUNDRED = 100;

    String getStatusMessage(int consoleWidth);

    default String getDefaultStatusMessage(int consoleWidth) {
        String launcherName = getName();
        StringBuilder builder = new StringBuilder(launcherName).append(getStatusFormat(getResult()));
        builder.insert(launcherName.length(),
                StringUtils.repeat(GlobalStringConstants.CR_DOT, consoleWidth - builder.length() % consoleWidth));
        return StringUtil.wrap(builder.toString(), consoleWidth);
    }

    default String getStatusFormat(ILauncherResult launcherResult) {
        return launcherResult.getExecutedTestCases() + "/" + launcherResult.getTotalTestCases() + "("
                + getStatusByPercentage(launcherResult) + "%)";
    }

    default String getStatusByPercentage(ILauncherResult launcherResult) {
        return Integer.toString(Math.round(
                ((float) launcherResult.getExecutedTestCases() / launcherResult.getTotalTestCases()) * ONE_HUNDRED));
    }
}
