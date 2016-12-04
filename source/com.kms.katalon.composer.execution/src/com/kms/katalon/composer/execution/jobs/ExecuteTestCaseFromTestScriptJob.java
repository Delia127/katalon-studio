package com.kms.katalon.composer.execution.jobs;

import java.io.IOException;

import org.eclipse.e4.ui.di.UISynchronize;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.configuration.ExistingRunConfiguration;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class ExecuteTestCaseFromTestScriptJob extends ExecuteTestCaseJob {
    private String rawScript;

    public ExecuteTestCaseFromTestScriptJob(String name,
            ExistingRunConfiguration runConfig, TestCaseEntity testCase, LaunchMode launchMode, UISynchronize sync, String rawScript) {
        super(name, runConfig, testCase, launchMode, sync);
        this.rawScript = rawScript;
    }

    @Override
    protected void buildScripts() throws IOException, ExecutionException {
        super.buildScripts();
        ((DefaultExecutionSetting) runConfig.getExecutionSetting()).setRawScript(rawScript);
    }
}
