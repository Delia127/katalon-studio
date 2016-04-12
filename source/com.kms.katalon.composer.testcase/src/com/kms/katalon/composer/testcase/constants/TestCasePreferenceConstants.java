package com.kms.katalon.composer.testcase.constants;

import com.kms.katalon.constants.PreferenceConstants;

public interface TestCasePreferenceConstants extends PreferenceConstants {
    String TESTCASE_DEFAULT_VARIABLE_TYPE = "default.variableType";

    String TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE = "auto.generateDefaultVariableType";

    String TESTCASE_AUTO_EXPORT_VARIABLE = "auto.exportVariable";

    String TESTCASE_DEFAULT_KEYWORDS = "default.keywords";

    String TESTCASE_DEFAULT_KEYWORD_TYPE = "default.keywordType";
    
    String TESTCASE_DEFAULT_FAILURE_HANDLING = "default.failureHandling";
    
    String TESTCASE_PART_DEFAULT_START_VIEW = "default.startView";
    
    String MANUAL = "manual.";

    String MANUAL_ALLOW_LINE_WRAPPING = MANUAL + "allow_line_wrapping";

    String MANUAL_MAXIMUM_LINE_WIDTH = MANUAL + "maximum_line_width";
}
