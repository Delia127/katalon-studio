package com.kms.katalon.objectspy.element;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class HTMLPageElement extends HTMLFrameElement {

	private String url;

	public HTMLPageElement() {
		super();
	}

	public HTMLPageElement(String name, Map<String, String> attributes, List<HTMLElement> childElements, String url) {
		super(name, "PAGE", attributes, null, childElements);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HTMLPageElement)) {
            return false;
        }
        HTMLPageElement that = (HTMLPageElement) object;
        return new EqualsBuilder().append(this.getUrl(), that.getUrl()).append(this.getName(), that.getName())
                .isEquals();
    }

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).appendSuper(super.hashCode()).append(this.getUrl()).toHashCode();
	}
}
