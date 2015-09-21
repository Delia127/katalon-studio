package com.kms.katalon.core.testdata;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.exception.ExceptionsUtil;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.testdata.reader.CSVSeperator;
import com.kms.katalon.core.util.PathUtils;

public class TestDataFactory {
	private static final String TEST_DATA_FILE_EXTENSION = ".dat";
	private static final String DRIVER_NODE = "driver";
	private static final String URL_NODE = "dataSourceUrl";
	private static final String SHEET_NAME_NODE = "sheetName";
	private static final String DATA_NODE = "data";
	private static final String INTERNAL_DATA_NODE = "internalDataColumns";
	// private static final String INTERNAL_DATA_COLUMN_NAME_ATTRIBUTE =
	// "columnName";
	private static final String INTERNAL_DATA_COLUMN_NAME_ATTRIBUTE = "name";
	private static final String CONTAINS_HEADERS_NODE = "containsHeaders";
	private static final String CSV_SEPERATOR_NODE = "csvSeperator";
	private static final String IS_RELATIVE_PATH_NODE = "isInternalPath";
	private static final String ENCODER_CHARSET = "utf-8";
	private static final KeywordLogger logger = KeywordLogger.getInstance();

	public static TestData findTestData(String testDataId) throws IllegalArgumentException {
		logger.logInfo(StringConstants.XML_LOG_TEST_DATA_CHECKING_TEST_DATA_ID);
		if (testDataId == null) {
			throw new IllegalArgumentException(StringConstants.XML_LOG_ERROR_TEST_DATA_NULL_TEST_DATA_ID);
		}
		try {
			File dataFile = new File(getProjectDir() + File.separator + testDataId + TEST_DATA_FILE_EXTENSION);
			return internallyfindTestData(dataFile, getProjectDir(), testDataId);
		} catch (Exception e) {
			throw new StepFailedException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_CANNOT_FIND_TEST_DATA_X_BECAUSE_OF_Y, testDataId,
					ExceptionsUtil.getMessageForThrowable(e)));
		}
	}

	private static TestData internallyfindTestData(File dataFile, String projectDir, String testDataId)
			throws Exception {
		logger.logInfo(MessageFormat.format(StringConstants.XML_LOG_TEST_DATA_FINDING_TEST_DATA_WITH_ID_X, testDataId));
		if (dataFile != null && dataFile.exists()) {
			SAXReader reader = new SAXReader();
			Document document = reader.read(dataFile);
			Element testDataElement = document.getRootElement();

			String driverName = testDataElement.elementText(DRIVER_NODE);
			switch (TestDataType.fromValue(driverName)) {
			case EXCEL_FILE:
				logger.logInfo(StringConstants.XML_LOG_TEST_DATA_READING_EXCEL_DATA);
				return readExcelData(testDataElement, projectDir);
			case INTERNAL_DATA:
				logger.logInfo(StringConstants.XML_LOG_TEST_DATA_READING_INTERNAL_DATA);
				return readInternalData(testDataElement, projectDir, dataFile);
			case CSV_FILE:
				logger.logInfo(StringConstants.XML_LOG_TEST_DATA_READING_CSV_DATA);
				return readCSVData(testDataElement, projectDir);
			default:
				break;
			}
			return null;
		} else {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_X_NOT_EXISTS, testDataId));
		}
	}

	private static String getProjectDir() {
		return new File(RunConfiguration.getProjectDir()).getAbsolutePath();
	}

	public static TestData findTestDataForExternalBundleCaller(String testDataId, String projectDir) throws Exception {
		File currentDirFile = new File(projectDir);
		String currentDirFilePath = currentDirFile.getAbsolutePath();
		File dataFile = new File(currentDirFilePath + File.separator + testDataId + TEST_DATA_FILE_EXTENSION);
		return internallyfindTestData(dataFile, projectDir, testDataId);
	}

	public static TestData readExcelData(Element testDataElement, String projectDir) throws Exception {
		if (testDataElement.element(URL_NODE) == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT, URL_NODE));
		}
		if (testDataElement.element(SHEET_NAME_NODE) == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT, SHEET_NAME_NODE));
		}
		String sourceUrl = testDataElement.elementText(URL_NODE);
		String sheetName = testDataElement.elementText(SHEET_NAME_NODE);
		Boolean isRelativePath = false;
		if (testDataElement.element(IS_RELATIVE_PATH_NODE) != null) {
			isRelativePath = Boolean.valueOf(testDataElement.elementText(IS_RELATIVE_PATH_NODE));
		}
		if (isRelativePath) {
			sourceUrl = PathUtils.relativeToAbsolutePath(sourceUrl, projectDir);
		}
		logger.logInfo(MessageFormat.format(StringConstants.XML_LOG_TEST_DATA_READING_EXCEL_DATA_WITH_SOURCE_X_SHEET_Y,
				sourceUrl, sheetName));
		return new ExcelData(sheetName, sourceUrl);
	}

	public static TestData readInternalData(Element testDataElement, String projectDir, File dataFile)
			throws UnsupportedEncodingException {
		List<String> columnNames = new ArrayList<String>();
		List<String[]> data = new ArrayList<String[]>();
		for (Object internalDataColumnElementObject : testDataElement.elements(INTERNAL_DATA_NODE)) {
			Element internalDataColumnElement = (Element) internalDataColumnElementObject;
			// columnNames.add(internalDataColumnElement.attributeValue(INTERNAL_DATA_COLUMN_NAME_ATTRIBUTE));
			columnNames.add(internalDataColumnElement.element(INTERNAL_DATA_COLUMN_NAME_ATTRIBUTE).getText());
		}

		for (Object dataElementObject : testDataElement.elements(DATA_NODE)) {
			Element dataElement = (Element) dataElementObject;
			String[] rowRawData = dataElement.getText().split(" ");
			String[] rowData = new String[rowRawData.length];
			for (int columnIndex = 0; columnIndex < rowRawData.length; columnIndex++) {
				String decodeValue = URLDecoder.decode(rowRawData[columnIndex].toString(), ENCODER_CHARSET);
				if (!decodeValue.equals("null")) {
					rowData[columnIndex] = decodeValue;
				} else {
					rowData[columnIndex] = "";
				}
			}
			data.add(rowData);
		}
		return new InternalData(dataFile.getAbsolutePath(), data, columnNames);
	}

	public static TestData readCSVData(Element testDataElement, String projectDir) throws Exception {
		if (testDataElement.element(URL_NODE) == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT, URL_NODE));
		}
		if (testDataElement.element(CONTAINS_HEADERS_NODE) == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT, CONTAINS_HEADERS_NODE));
		}
		if (testDataElement.element(CSV_SEPERATOR_NODE) == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT, CSV_SEPERATOR_NODE));
		}
		String sourceUrl = testDataElement.element(URL_NODE).getText();
		boolean containsHeader = Boolean.valueOf(testDataElement.element(CONTAINS_HEADERS_NODE).getText());
		CSVSeperator seperator = CSVSeperator.fromValue(testDataElement.element(CSV_SEPERATOR_NODE).getText());
		Boolean isRelativePath = false;
		if (testDataElement.element(IS_RELATIVE_PATH_NODE) != null) {
			isRelativePath = Boolean.valueOf(testDataElement.elementText(IS_RELATIVE_PATH_NODE));
		}
		
		if (isRelativePath) {
			sourceUrl = PathUtils.relativeToAbsolutePath(sourceUrl, projectDir);
		}
		
		logger.logInfo(MessageFormat.format(
				StringConstants.XML_LOG_TEST_DATA_READING_CSV_DATA_WITH_SOURCE_X_SEPERATOR_Y_AND_Z,
				seperator.toString(), containsHeader ? "containing header" : "not containing header"));
		return new CSVData(sourceUrl, containsHeader, seperator);
	}
}
