package com.kms.katalon.core.testobject;

import static com.kms.katalon.core.constants.StringConstants.ID_SEPARATOR;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.util.ExceptionsUtil;

public class ObjectRepository {
    private static KeywordLogger logger = KeywordLogger.getInstance();

    private static final String TEST_OBJECT_ROOT_FOLDER_NAME = "Object Repository";

    private static final String TEST_OBJECT_ID_PREFIX = TEST_OBJECT_ROOT_FOLDER_NAME + ID_SEPARATOR;

    private static final String WEB_SERVICES_TYPE_NAME = "WebServiceRequestEntity";

    private static final String WEB_ELEMENT_TYPE_NAME = "WebElementEntity";

    private static final String WEBELEMENT_FILE_EXTENSION = ".rs";

    private static final String WEB_ELEMENT_PROPERTY_NODE_NAME = "webElementProperties";

    private static final String PROPERTY_NAME = "name";

    private static final String PROPERTY_CONDITION = "matchCondition";

    private static final String PROPERTY_VALUE = "value";

    private static final String PROPERTY_IS_SELECTED = "isSelected";

    private static final String[] PARENT_FRAME_ATTRS = new String[] { "ref_element", "parent_frame" };

    /**
     * Returns test object id of a its relative id.
     * 
     * @param testObjectRelativeId
     * Relative test object's id.
     * @returnString of test object id, <code>null</code> if <code>testObjectRelativeId</code> is null.
     */
    public static String getTestObjectId(final String testObjectRelativeId) {
        if (testObjectRelativeId == null) {
            return null;
        }

        if (testObjectRelativeId.startsWith(TEST_OBJECT_ID_PREFIX)) {
            return testObjectRelativeId;
        }
        return TEST_OBJECT_ID_PREFIX + testObjectRelativeId;
    }

    /**
     * Returns relative id of a test object's id. The relative id is cut <code>"Object Repository/"</code> prefix from
     * the
     * test object's id.
     * 
     * @param testObjectId
     * Full test object's id.
     * @return String of test object relative id, <code>null</code> if <code>testObjectId</code> is null.
     */
    public static String getTestObjectRelativeId(final String testObjectId) {
        if (testObjectId == null) {
            return null;
        }
        return testObjectId.replaceFirst(TEST_OBJECT_ID_PREFIX, StringUtils.EMPTY);
    }

    /**
     * Finds {@link TestObject} by its id or relative id
     * 
     * @param testObjectRelativeId
     * Can be test object full id or test object relative id
     * <p>
     * Eg: Using "Object Repository/Sample Test Object" (full id) OR "Sample Test Object" (relative id) as
     * <code>testObjectRelativeId</code> is accepted for the test object with id "Object Repository/Sample Test Object"
     * 
     * @return an instance of {@link TestObject} or <code>null</code> if the parameter is null or test object doesn't
     * exist
     */
    public static TestObject findTestObject(String testObjectRelativeId) {
        if (testObjectRelativeId == null) {
            logger.logWarning(StringConstants.TO_LOG_WARNING_TEST_OBJ_NULL);
            return null;
        }
        String testObjectId = getTestObjectId(testObjectRelativeId);
        logger.logInfo(MessageFormat.format(StringConstants.TO_LOG_INFO_FINDING_TEST_OBJ_W_ID, testObjectId));
        File objectFile = new File(RunConfiguration.getProjectDir(), testObjectId + WEBELEMENT_FILE_EXTENSION);
        if (!objectFile.exists()) {
            logger.logWarning(MessageFormat.format(StringConstants.TO_LOG_WARNING_TEST_OBJ_DOES_NOT_EXIST, testObjectId));
            return null;
        }
        return readTestObjectFile(testObjectId, objectFile);
    }

    private static TestObject readTestObjectFile(String testObjectId, File objectFile) {
        try {
            Element rootElement = new SAXReader().read(objectFile).getRootElement();
            String elementName = rootElement.getName();
            if (WEB_ELEMENT_TYPE_NAME.equals(elementName)) {
                return findWebUIObject(testObjectId, rootElement);
            }

            if (WEB_SERVICES_TYPE_NAME.equals(elementName)) {
                return findRequestObject(testObjectId, rootElement);
            }
            return null;
        } catch (DocumentException e) {
            logger.logWarning(MessageFormat.format(
                    StringConstants.TO_LOG_WARNING_CANNOT_GET_TEST_OBJECT_X_BECAUSE_OF_Y, testObjectId,
                    ExceptionsUtil.getMessageForThrowable(e)));
            return null;
        }
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
            ConditionType propertyCondition = ConditionType.fromValue(StringEscapeUtils.unescapeXml(propertyElement.elementText(PROPERTY_CONDITION)));
            String propertyValue = StringEscapeUtils.unescapeXml(propertyElement.elementText(PROPERTY_VALUE));
            boolean isPropertySelected = Boolean.valueOf(StringEscapeUtils.unescapeXml(propertyElement.elementText(PROPERTY_IS_SELECTED)));

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
    private static RequestObject findRequestObject(String requestObjectId, Element reqElement) {
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
