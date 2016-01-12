package com.kms.katalon.core.testobject;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.util.ExceptionsUtil;

public class ObjectRepository {
	private static final String WEB_SERVICES_TYPE_NAME = "WebServiceRequestEntity";
	private static final String WEB_ELEMENT_TYPE_NAME = "WebElementEntity";
	private static final String WEBELEMENT_FILE_EXTENSION = ".rs";
	private static final String WEB_ELEMENT_PROPERTY_NODE_NAME = "webElementProperties";
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_CONDITION = "matchCondition";
	private static final String PROPERTY_VALUE = "value";
	private static final String PROPERTY_IS_SELECTED = "isSelected";
	private static final String[] PARENT_FRAME_ATTRS = new String[] { "ref_element", "parent_frame" };

	public static TestObject findTestObject(String testObjectId) {
		try {
			if (testObjectId == null) {
				KeywordLogger.getInstance().logWarning(StringConstants.TO_LOG_WARNING_TEST_OBJ_NULL);
				return null;
			}
			KeywordLogger.getInstance().logInfo(
					MessageFormat.format(StringConstants.TO_LOG_INFO_FINDING_TEST_OBJ_W_ID, testObjectId));
			String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
			File objectFile = new File(currentDirFilePath + File.separator + testObjectId
					+ WEBELEMENT_FILE_EXTENSION);
			if (objectFile.exists()) {
				SAXReader reader = new SAXReader();
				Document document = reader.read(objectFile);
				Element element = document.getRootElement();
				if (element.getName().equals(WEB_ELEMENT_TYPE_NAME)) {
					return findWebUIObject(testObjectId, element);
				} else if (element.getName().equals(WEB_SERVICES_TYPE_NAME)) {
					return findRequestObject(testObjectId);
				}
			} else {
				KeywordLogger.getInstance().logWarning(
						MessageFormat.format(StringConstants.TO_LOG_WARNING_TEST_OBJ_DOES_NOT_EXIST, testObjectId));
				return null;
			}
		} catch (Exception e) {
			KeywordLogger.getInstance().logWarning(
					MessageFormat.format(StringConstants.TO_LOG_WARNING_CANNOT_GET_TEST_OBJECT_X_BECAUSE_OF_Y,
							testObjectId, ExceptionsUtil.getMessageForThrowable(e)));
		}
		return null;

	}

	private static TestObject findWebUIObject(String testObjectId, Element element) {
		TestObject testObject = new TestObject(testObjectId);
		// For image
		String imagePath = element.elementText("imagePath");
		boolean useRalativeImagePath = Boolean.parseBoolean(element.elementText("useRalativeImagePath"));
		testObject.setImagePath(imagePath);
		testObject.setUseRelativeImagePath(useRalativeImagePath);

		for (Object propertyElementObject : element.elements(WEB_ELEMENT_PROPERTY_NODE_NAME)) {
			TestObjectProperty objectProperty = new TestObjectProperty();
			Element propertyElement = (Element) propertyElementObject;

			String propertyName = StringEscapeUtils.unescapeXml(propertyElement.elementText(PROPERTY_NAME));
			ConditionType propertyCondition = ConditionType.fromValue(StringEscapeUtils.unescapeXml(propertyElement
					.elementText(PROPERTY_CONDITION)));
			String propertyValue = StringEscapeUtils.unescapeXml(propertyElement.elementText(PROPERTY_VALUE));
			boolean isPropertySelected = Boolean.valueOf(StringEscapeUtils.unescapeXml(propertyElement
					.elementText(PROPERTY_IS_SELECTED)));

			objectProperty.setName(propertyName);
			objectProperty.setCondition(propertyCondition);
			objectProperty.setValue(propertyValue);
			objectProperty.setActive(isPropertySelected);

			// Check if this element is inside a frame
			if (Arrays.asList(PARENT_FRAME_ATTRS).contains(propertyName) && isPropertySelected) {
				TestObject parentObject = findTestObject(propertyValue);
				testObject.setParentObject(parentObject);
			} else {
				testObject.addProperty(objectProperty);
			}
		}

		return testObject;
	}

	@SuppressWarnings("unchecked")
	private static RequestObject findRequestObject(String requestObjectId) throws Exception {
		String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
		File objectFile = new File(currentDirFilePath + File.separator + requestObjectId
				+ WEBELEMENT_FILE_EXTENSION);
		if (objectFile.exists()) {
			SAXReader reader = new SAXReader();
			Document document = reader.read(objectFile);
			Element reqElement = document.getRootElement();

			RequestObject requestObject = new RequestObject(requestObjectId);
			requestObject.setName(reqElement.elementText("name"));

			String serviceType = reqElement.elementText("serviceType");
			requestObject.setServiceType(serviceType);

			requestObject.setHttpHeaderProperties(parseProperties(reqElement.elements("httpHeaderProperties")));
			requestObject.setHttpBody(reqElement.elementText("httpBody"));

			if ("SOAP".equals(serviceType)) {
				requestObject.setWsdlAddress(reqElement.elementText("wsdlAddress"));
				requestObject.setSoapRequestMethod(reqElement.elementText("soapRequestMethod"));
				requestObject.setSoapServiceFunction(reqElement.elementText("soapServiceFunction"));
				requestObject.setSoapParameters(parseProperties(reqElement.elements("soapParameters")));
				requestObject.setSoapHeader(reqElement.elementText("soapHeader"));
				requestObject.setSoapBody(reqElement.elementText("soapBody"));
			} else if ("RESTful".equals(serviceType)) {
				requestObject.setRestUrl(reqElement.elementText("restUrl"));
				requestObject.setRestRequestMethod(reqElement.elementText("restRequestMethod"));
				requestObject.setRestParameters(parseProperties(reqElement.elements("restParameters")));
			}

			return requestObject;
		}
		return null;
	}

	private static List<TestObjectProperty> parseProperties(List<Object> objects) {
		List<TestObjectProperty> props = new ArrayList<>();
		for (Object propertyElementObject : objects) {
			TestObjectProperty objectProperty = new TestObjectProperty();
			Element propertyElement = (Element) propertyElementObject;

			String propertyName = propertyElement.elementText(PROPERTY_NAME);
			ConditionType propertyCondition = ConditionType.fromValue(propertyElement.elementText(PROPERTY_CONDITION));
			String propertyValue = propertyElement.elementText(PROPERTY_VALUE);
			boolean isPropertySelected = Boolean.valueOf(propertyElement.elementText(PROPERTY_IS_SELECTED));

			objectProperty.setName(propertyName);
			objectProperty.setCondition(propertyCondition);
			objectProperty.setValue(propertyValue);
			objectProperty.setActive(isPropertySelected);

			props.add(objectProperty);
		}
		return props;
	}
}
