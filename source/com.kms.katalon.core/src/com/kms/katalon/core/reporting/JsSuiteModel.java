package com.kms.katalon.core.reporting;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class JsSuiteModel extends JsModel {

	private List<String> listStrings;
	private TestSuiteLogRecord suiteLog;
	private JsModel metaData;
	private JsModel subSuite;
	private List<JsTestModel> tests;
	private JsModel status;
	private JsModel suiteKeyword; // Setup, Teardown
	private JsModel sum;

	public JsSuiteModel(TestSuiteLogRecord suiteLog, List<String> listStrings) {
		super();
		this.suiteLog = suiteLog;
		this.listStrings = listStrings;
	}

	private void init() {
		if (listStrings.isEmpty()) {
			listStrings.add(EMPTY_STRING);
		}
		props.add(new JsModelProperty("Name", suiteLog.getName(), listStrings));
		props.add(new JsModelProperty("Source", suiteLog.getSource().replace("\\", "\\\\"), listStrings));
		props.add(new JsModelProperty("Relative Source", suiteLog.getSource().replace("\\", "\\\\"), listStrings));
		// suite doc, skip it
		props.add(new JsModelProperty("doc", EMPTY_STRING_INDEX, null));
		// Meta data, empty
		metaData = new JsModel();
		// Suite Status
		int[] totalFailsErrors = initStatus();
		// Sub-suite
		subSuite = new JsModel();
		// Child tests
		tests = new ArrayList<JsTestModel>();
		for (ILogRecord testLog : suiteLog.getChildRecords()) {
			if (testLog instanceof TestCaseLogRecord) {
				tests.add(new JsTestModel((TestCaseLogRecord) testLog, listStrings));
			}
		}
		// Keywords
		suiteKeyword = new JsModel();
		// Summary result
		initSummary(totalFailsErrors);
	}

	/**
	 * @return total failures & errors
	 **/
	private int[] initStatus() {

		status = new JsModel();

		// Status (0: index of STATUSES['FAIL', 'PASS', 'NOT_RUN'], 1:
		// startMillis, 2: elapsed, 3: message (if any))
		TestStatusValue suiteStat = TestStatusValue.PASSED;
		long suiteStartTime = suiteLog.getStartTime();
		long suiteEndTime = suiteLog.getEndTime();
		long elapsedTime = suiteEndTime - suiteStartTime;
		String lastErrMsg = "";
		int totalFail = 0;
		int totalErr = 0;
		for (ILogRecord testLogEntity : suiteLog.getChildRecords()) {
			if (testLogEntity.getStatus() != null
					&& testLogEntity.getStatus().getStatusValue() == TestStatusValue.FAILED) {
				suiteStat = TestStatusValue.FAILED;
				lastErrMsg = testLogEntity.getMessage();
				totalFail++;
			} else if (testLogEntity.getStatus() != null
					&& testLogEntity.getStatus().getStatusValue() == TestStatusValue.ERROR) {
				suiteStat = TestStatusValue.ERROR;
				lastErrMsg = testLogEntity.getMessage();
				totalErr++;
			}
		}
		String statValue = suiteStat.ordinal() + "";
		status.props.add(new JsModelProperty("status", statValue, null));
		status.props.add(new JsModelProperty("suiteStartTime", suiteStartTime + "", null));
		status.props.add(new JsModelProperty("elapsedTime", elapsedTime + "", null));
		if (suiteStat == TestStatusValue.FAILED || suiteStat == TestStatusValue.ERROR) {
			// Failed reason
			status.props.add(lastErrMsg.equals("") ? new JsModelProperty("errMessage", EMPTY_STRING_INDEX, null)
					: new JsModelProperty("errMessage", lastErrMsg, listStrings));
		}
		suiteLog.getStatus().setStatusValue(suiteStat);

		return new int[] { totalFail, totalErr };
	}

	private void initSummary(int[] totalFailsErrors) {
		// Summary result
		int totalChildCount = suiteLog.getChildRecords().length;
		sum = new JsModel();
		sum.props.add(new JsModelProperty("total", String.valueOf(totalChildCount), null));
		sum.props.add(new JsModelProperty("passes", String.valueOf(totalChildCount
				- (totalFailsErrors[0] + totalFailsErrors[1])), null));
		sum.props.add(new JsModelProperty("fails", String.valueOf(totalFailsErrors[0]), null));
		sum.props.add(new JsModelProperty("errors", String.valueOf(totalFailsErrors[1]), null));
	}

	public StringBuilder toArrayString() {

		init();

		StringBuilder sb = new StringBuilder();

		// Start suite
		sb.append(ARRAY_OPEN);

		// Properties
		for (JsModelProperty prop : props) {
			sb.append(prop.getPropertyValue());
			sb.append(ARRAY_DLMT);
		}

		// meta-data
		sb.append(metaData.toArrayString());
		sb.append(ARRAY_DLMT);

		// Status
		sb.append(status.toArrayString());
		sb.append(ARRAY_DLMT);

		// Sub suites
		sb.append(subSuite.toArrayString());
		sb.append(ARRAY_DLMT);

		// Tests
		sb.append(ARRAY_OPEN);
		for (int i = 0; i < tests.size(); i++) {
			sb.append(tests.get(i).toArrayString());
			if (i < tests.size() - 1) {
				sb.append(ARRAY_DLMT);
			}
		}
		sb.append(ARRAY_CLOSE);
		sb.append(ARRAY_DLMT);

		// Suite Keywords, could be SetUp/TearDown, have not support it
		sb.append(suiteKeyword.toArrayString());
		sb.append(ARRAY_DLMT);

		// Summary result
		sb.append(sum.toArrayString());

		// End suite
		sb.append(ARRAY_CLOSE);

		return sb;
	}
}
