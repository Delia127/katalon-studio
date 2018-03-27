package com.kms.katalon.core.webui.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.TestObjectProperty;

public class XPathBuilder {
    
    public static enum AggregationType {
        UNION,
        INTERSECT
    }

    private static final String XPATH_INTERSECTION_FORMULA = "%s[count(. | %s) = count(%s)]";

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
    
    public String build() {
        return build(AggregationType.INTERSECT);
    }

    public String build(AggregationType aggregationType) {
        
        boolean isIntersect = aggregationType.equals(AggregationType.INTERSECT);
        boolean isUnion = !isIntersect;
        
        boolean hasTag = false;
        tag = "*";
        predicates = new ArrayList<>();
        xpath = StringUtils.EMPTY;

        if (properties != null && !properties.isEmpty()) {
            for (TestObjectProperty p : properties) {
                if (isUnion || p.isActive()) {
                    String propertyName = p.getName();
                    String propertyValue = p.getValue();
                    ConditionType conditionType = p.getCondition();
                    switch (PropertyType.nameOf(propertyName)) {
                        case ATTRIBUTE:
                            predicates.add(buildExpression("@" + propertyName, propertyValue, conditionType));
                            break;
                        case TAG:
                            hasTag = true;
                            tag = propertyValue;
                            break;
                        case TEXT:
                            String textExpression = buildExpression("text()", propertyValue, conditionType);
                            String dotExpression = buildExpression(".", propertyValue, conditionType);
                            predicates.add(String.format("(%s or %s)", textExpression, dotExpression));
                            break;
                        case XPATH:
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
            final String operator = isIntersect ? " and " : " or ";
            final String xpathTag = isIntersect ? tag : "*";
            propertyBuilder.append("//")
                    .append(xpathTag)
                    .append("[")
                    .append(StringUtils.join(predicates, operator))
                    .append("]");
            xpaths.add(propertyBuilder.toString());
        }
        
        if (isUnion && hasTag) { // union
            xpaths.add("//" + tag);
        }

        return getXpathSelectorValue(xpaths, aggregationType);
    }

    private String getXpathSelectorValue(List<String> xpathList, AggregationType aggregationType) {
        String xpathString;
        if (aggregationType.equals(AggregationType.INTERSECT)) {
            StringBuilder xpathStringBuilder = new StringBuilder();
            for (String xpath : xpathList) {
                if (xpathStringBuilder.toString().isEmpty()) {
                    xpathStringBuilder.append(xpath);
                } else {
                    String existingXpath = xpathStringBuilder.toString();
                    xpathStringBuilder = new StringBuilder(String.format(XPATH_INTERSECTION_FORMULA, existingXpath, xpath, xpath));
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
            case CONTAINS:
                return String.format(XPATH_CONDITION_TYPE_CONTAINS, propertyName, escapeSingleQuote(propertyValue));
            case ENDS_WITH:
                return String.format(XPATH_CONDITION_TYPE_ENDS_WITH, propertyName, escapeSingleQuote(propertyValue));
            case EQUALS:
                return String.format(XPATH_CONDITION_TYPE_EQUALS, propertyName, escapeSingleQuote(propertyValue));
            case MATCHES_REGEX:
                return String.format(XPATH_CONDITION_TYPE_MATCHES, propertyName, propertyValue);
            case NOT_CONTAIN:
                return String.format(XPATH_CONDITION_TYPE_NOT_CONTAINS, propertyName, escapeSingleQuote(propertyValue));
            case NOT_EQUAL:
                return String.format(XPATH_CONDITION_TYPE_NOT_EQUALS, propertyName, escapeSingleQuote(propertyValue));
            case NOT_MATCH_REGEX:
                return String.format(XPATH_CONDITION_TYPE_NOT_MATCHES, propertyName, propertyValue);
            case STARTS_WITH:
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
