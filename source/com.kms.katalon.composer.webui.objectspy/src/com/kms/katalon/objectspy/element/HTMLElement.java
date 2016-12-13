package com.kms.katalon.objectspy.element;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.w3c.dom.Element;

public class HTMLElement {
    private static final String AND_OPERATOR = " and ";
    private static final String TEXT_METHOD = "text";
    private static final String CSS_SELECTOR = "css";
    private static final String XPATH_SELECTOR = "xpath";
    private static final String XPATH_FIND_BY_ATTRIBUTE = "@{0}=''{1}''";
    private static final String XPATH_FIND_BY_TEXT = TEXT_METHOD + "()=''{0}''";
    private static final String XPATH_EXPRESSION = "//{0}[{1}]";

    public enum HTMLStatus {
		NotVerified, Exists, Missing, Changed, Multiple, Invalid
	}
	
    public class MatchedStatus {
        private HTMLStatus status;

        private Element matchedElement;
        
        public MatchedStatus() {
            reset();
        }
        
        public void reset() {
            status = HTMLStatus.NotVerified;
            matchedElement = null;
        }

        public HTMLStatus getStatus() {
            return status;
        }

        public void setStatus(HTMLStatus status) {
            this.status = status;
        }

        public Element getMatchedElement() {
            return matchedElement;
        }

        public void setMatchedElement(Element matchedElement) {
            this.matchedElement = matchedElement;
        }
    }

	protected String name;
	protected String type;
	protected String xpath;
	protected Map<String, String> attributes;
	protected HTMLFrameElement parentElement;
	private MatchedStatus matchedStatus;

    protected HTMLElement() {
        this(StringUtils.EMPTY, StringUtils.EMPTY, Collections.emptyMap(), null);
    }

    public HTMLElement(String name, String type, Map<String, String> attributes, HTMLFrameElement parentElement) {
        this.name = name;
        this.type = type;
        this.parentElement = parentElement;
        if (parentElement != null) {
            parentElement.getChildElements().add(this);
        }
        this.attributes = new LinkedHashMap<>(attributes);
        this.matchedStatus = new MatchedStatus();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public HTMLFrameElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(HTMLFrameElement parentElement) {
		this.parentElement = parentElement;
	}

    public String getXpath() {
        StringBuilder xpathBuilder = new StringBuilder();
        for (Entry<String, String> attr : attributes.entrySet()) {
            String attributeKey = attr.getKey();
            String attributeValue = attr.getValue();
            switch (attributeKey) {
                case XPATH_SELECTOR:
                    return attributeValue;
                case CSS_SELECTOR:
                    continue;
                case TEXT_METHOD:
                    appendAndOperator(xpathBuilder);
                    xpathBuilder.append(MessageFormat.format(XPATH_FIND_BY_TEXT, attributeValue));
                    break;
                default:
                    appendAndOperator(xpathBuilder);
                    xpathBuilder.append(MessageFormat.format(XPATH_FIND_BY_ATTRIBUTE, attributeKey, attributeValue));
            }
        }
        return MessageFormat.format(XPATH_EXPRESSION, StringUtils.defaultIfEmpty(getType(), "*"),
                xpathBuilder.toString());
    }

    private void appendAndOperator(StringBuilder xpathBuilder) {
        if (StringUtils.isNotEmpty(xpathBuilder.toString())) {
            xpathBuilder.append(AND_OPERATOR);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HTMLElement)) {
            return false;
        }
        HTMLElement that = (HTMLElement) object;
        return new EqualsBuilder().append(this.getParentElement(), that.getParentElement())
                .append(this.getName(), that.getName()).append(this.getXpath(), that.getXpath()).isEquals();
    }

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(
				(this.getParentElement() != null) ? this.getParentElement().hashCode() : "" + this.xpath).toHashCode();
	}

	public HTMLPageElement getParentPageElement() {
		if (getParentElement() instanceof HTMLPageElement) {
			return (HTMLPageElement) getParentElement();
		} else if (getParentElement() != null) {
			return getParentElement().getParentPageElement();
		} else {
			return null;
		}
	}

	public String getTypeAttribute() {
		if (type.toLowerCase().equals("input")) {
			return attributes.get("type");
		}
		return StringUtils.EMPTY;
	}

    public MatchedStatus getMatchedStatus() {
        return matchedStatus;
    }

}
