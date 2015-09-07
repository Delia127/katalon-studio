package com.kms.katalon.composer.components.impl.util;

import com.kms.katalon.constants.IdConstants;

public class EntityPartUtil {
	public static String getTestCaseCompositePartId(String testCasePk) {
		return IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX + "(" + testCasePk + ")";
	}

	public static String getTestObjectPartId(String testObjectPk) {
		return IdConstants.TESTOBJECT_CONTENT_PART_ID_PREFIX + "(" + testObjectPk + ")";
	}

	public static String getTestSuiteCompositePartId(String testSuitePk) {
		return IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX + "(" + testSuitePk + ")";
	}
	
	public static String getTestDataPartId(String testDataPk) {
		return IdConstants.TESTDATA_CONTENT_PART_ID_PREFIX + "(" + testDataPk + ")";           
	}
	
	public static String getReportPartId(String reportPk) {
		return IdConstants.REPORT_CONTENT_PART_ID_PREFIX + "(" + reportPk + ")";           
	}
}
