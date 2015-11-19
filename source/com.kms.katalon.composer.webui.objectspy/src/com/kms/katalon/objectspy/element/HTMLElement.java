package com.kms.katalon.objectspy.element;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class HTMLElement {
	public enum HTMLStatus {
		NotVerified, Exists, Missing, Changed, Multiple, Invalid
	}

	protected String name;
	protected String type;
	protected String xpath;
	protected Map<String, String> attributes;
	protected HTMLFrameElement parentElement;
	protected HTMLStatus status;

	protected HTMLElement() {
		name = "";
		attributes = new HashMap<>();
		setStatus(HTMLStatus.NotVerified);
	}

	public HTMLElement(String name, String type, Map<String, String> attributes, HTMLFrameElement parentElement) {
		this.name = name;
		this.type = type;
		this.attributes = attributes;
		this.parentElement = parentElement;
		if (parentElement != null) {
			parentElement.getChildElements().add(this);
		}
		setStatus(HTMLStatus.NotVerified);
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
		return attributes.get("xpath");
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

	public HTMLStatus getStatus() {
		return status;
	}

	public void setStatus(HTMLStatus status) {
		this.status = status;
	}

}
