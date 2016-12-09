package com.kms.katalon.execution.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MailPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    public static final String MAIL_CONFIG_USERNAME_DEFAULT_VALUE = "E.g: testemailkms@gmail.com";

    public static final String MAIL_CONFIG_PORT_DEFAULT_VALUE = "E.g: 465";

    public static final String MAIL_CONFIG_HOST_DEFAULT_VALUE = "E.g: smtp.gmail.com";

    private static final String MAIL_CONFIG_SECURITY_PROTOCOL_DEFAULT_VALUE = MailUtil.MailSecurityProtocolType.SSL
            .toString();

    private static final boolean MAIL_CONFIG_ATTACHMENT_DEFAULT_VALUE = false;

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
        store.setDefault(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL,
                MAIL_CONFIG_SECURITY_PROTOCOL_DEFAULT_VALUE);
        store.setDefault(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT, MAIL_CONFIG_ATTACHMENT_DEFAULT_VALUE);
    }

}
