package com.kms.katalon.util;

import java.util.Random;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.activation.ActivationService;
import com.kms.katalon.activation.ActivationServiceConsumer;
import com.kms.katalon.activation.dialog.ActivationDialogV2;
import com.kms.katalon.activation.dialog.ActivationOfflineDialogV2;
import com.kms.katalon.activation.dialog.SignupDialog;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.intro.QuickStartDialog;
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
            if (!isActivated && !checkActivationDialog()) {
                return false;
            }
        }

        if (!isActivated) {
            showFunctionsIntroductionForTheFirstTime();
        }
        return true;
    }

    private static boolean checkActivationDialog() {
        int result = new ActivationDialogV2(null).open();
        switch (result) {
            case ActivationDialogV2.OK:
                return true;
            case ActivationDialogV2.REQUEST_SIGNUP_CODE:
                return checkSignupDialog();
            case ActivationDialogV2.REQUEST_OFFLINE_CODE:
                return checkOfflineActivationDialog(false);
            default:
                return false;
        }
    }

    private static boolean checkOfflineActivationDialog(boolean navigateFromSignUp) {
        int result = new ActivationOfflineDialogV2(null, navigateFromSignUp).open();
        switch (result) {
            case ActivationOfflineDialogV2.OK:
                return true;
            case ActivationOfflineDialogV2.REQUEST_ONLINE_CODE:
                return checkActivationDialog();
            case ActivationOfflineDialogV2.REQUEST_SIGNUP_CODE:
                return checkSignupDialog();
            default:
                return false;
        }
    }

    private static boolean checkSignupDialog() {
        int result = new SignupDialog(null).open();
        switch (result) {
            case SignupDialog.OK:
//                SignupSurveyDialog dialog = new SignupSurveyDialog(null);
//                dialog.open();
                return true;
            case SignupDialog.REQUEST_ACTIVATION_CODE:
                return checkActivationDialog();
            case SignupDialog.REQUEST_OFFLINE_CODE:
                return checkOfflineActivationDialog(true);
            default:
                return false;
        }
    }

    private static void showFunctionsIntroductionForTheFirstTime() {
//        FunctionsIntroductionDialog dialog = new FunctionsIntroductionDialog(null);
//        dialog.open();
//        FunctionsIntroductionFinishDialog finishDialog = new FunctionsIntroductionFinishDialog(null);
//        finishDialog.open();
        QuickStartDialog dialog = new QuickStartDialog(null);
        
        // Dialog.CANCEL means open project in this case, checkout QuickStartDialog for more details
        if(dialog.open() == Dialog.CANCEL) {
        	try {
				new CommandCaller().call(CommandId.PROJECT_OPEN);
			} catch (CommandException e) {
				 LogUtil.logError(e);
			}
        }else {
            
            try {
                new CommandCaller().call(CommandId.PROJECT_ADD);
            } catch (CommandException e) {
                LogUtil.logError(e);
            }
            
        }
        
//        if (finishDialog.open() == Dialog.OK) {
//            try {
//                new CommandCaller().call(CommandId.PROJECT_ADD);
//            } catch (CommandException e) {
//                LogUtil.logError(e);
//            }
//        }
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
