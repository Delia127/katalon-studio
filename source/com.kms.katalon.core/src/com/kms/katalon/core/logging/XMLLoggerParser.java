package com.kms.katalon.core.logging;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLLoggerParser {

	private static final String LOG_RECORD_NODE_NAME = "record";
	private static final String LEVEL_NODE_NAME = "level";
	private static final String MESSAGE_NODE_NAME = "message";
	private static final String MILLIS_NODE_NAME = "millis";
	private static final String METHOD_NODE_NAME = "method";
	// private static final String NESTED_LEVEL_NODE_NAME = "nestedLevel";
	// private static final String START_TIME_NODE_NAME = "startTime";
	private static final String EXCEPTION_NODE_NAME = "exception";
	private static final String EXCEPTION_FRAME_NODE_NAME = "frame";
	private static final String EXCEPTION_CLASS_NODE_NAME = "class";
	private static final String EXCEPTION_METHOD_NODE_NAME = "method";
	private static final String EXCEPTION_LINE_NODE_NAME = "line";
	private static final String LOG_RECORD_PROP_NODE_NAME = "property";

	public static List<XmlLogRecord> parseLogString(String recordsXML) throws Exception {
		String cleanXml = recordsXML.replaceAll("(&(?!amp;))", "&amp;");
		List<XmlLogRecord> logRecords = new ArrayList<XmlLogRecord>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(cleanXml));
		Element rootElement = document.getRootElement();

		for (Object logElementObject : rootElement.elements(LOG_RECORD_NODE_NAME)) {
			Element logElement = (Element) logElementObject;
			XmlLogRecord record = new XmlLogRecord(LogLevel.ALL, "");

			Element logLevelElement = logElement.element(LEVEL_NODE_NAME);
			record.setLevel(LogLevel.parse(logLevelElement.getText()));

			Element logMessageElement = logElement.element(MESSAGE_NODE_NAME);
			record.setMessage(unescapeString(logMessageElement.getText()));

			Element logMillisElement = logElement.element(MILLIS_NODE_NAME);
			record.setMillis(Long.valueOf(logMillisElement.getText()));

			Element logMethodElement = logElement.element(METHOD_NODE_NAME);
			record.setSourceMethodName(logMethodElement.getText());

			Element logExceptionElement = logElement.element(EXCEPTION_NODE_NAME);
			if (logExceptionElement != null) {
				List<XmlLogRecordException> recordExceptionLst = new ArrayList<XmlLogRecordException>();
				for (Object exceptionFrameElementObject : logExceptionElement.elements(EXCEPTION_FRAME_NODE_NAME)) {
					XmlLogRecordException logException = new XmlLogRecordException();

					Element exceptionFrameElement = (Element) exceptionFrameElementObject;

					Element exceptionClassElement = exceptionFrameElement.element(EXCEPTION_CLASS_NODE_NAME);
					logException.setClassName(exceptionClassElement.getText());

					Element exceptionMethodElement = exceptionFrameElement.element(EXCEPTION_METHOD_NODE_NAME);
					logException.setMethodName(exceptionMethodElement.getText());

					Element exceptionLineNumberElement = exceptionFrameElement.element(EXCEPTION_LINE_NODE_NAME);
					if (exceptionLineNumberElement != null) {
						logException.setLineNumber(Integer.parseInt(exceptionLineNumberElement.getText()));
						recordExceptionLst.add(logException);
					}
				}
				record.setExceptions(recordExceptionLst);
			}

			for (Object propElementObject : logElement.elements(LOG_RECORD_PROP_NODE_NAME)) {
				String propName = ((Element) propElementObject).attribute("name").getValue();
				String propVal = ((Element) propElementObject).getStringValue();
				record.getProperties().put(propName, unescapeString(propVal));
			}

			logRecords.add(record);
		}

		return logRecords;
	}

	public static String unescapeString(String text) {
		return StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeXml(text));
	}

	public static String getRecordDate(LogRecord record) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
		return format.format(new Date(record.getMillis()));
	}
}
