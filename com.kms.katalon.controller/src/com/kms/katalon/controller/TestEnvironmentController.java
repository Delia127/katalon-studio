package com.kms.katalon.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.common.Util;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.entity.testenvironment.TestEnvironmentEntity;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@Creatable
public class TestEnvironmentController extends EntityController {
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
		return Util.PAGELOAD_TIMEOUT_VALUES;
	}

	public short getPageLoadTimeOutDefaultValue() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
		return (short) store.getInt(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT);
	}

	public short getPageLoadTimeOutMinimumValue() {
		return Util.PAGELOAD_TIMEOUT_MIN_VALUE;
	}

	public short getPageLoadTimeOutMaximumValue() {
		return Util.PAGELOAD_TIMEOUT_MAX_VALUE;
	}

	public String getPageLoadTimeOutDefaultValueByString() {
		return Util.PAGELOAD_TIMEOUT_DEFAULT;
	}
}
