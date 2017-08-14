package com.kms.katalon.composer.execution.trace;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataStyleRangeMatcher extends ArtifactStyleRangeMatcher {

    private static final String TEST_DATA_ID_PATTERN = "'Data Files\\/([^']*)'";

    @Override
    public String getPattern() {
        return TEST_DATA_ID_PATTERN;
    }

    @Override
    protected void internalClick(String testDataId) {
        DataFileEntity testData = getTestData(testDataId);
        if (testData == null) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                    MessageFormat.format(ComposerExecutionMessageConstants.WARN_TEST_DATA_NOT_FOUND, testDataId));
            return;
        }
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TEST_DATA_OPEN, testData);
    }

    private DataFileEntity getTestData(String testDataId) {
        DataFileEntity testData = null;
        try {
            testData = TestDataController.getInstance().getTestDataByDisplayId(testDataId);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return testData;
    }

}
