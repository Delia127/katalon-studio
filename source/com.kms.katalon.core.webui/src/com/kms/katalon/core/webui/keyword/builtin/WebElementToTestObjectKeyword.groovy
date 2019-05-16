package com.kms.katalon.core.webui.keyword.builtin

import groovy.transform.CompileStatic

import java.text.MessageFormat
import java.util.AbstractMap
import java.util.ArrayList
import java.util.List
import java.util.AbstractMap.SimpleEntry
import java.util.Map.Entry
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.lang.math.NumberUtils
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.exception.StepErrorException
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.helper.KeywordHelper
import com.kms.katalon.core.keyword.internal.AbstractKeyword
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.logging.model.TestStatus
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.main.TestResult
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectBuilder
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.configuration.RunConfiguration

@Action(value = "webElementToTestObject")
public class WebElementToTestObjectKeyword extends AbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.BUITIN
    }

    @CompileStatic
    @Override
    public Object execute(Object ...params) {
        WebElement webElement = (WebElement) params[0]
        FailureHandling flowControl = (FailureHandling)(params.length > 1 && params[1] instanceof FailureHandling ? params[1] : RunConfiguration.getDefaultFailureHandling())
        return WebElementToTestObject(webElement, flowControl)
    }

    @CompileStatic
    public Object WebElementToTestObject(WebElement webElement, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            logger.logDebug(StringConstants.KW_LOG_INFO_CONVERT_WEB_ELEMENT_TO_TEST_OBJECT);

            String outerHtmlContent = webElement.getAttribute("outerHTML");
            String regex = "([a-z]+-?[a-z]+_?)='?\"?([a-z]+-?[a-z]+_?)'?\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(outerHtmlContent);
            List<TestObjectProperty> properties = new ArrayList<>();
            while (matcher.find()) {
                properties.add(new TestObjectProperty(matcher.group(1), ConditionType.EQUALS, matcher.group(2), true));
            }

            if (outerHtmlContent == null || outerHtmlContent.equals("")) {
                return null;
            }
            TestObject resultTestObject = new TestObjectBuilder(webElement.getTagName())
                    .withProperties(properties)
                    .withSelectorMethod(SelectorMethod.BASIC)
                    .build();

            String cssLocatorValue = findActiveEqualsObjectProperty(resultTestObject, "css");
            if (cssLocatorValue != null) {
                resultTestObject.setSelectorValue(SelectorMethod.BASIC, cssLocatorValue);
            }
            XPathBuilder xpathBuilder = new XPathBuilder(resultTestObject.getActiveProperties());
            resultTestObject.setSelectorValue(SelectorMethod.BASIC, xpathBuilder.build());
            return resultTestObject;
        }, flowControl, StringConstants.KW_LOG_INFO_FAIL_TO_CONVERT_WEB_ELEMENT_TO_TEST_OBJECT)
    }

    @CompileStatic
    public static String findActiveEqualsObjectProperty(TestObject to, String propertyName) {
        for (TestObjectProperty property : to.getActiveProperties()) {
            if (property.getName().equals(propertyName) && property.getCondition() == ConditionType.EQUALS) {
                return property.getValue();
            }
        }
        return null;
    }

    private static class XPathBuilder {

        public static enum AggregationType {
            UNION,
            INTERSECT
        }

        private static final String XPATH_INTERSECT_FORMULA = "%s[count(. | %s) = count(%s)]";

        private static final String XPATH_CONDITION_TYPE_NOT_MATCHES = "not(matches(%s, '%s'))";

        private static final String XPATH_CONDITION_TYPE_MATCHES = "matches(%s, '%s')";

        private static final String XPATH_CONDITION_TYPE_ENDS_WITH = "ends-with(%s, %s)";

        private static final String XPATH_CONDITION_TYPE_STARTS_WITH = "starts-with(%s, %s)";

        private static final String XPATH_CONDITION_TYPE_NOT_EQUALS = "%s != %s";

        private static final String XPATH_CONDITION_TYPE_NOT_CONTAINS = "not(contains(%s, %s))";

        private static final String XPATH_CONDITION_TYPE_EQUALS = "%s = %s";

        private static final String XPATH_CONDITION_TYPE_CONTAINS = "contains(%s, %s)";

        private String tag;

        private String xpath;

        private List<String> predicates;

        private List<TestObjectProperty> properties;

        public XPathBuilder(List<TestObjectProperty> properties) {
            this.properties = properties;
        }

        /**
         * convenient function to avoid changing signature
         * @return
         */
        public String build() {
            return build(AggregationType.INTERSECT);
        }

        /**
         * Union: "or" of all XPath locators, each contains a single condition
         * @param aggregationType
         * @return
         */
        public String build(AggregationType aggregationType) {

            boolean isIntersect = aggregationType.equals(AggregationType.INTERSECT);
            boolean isUnion = !isIntersect;

            boolean hasTagCondition = false;
            tag = "*";
            predicates = new ArrayList<>();
            xpath = StringUtils.EMPTY;

            if (properties != null && !properties.isEmpty()) {
                for (TestObjectProperty p : properties) {
                    if (isUnion || p.isActive()) {
                        // if union, use all properties
                        String propertyName = p.getName();
                        String propertyValue = p.getValue();
                        ConditionType conditionType = p.getCondition();
                        switch (PropertyType.nameOf(propertyName)) {
                            case PropertyType.ATTRIBUTE:
                                predicates.add(buildExpression("@" + propertyName, propertyValue, conditionType));
                                break;
                            case PropertyType.TAG:
                                hasTagCondition = true;
                                tag = propertyValue;
                                break;
                            case PropertyType.TEXT:
                                String textExpression = buildExpression("text()", propertyValue, conditionType);
                                String dotExpression = buildExpression(".", propertyValue, conditionType);
                                predicates.add(String.format("(%s or %s)", textExpression, dotExpression));
                                break;
                            case PropertyType.XPATH:
                                xpath = propertyValue;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            List<String> xpaths = new ArrayList<>();

            if (StringUtils.isNotEmpty(xpath)) {
                xpaths.add(xpath);
            }

            if (!predicates.isEmpty()) {
                StringBuilder propertyBuilder = new StringBuilder();
                final String operator = isIntersect ? " and " : " or "; // intersect = and, union = or
                final String xpathTag = isIntersect ? tag : "*";
                propertyBuilder.append("//")
                        .append(xpathTag)
                        .append("[")
                        .append(StringUtils.join(predicates, operator))
                        .append("]");
                xpaths.add(propertyBuilder.toString());
            }

            if (isUnion && hasTagCondition) { // union
                xpaths.add("//" + tag);
            }

            return combineXpathLocators(xpaths, aggregationType);
        }

        public List<Entry<String, String>> buildXpathBasedLocators() {

            List<Entry<String, String>> locators = new ArrayList<>();

            if (properties != null && !properties.isEmpty()) {
                for (TestObjectProperty p : properties) {
                    String propertyName = p.getName();
                    String propertyValue = p.getValue();
                    ConditionType conditionType = p.getCondition();
                    Entry<String, String> entry;
                    switch (PropertyType.nameOf(propertyName)) {
                        case PropertyType.TEXT:
                            String textExpression = buildExpression("text()", propertyValue, conditionType);
                            String dotExpression = buildExpression(".", propertyValue, conditionType);
                            String predicate = String.format("(%s or %s)", textExpression, dotExpression);
                            String locator = "//*[" + predicate + "]";
                            entry = new SimpleEntry<>(propertyName, locator);
                            break;
                        case PropertyType.XPATH:
                            entry = new SimpleEntry<>(propertyName, propertyValue);
                            break;
                        default:
                            entry = null;
                            break;
                    }
                    if (entry != null) {
                        locators.add(entry);
                    }
                }
            }

            return locators;
        }

        private String combineXpathLocators(List<String> xpathList, AggregationType aggregationType) {
            String xpathString;
            if (aggregationType.equals(AggregationType.INTERSECT)) {
                StringBuilder xpathStringBuilder = new StringBuilder();
                for (String xpath : xpathList) {
                    if (xpathStringBuilder.toString().isEmpty()) {
                        xpathStringBuilder.append(xpath);
                    } else {
                        String existingXpath = xpathStringBuilder.toString();
                        xpathStringBuilder = new StringBuilder(String.format(XPATH_INTERSECT_FORMULA, existingXpath, xpath, xpath));
                    }
                }
                xpathString = xpathStringBuilder.toString();
            } else {
                xpathString = StringUtils.join(xpathList, " | ");
            }
            return xpathString;
        }

        private String buildExpression(String propertyName, String propertyValue, ConditionType contidionType) {
            switch (contidionType) {
                case contidionType.CONTAINS:
                    return String.format(XPATH_CONDITION_TYPE_CONTAINS, propertyName, escapeSingleQuote(propertyValue));
                case contidionType.ENDS_WITH:
                    return String.format(XPATH_CONDITION_TYPE_ENDS_WITH, propertyName, escapeSingleQuote(propertyValue));
                case contidionType.EQUALS:
                    return String.format(XPATH_CONDITION_TYPE_EQUALS, propertyName, escapeSingleQuote(propertyValue));
                case contidionType.MATCHES_REGEX:
                    return String.format(XPATH_CONDITION_TYPE_MATCHES, propertyName, propertyValue);
                case contidionType.NOT_CONTAIN:
                    return String.format(XPATH_CONDITION_TYPE_NOT_CONTAINS, propertyName, escapeSingleQuote(propertyValue));
                case contidionType.NOT_EQUAL:
                    return String.format(XPATH_CONDITION_TYPE_NOT_EQUALS, propertyName, escapeSingleQuote(propertyValue));
                case contidionType.NOT_MATCH_REGEX:
                    return String.format(XPATH_CONDITION_TYPE_NOT_MATCHES, propertyName, propertyValue);
                case contidionType.STARTS_WITH:
                    return String.format(XPATH_CONDITION_TYPE_STARTS_WITH, propertyName, escapeSingleQuote(propertyValue));
                default:
                    return StringUtils.EMPTY;
            }
        }

        private String escapeSingleQuote(String s) {
            if (!s.contains("'")) {
                return qoute(s);
            }

            String[] strings = s.split("'");

            StringBuilder xpathBuilder = new StringBuilder("concat(");
            for (int i = 0; i < strings.length; i++) {
                if (i > 0) {
                    xpathBuilder.append(" , \"'\" , ");
                }

                xpathBuilder.append(qoute(strings[i]));
            }

            xpathBuilder.append(")");
            return xpathBuilder.toString();
        }

        private String qoute(String s) {
            return "'" + s + "'";
        }

        public enum PropertyType {
            TAG, ATTRIBUTE, TEXT, XPATH;

            public static PropertyType nameOf(String name) {
                try {
                    return valueOf(name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return PropertyType.ATTRIBUTE;
                }
            }
        }
    }
}
