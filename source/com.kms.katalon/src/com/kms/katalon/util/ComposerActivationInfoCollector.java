package com.kms.katalon.util;

import java.util.Random;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.activation.ActivationService;
import com.kms.katalon.activation.ActivationServiceConsumer;
import com.kms.katalon.activation.dialog.ActivationDialog;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;
import com.kms.katalon.composer.intro.FunctionsIntroductionFinishDialog;
import com.kms.katalon.composer.project.constants.CommandId;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.service.Trackings;

public class ComposerActivationInfoCollector extends ActivationInfoCollector {

    private static final long RANDOM_MIN = 78364164096L;

    private static final long RANDOM_MAX = 2821109907455L;

    private ComposerActivationInfoCollector() {
        super();
    }

    public static boolean checkActivation() {
        boolean isActivated = isActivated();
        if (!isActivated) {
            // Send anonymous info for the first time using
            Trackings.trackOpenFirstTime();
        }
        ActivationService activationService = ActivationServiceConsumer.getServiceInstance();
        if (activationService != null) {
            boolean activated = activationService.checkActivation(Display.getCurrent().getActiveShell());
            if (!activated) {
                return false;
            }
        } else {
            if (isActivated) {
                return true;
            }
            int result = new ActivationDialog(null).open();
            if (result == Window.CANCEL) {
                return false;
            }
        }
        if (!isActivated) {
            showFunctionsIntroductionForTheFirstTime();
        }
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

    public static String getActivationRequestCode() {
        String requestCodePropName = ApplicationStringConstants.APP_PROP_ACTIVATION_REQUEST_CODE;
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
