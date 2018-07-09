package com.kms.katalon.composer.execution.trace;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectStyleRangeMatcher extends ArtifactStyleRangeMatcher {

    private static final String TEST_OBJECT_ID_PATTERN = "'Object Repository\\/([^']*)'";

    @Override
    public String getPattern() {
        return TEST_OBJECT_ID_PATTERN;
    }

    @Override
    protected void internalClick(String testObjectId) {
        WebElementEntity webElement = getTestObject(testObjectId);
        if (webElement == null) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                    MessageFormat.format(ComposerExecutionMessageConstants.WARN_TEST_OBEJCT_NOT_FOUND, testObjectId));
            return;
        }

        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TEST_OBJECT_OPEN, webElement);
    }

    private WebElementEntity getTestObject(String testObjectId) {
        WebElementEntity webElement = null;
        try {
            webElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(testObjectId);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return webElement;
    }
}
