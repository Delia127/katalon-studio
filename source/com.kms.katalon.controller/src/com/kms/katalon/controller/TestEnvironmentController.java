package com.kms.katalon.controller;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.entity.testenvironment.TestEnvironmentEntity;

@Creatable
public class TestEnvironmentController extends EntityController {
    public static final String[] PAGELOAD_TIMEOUT_VALUES = { "Default", "10", "30", "60", "90" };

    public static final String PAGELOAD_TIMEOUT_DEFAULT = PAGELOAD_TIMEOUT_VALUES[0];

    public static final short PAGELOAD_TIMEOUT_MIN_VALUE = 0;

    public static final short PAGELOAD_TIMEOUT_MAX_VALUE = 9999;

    private static EntityController _instance;

    private TestEnvironmentController() {
        super();
    }

    public static TestEnvironmentController getInstance() {
        if (_instance == null) {
            _instance = new TestEnvironmentController();
        }
        return (TestEnvironmentController) _instance;
    }

    public List<TestEnvironmentEntity> getAllTestEnv() throws Exception {
        List<TestEnvironmentEntity> testEnvironments = new ArrayList<TestEnvironmentEntity>();
        return testEnvironments;
    }

    public String[] getPageLoadTimeOutValues() {
        return PAGELOAD_TIMEOUT_VALUES;
    }

    public short getPageLoadTimeOutDefaultValue() {
        return (short) getPreferenceStore(PreferenceConstants.EXECUTION_QUALIFIER).getInt(
                PreferenceConstants.EXECUTION_DEFAULT_TIMEOUT);
    }

    public short getPageLoadTimeOutMinimumValue() {
        return PAGELOAD_TIMEOUT_MIN_VALUE;
    }

    public short getPageLoadTimeOutMaximumValue() {
        return PAGELOAD_TIMEOUT_MAX_VALUE;
    }

    public String getPageLoadTimeOutDefaultValueByString() {
        return PAGELOAD_TIMEOUT_DEFAULT;
    }
}
