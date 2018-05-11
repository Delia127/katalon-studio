package com.kms.katalon.util;

import java.util.Random;
import java.util.concurrent.Executors;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;

import com.kms.katalon.activation.dialog.ActivationDialog;
import com.kms.katalon.application.RunningMode;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.application.usagetracking.UsageInfoCollector;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;
import com.kms.katalon.composer.intro.FunctionsIntroductionFinishDialog;
import com.kms.katalon.composer.project.constants.CommandId;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.logging.LogUtil;

public class ComposerActivationInfoCollector extends ActivationInfoCollector {

    private static final long RANDOM_MIN = 78364164096L;

    private static final long RANDOM_MAX = 2821109907455L;

    private static Boolean qTestActivated = false;
    private ComposerActivationInfoCollector() {
        super();
    }

    public static boolean checkActivation(final IEventBroker eventBroker) {
        Bundle qtestBundle = Platform.getBundle(IdConstants.QTEST_INTEGRATION_BUNDLE_ID);

        if (isActivated() && qtestBundle == null) {
            return true;
        }
        // Send anonymous info for the first time using
        Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector.collect(
                UsageInfoCollector.getAnonymousUsageInfo(UsageActionTrigger.OPEN_FIRST_TIME, RunningMode.GUI)));

        if (qtestBundle != null) {
            eventBroker.subscribe(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK_COMPLETED,
                    new EventServiceAdapter() {
                        @Override
                        public void handleEvent(Event event) {
                            qTestActivated = (Boolean) getObject(event);
                        }
                    });

            eventBroker.send(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK, null);

            if (!qTestActivated) {
                return false;
            }

        } else {
            int result = new ActivationDialog(null).open();
            if (result == Window.CANCEL) {
                return false;
            }
        }
        showFunctionsIntroductionForTheFirstTime();
        return true;

    }

    private static void showFunctionsIntroductionForTheFirstTime() {
        FunctionsIntroductionDialog dialog = new FunctionsIntroductionDialog(null);
        dialog.open();
        FunctionsIntroductionFinishDialog finishDialog = new FunctionsIntroductionFinishDialog(null);
        if (finishDialog.open() == Dialog.OK) {
            try {
                new CommandCaller().call(CommandId.PROJECT_ADD);
            } catch (CommandException e) {
                LogUtil.logError(e);
            }
        }
    }

    public static String genRequestActivationInfo() {
        String requestCodePropName = ApplicationStringConstants.REQUEST_CODE_PROP_NAME;
        String requestActivationCode = ApplicationInfo.getAppProperty(requestCodePropName);

        if (requestActivationCode == null || requestActivationCode.trim().length() < 1) {
            requestActivationCode = genRequestActivateOfflineCode();
            ApplicationInfo.setAppProperty(requestCodePropName, requestActivationCode, true);
        }
        return requestActivationCode;
    }

    private static String genRequestActivateOfflineCode() {
        Random random = new Random();
        long num = RANDOM_MIN + (long) ((RANDOM_MAX - RANDOM_MIN) * random.nextFloat());
        return Long.toString(num, 36).toUpperCase();
    }
}
