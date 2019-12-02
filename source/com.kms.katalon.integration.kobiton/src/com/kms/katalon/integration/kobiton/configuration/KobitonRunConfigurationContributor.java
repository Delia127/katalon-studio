package com.kms.katalon.integration.kobiton.configuration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.contributor.WebUIRunConfigurationContributor;
import com.kms.katalon.integration.kobiton.constants.IntegrationKobitonMessages;
import com.kms.katalon.integration.kobiton.constants.KobitonPreferenceConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class KobitonRunConfigurationContributor extends WebUIRunConfigurationContributor {
    private static final int PREFERRED_ORDER = 8;

    private KobitonDevice selectedDevice;

    @Override
    public String getId() {
        return WebUIDriverType.KOBITON_WEB_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        if (selectedDevice != null) {
            return getKobitonConfiguration(projectDir, selectedDevice);
        }
        return new KobitonRunConfiguration(projectDir);
    }

    /**
     * Get Kobiton configuration from Test Suite Collection
     */
    @Override
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription)
            throws IOException, ExecutionException, InterruptedException {
        KobitonDevice device = JsonUtil.fromJson(runConfigurationDescription.getRunConfigurationData()
                .get(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY), KobitonDevice.class);

        return getKobitonConfiguration(projectDir, device);
    }

    private IRunConfiguration getKobitonConfiguration(String projectDir, KobitonDevice device) throws IOException {
        KobitonRunConfiguration runConfiguration = new KobitonRunConfiguration(projectDir);
        runConfiguration.setKobitonDevice(device);
        runConfiguration.setApiKey(KobitonPreferencesProvider.getKobitonApiKey());
        runConfiguration.setUserName(KobitonPreferencesProvider.getKobitonUserName());
        runConfiguration.setKobitonDevice(device);
        return runConfiguration;
    }

    @Override
    public int getPreferredOrder() {
        return PREFERRED_ORDER;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Arrays.asList(getKobitonTokenIdConsoleOpt(StringUtils.EMPTY),
                getKobitonDeviceIdConsoleOpt(StringUtils.EMPTY));
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if(consoleOption.getOption().equals("kobitonDeviceId")) {
            int kobitonDeviceId = Integer.valueOf(argumentValue);
            
            List<KobitonDevice> availableDevices = KobitonApiProvider
                    .getKobitonFavoriteDevices(KobitonPreferencesProvider.getKobitonToken());
            Optional<KobitonDevice> selectedDeviceOpt = availableDevices.stream()
                    .filter(device -> device.getId() == kobitonDeviceId)
                    .findAny();
            if (!selectedDeviceOpt.isPresent()) {
                throw new ExecutionException(
                        MessageFormat.format(IntegrationKobitonMessages.MSG_ERR_KOBITON_DEVICE_NOT_FOUND, kobitonDeviceId));
            }

            selectedDevice = selectedDeviceOpt.get();
        } else {
            IPreferenceStore store = PreferenceStoreManager
                    .getPreferenceStore(KobitonPreferenceConstants.KOBITON_QUALIFIER);
            if (consoleOption instanceof StringConsoleOption) {
                store.setValue(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN, argumentValue);
                KobitonPreferencesProvider.saveKobitonToken(argumentValue);

            }
        }
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description) {
        KobitonDevice device = JsonUtil.fromJson(
                description.getRunConfigurationData().get(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY),
                KobitonDevice.class);
        String tokenId = KobitonPreferencesProvider.getKobitonToken();
        return Arrays.asList(getKobitonTokenIdConsoleOpt(tokenId),
                getKobitonDeviceIdConsoleOpt(Integer.toString(device.getId())));
    }

    private ConsoleOption<?> getKobitonDeviceIdConsoleOpt(final String rawValue) {
        return new StringConsoleOption() {

            @Override
            public String getOption() {
                return "kobitonDeviceId";
            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public String getValue() {
                return rawValue;
            }
        };
    }
    
    private ConsoleOption<?> getKobitonTokenIdConsoleOpt(final String rawValue) {
        return new StringConsoleOption() {

            @Override
            public String getOption() {
                return "kobitonTokenId";
            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public String getValue() {
                return rawValue;
            }
        };
    }


}
