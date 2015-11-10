package com.kms.katalon.core.reporting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.BaseEncoding;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestStepLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;

public class JsStepModel extends JsModel {

	private TestStepLogRecord stepLogEntity;
	private List<String> listStrings;
	private String parentPrefix;
	private List<JsCallStepModel> calledTests;
	private List<JsStepModel> calledSteps;
	private JsModel status;
	private List<JsModel> logRecords;

	public JsStepModel(TestStepLogRecord stepLog, List<String> listStrings, String parentPrefix) {
		this.stepLogEntity = stepLog;
		this.listStrings = listStrings;
		this.parentPrefix = parentPrefix;
	}

	private void init() {

		this.calledTests = new ArrayList<JsCallStepModel>();
		this.calledSteps = new ArrayList<JsStepModel>();
		this.status = new JsModel();
		this.logRecords = new ArrayList<JsModel>();

		// Step name
		String stepName = stepLogEntity.getName();
		if (parentPrefix != null && !parentPrefix.isEmpty()) {
			stepName = parentPrefix + "." + stepName;
		}

		props.add(new JsModelProperty("Type", EMPTY_STRING_INDEX, null));
		props.add(new JsModelProperty("name", stepName, listStrings));
		props.add(new JsModelProperty("timeout", EMPTY_STRING_INDEX, null));
		props.add(new JsModelProperty("doc", stepLogEntity.getDescription(), listStrings));
		props.add(new JsModelProperty("args", EMPTY_STRING_INDEX, null));

		// The Status
		initStatus();

		// Called tests
		if (stepLogEntity.getChildRecords().length > 0) {
			for (ILogRecord logRecord : stepLogEntity.getChildRecords()) {
				if (logRecord instanceof TestCaseLogRecord) {
					calledTests.add(new JsCallStepModel(stepLogEntity, (TestCaseLogRecord) logRecord, listStrings));
				} else if (logRecord instanceof TestStepLogRecord) {
					calledSteps.add(new JsStepModel((TestStepLogRecord) logRecord, listStrings, ""));
				}
			}
		}
		// Log Records (TRACE , DEBUG , INFO , WARN, FAIL, ERROR)
		initLogRecords();
	}

	@Override
	public StringBuilder toArrayString() {

		init();

		StringBuilder sb = new StringBuilder();
		sb.append(ARRAY_OPEN);
		// Properties
		for (JsModelProperty prop : props) {
			sb.append(prop.getPropertyValue());
			sb.append(ARRAY_DLMT);
		}
		// Status
		sb.append(status.toArrayString());
		sb.append(ARRAY_DLMT);
		// Called test
		sb.append(ARRAY_OPEN);

		// //Called keyword/step
		boolean isLooping = calledTests.size() > 1;
		for (int i = 0; i < calledTests.size(); i++) {
			if (!isLooping) {
				for (int j = 0; j < calledTests.get(i).getTestModel().getSteps().size(); j++) {
					JsStepModel innerStep = calledTests.get(i).getTestModel().getSteps().get(j);
					sb.append(innerStep.toArrayString());
					if (j < calledTests.get(i).getTestModel().getSteps().size() - 1) {
						sb.append(ARRAY_DLMT);
					}
				}
			} else {
				sb.append(calledTests.get(i).toArrayString());
				if (i < calledTests.size() - 1) {
					sb.append(ARRAY_DLMT);
				}
			}
		}

		for (int i = 0; i < calledSteps.size(); i++) {
			JsStepModel innerStep = calledSteps.get(i);
			sb.append(innerStep.toArrayString());
			if (i < calledSteps.size() - 1) {
				sb.append(ARRAY_DLMT);
			}
		}

		sb.append(ARRAY_CLOSE);
		sb.append(ARRAY_DLMT);

		// Messages/log records
		sb.append(ARRAY_OPEN);
		for (int i = 0; i < logRecords.size(); i++) {
			sb.append(logRecords.get(i).toArrayString());
			if (i < logRecords.size() - 1) {
				sb.append(ARRAY_DLMT);
			}
		}
		sb.append(ARRAY_CLOSE);

		sb.append(ARRAY_CLOSE);
		return sb;
	}

	private void initStatus() {
		String statVal = "";
		if (stepLogEntity.getStatus() == null) {
			statVal = TestStatusValue.indexOf(TestStatusValue.NOT_RUN) + "";
		} else {
			statVal = TestStatusValue.indexOf(stepLogEntity.getStatus().getStatusValue()) + "";
		}
		long startTime = stepLogEntity.getStartTime();
		long elapsedTime = stepLogEntity.getEndTime() - startTime;
		status.props.add(new JsModelProperty("status", statVal, null));
		status.props.add(new JsModelProperty("startTime", String.valueOf(startTime), null));
		status.props.add(new JsModelProperty("elapsedTime", String.valueOf(elapsedTime), null));
	}

	private void initLogRecords() {
		for (ILogRecord logRecord : stepLogEntity.getChildRecords()) {
			if (logRecord instanceof MessageLogRecord) {
				MessageLogRecord messageLog = (MessageLogRecord) logRecord;
				long logStartTime = messageLog.getStartTime();
				String logStatVal = TestStatusValue.indexOf(messageLog.getStatus().getStatusValue()) + "";
				String logStatMsg = messageLog.getMessage();
				JsModel jsLogRecModel = new JsModel();
				jsLogRecModel.props.add(new JsModelProperty("startTime", logStartTime + "", null));
				jsLogRecModel.props.add(new JsModelProperty("status", logStatVal, null));
				jsLogRecModel.props.add(new JsModelProperty("message", logStatMsg, listStrings));
				//if (stepLogEntity.getAttachment() != null && !stepLogEntity.getAttachment().isEmpty()) {
				if (messageLog.getAttachment() != null && !messageLog.getAttachment().isEmpty()) {
					//Find log folder
					String logFolder = null;
					ILogRecord testSuiteLogRecord = messageLog;
					while(testSuiteLogRecord != null){
						if(testSuiteLogRecord instanceof TestSuiteLogRecord){
							logFolder = ((TestSuiteLogRecord)testSuiteLogRecord).getLogFolder();
							break;
						}
						testSuiteLogRecord = testSuiteLogRecord.getParentLogRecord();
					}
					if(logFolder != null){
						try{
							String md5 = encodeFileContent(logFolder + File.separator + messageLog.getAttachment());
							jsLogRecModel.props.add(new JsModelProperty("link", "data:image/png;base64," + md5, listStrings));
						}
						catch(Exception ex){}
					}
				}
				logRecords.add(jsLogRecModel);
			}
		}
	}

	private String encodeFileContent(String filePath) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            return BaseEncoding.base64().encode(getBinaryFromInputStream(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
	
	private byte[] getBinaryFromInputStream(InputStream content) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[8096];
        int length;
        while ((length = content.read(buffer)) > -1) {
            output.write(buffer, 0, length);
        }
        output.close();
        return output.toByteArray();
    }
	
}
