package com.kms.katalon.composer.initializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ContentAssistProposalInitializer implements ApplicationInitializer {

    private static final String DEFAULT_PROPOSAL_CATEGORY = "org.eclipse.jdt.ui.defaultProposalCategory";

    private static final String GROOVY_CODEASSIST_PROPOSAL = "org.codehaus.groovy.eclipse.codeassist.category";

    private static final String JDT_PREF_ID = "org.eclipse.jdt.ui";

    private static final String CONTENT_ASSIST_CATEGORY_ORDER = "content_assist_category_order";

    private static final String CONTENT_ASSIST_DISABLED_COMPUTERS = "content_assist_disabled_computers";

    private static final String CONTENT_ASSIST_PROPOSAL_ID = "com.kms.katalon.proposal";

    private static final String CONTENT_ASSIST_PROPOSAL_ID_WITH_INDEX = CONTENT_ASSIST_PROPOSAL_ID + ":0";

    private static final String SEPARATOR = "\u0000";

    // private static final String CONTENT_ASSIST_AUTOACTIVATION_TRIGGER_JAVA = "content_assist_autoactivation_triggers_java";

    private static final String CONTENT_ASSIST_AUTOACTIVATION_DELAY = "content_assist_autoactivation_delay";

    // private static final String AUTOACTIVATION_TRIGGER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.";

    private static final int AUTOACTIVATION_DELAY_IN_MILLIS = 500;

    private ScopedPreferenceStore jdtStore = PreferenceStoreManager.getPreferenceStore(JDT_PREF_ID);

    @Override
    public void setup() {
        if (isNotFirstTimeUsed()) {
            return;
        }
        topUpKatalonStudioProposal();

        disableGroovyProposal();

        enableAutoActivationTrigger();

        updateFistTimeUsed();
        try {
            jdtStore.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void enableAutoActivationTrigger() {
        //jdtStore.setValue(CONTENT_ASSIST_AUTOACTIVATION_TRIGGER_JAVA, AUTOACTIVATION_TRIGGER);

        jdtStore.setValue(CONTENT_ASSIST_AUTOACTIVATION_DELAY, AUTOACTIVATION_DELAY_IN_MILLIS);
    }

    private boolean isNotFirstTimeUsed() {
        return jdtStore.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED);
    }

    private void updateFistTimeUsed() {
        jdtStore.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, true);
    }

    private void topUpKatalonStudioProposal() {
        List<String> proposals = new ArrayList<>();
        String katalonProposalDescription = CONTENT_ASSIST_PROPOSAL_ID_WITH_INDEX;
        for (String p : jdtStore.getString(CONTENT_ASSIST_CATEGORY_ORDER).split(SEPARATOR)) {
            if (p.startsWith(CONTENT_ASSIST_PROPOSAL_ID)) {
                katalonProposalDescription = p;
                continue;
            }
            proposals.add(p);
        }
        proposals.add(0, katalonProposalDescription);
        jdtStore.setValue(CONTENT_ASSIST_CATEGORY_ORDER, StringUtils.join(proposals, SEPARATOR));
    }

    private void disableGroovyProposal() {
        Set<String> proposals = new LinkedHashSet<>();
        proposals.add(GROOVY_CODEASSIST_PROPOSAL);
        proposals.add(DEFAULT_PROPOSAL_CATEGORY);
        for (String p : jdtStore.getString(CONTENT_ASSIST_DISABLED_COMPUTERS).split(SEPARATOR)) {
            proposals.add(p);
        }
        jdtStore.setValue(CONTENT_ASSIST_DISABLED_COMPUTERS, StringUtils.join(proposals, SEPARATOR));
    }
}
