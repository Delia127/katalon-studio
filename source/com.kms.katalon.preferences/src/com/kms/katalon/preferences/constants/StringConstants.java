package com.kms.katalon.preferences.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// PreferencesRegistry
	public static final String INL_LOG_WARN_UNEXPECTED_ELEMENT_X = "unexpected element: {0}";
	public static final String INL_LOG_WARN_MISSING_ID_AND_OR_NAME = "missing id and/or name: {}";
	public static final String INL_LOG_ERROR_EXPECTED_INSTANCE_OF_PREF_PAGE = "Expected instance of PreferencePage: {0}";
	public static final String INL_LOG_WARN_CANNOT_SET_PREF_STORE_FOR_PAGE = "Unable to set the preferenceStore for page {0} defined in bundle {1}";
	public static final String INL_LOG_WARN_MISSING_PLUGIN_ID_IN_EXT = "missing plugin Id in extension {0} check the plugin {1}";
	public static final String INL_LOG_WARN_IN_EXT_ONLY_1_OR_2_ATTR_MUST_BE_SET = "In extension {0} only one of the two attributes (pluginId or idInWorkbenchContext) must be set. Check the plugin {1}";
	public static final String INL_LOG_WARN_IN_EXT_CLASS_MUST_IMPL_IPREFERENCESTOREPROVIDER = "In extension {0} the class must implements IPreferenceStoreProvider. Check the plugin {1}";

	// ScopedPreferenceStore
	public static final String INL_DO_NOT_ADD_DEFAULT_TO_SEARCH_CONTEXTS = "Do not add the default to the search contexts";
}
