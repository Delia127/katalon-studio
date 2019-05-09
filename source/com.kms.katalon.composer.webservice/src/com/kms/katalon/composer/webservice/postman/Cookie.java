
package com.kms.katalon.composer.webservice.postman;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Cookie
 * <p>
 * A Cookie, that follows the [Google Chrome format](https://developer.chrome.com/extensions/cookies)
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "domain",
    "expires",
    "maxAge",
    "hostOnly",
    "httpOnly",
    "name",
    "path",
    "secure",
    "session",
    "value",
    "extensions"
})
public class Cookie implements Serializable
{

    /**
     * The domain for which this cookie is valid.
     * (Required)
     * 
     */
    @JsonProperty("domain")
    @JsonPropertyDescription("The domain for which this cookie is valid.")
    private String domain;
    /**
     * When the cookie expires.
     * 
     */
    @JsonProperty("expires")
    @JsonPropertyDescription("When the cookie expires.")
    private String expires;
    @JsonProperty("maxAge")
    private String maxAge;
    /**
     * True if the cookie is a host-only cookie. (i.e. a request's URL domain must exactly match the domain of the cookie).
     * 
     */
    @JsonProperty("hostOnly")
    @JsonPropertyDescription("True if the cookie is a host-only cookie. (i.e. a request's URL domain must exactly match the domain of the cookie).")
    private boolean hostOnly;
    /**
     * Indicates if this cookie is HTTP Only. (if True, the cookie is inaccessible to client-side scripts)
     * 
     */
    @JsonProperty("httpOnly")
    @JsonPropertyDescription("Indicates if this cookie is HTTP Only. (if True, the cookie is inaccessible to client-side scripts)")
    private boolean httpOnly;
    /**
     * This is the name of the Cookie.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("This is the name of the Cookie.")
    private String name;
    /**
     * The path associated with the Cookie.
     * (Required)
     * 
     */
    @JsonProperty("path")
    @JsonPropertyDescription("The path associated with the Cookie.")
    private String path;
    /**
     * Indicates if the 'secure' flag is set on the Cookie, meaning that it is transmitted over secure connections only. (typically HTTPS)
     * 
     */
    @JsonProperty("secure")
    @JsonPropertyDescription("Indicates if the 'secure' flag is set on the Cookie, meaning that it is transmitted over secure connections only. (typically HTTPS)")
    private boolean secure;
    /**
     * True if the cookie is a session cookie.
     * 
     */
    @JsonProperty("session")
    @JsonPropertyDescription("True if the cookie is a session cookie.")
    private boolean session;
    /**
     * The value of the Cookie.
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("The value of the Cookie.")
    private String value;
    /**
     * Custom attributes for a cookie go here, such as the [Priority Field](https://code.google.com/p/chromium/issues/detail?id=232693)
     * 
     */
    @JsonProperty("extensions")
    @JsonPropertyDescription("Custom attributes for a cookie go here, such as the [Priority Field](https://code.google.com/p/chromium/issues/detail?id=232693)")
    private List<Object> extensions = new ArrayList<Object>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -8532676222063568530L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Cookie() {
    }

    /**
     * 
     * @param maxAge
     * @param expires
     * @param session
     * @param name
     * @param value
     * @param secure
     * @param path
     * @param domain
     * @param httpOnly
     * @param extensions
     * @param hostOnly
     */
    public Cookie(String domain, String expires, String maxAge, boolean hostOnly, boolean httpOnly, String name, String path, boolean secure, boolean session, String value, List<Object> extensions) {
        super();
        this.domain = domain;
        this.expires = expires;
        this.maxAge = maxAge;
        this.hostOnly = hostOnly;
        this.httpOnly = httpOnly;
        this.name = name;
        this.path = path;
        this.secure = secure;
        this.session = session;
        this.value = value;
        this.extensions = extensions;
    }

    /**
     * The domain for which this cookie is valid.
     * (Required)
     * 
     */
    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    /**
     * The domain for which this cookie is valid.
     * (Required)
     * 
     */
    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * When the cookie expires.
     * 
     */
    @JsonProperty("expires")
    public String getExpires() {
        return expires;
    }

    /**
     * When the cookie expires.
     * 
     */
    @JsonProperty("expires")
    public void setExpires(String expires) {
        this.expires = expires;
    }

    @JsonProperty("maxAge")
    public String getMaxAge() {
        return maxAge;
    }

    @JsonProperty("maxAge")
    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * True if the cookie is a host-only cookie. (i.e. a request's URL domain must exactly match the domain of the cookie).
     * 
     */
    @JsonProperty("hostOnly")
    public boolean isHostOnly() {
        return hostOnly;
    }

    /**
     * True if the cookie is a host-only cookie. (i.e. a request's URL domain must exactly match the domain of the cookie).
     * 
     */
    @JsonProperty("hostOnly")
    public void setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
    }

    /**
     * Indicates if this cookie is HTTP Only. (if True, the cookie is inaccessible to client-side scripts)
     * 
     */
    @JsonProperty("httpOnly")
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Indicates if this cookie is HTTP Only. (if True, the cookie is inaccessible to client-side scripts)
     * 
     */
    @JsonProperty("httpOnly")
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * This is the name of the Cookie.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * This is the name of the Cookie.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The path associated with the Cookie.
     * (Required)
     * 
     */
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    /**
     * The path associated with the Cookie.
     * (Required)
     * 
     */
    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Indicates if the 'secure' flag is set on the Cookie, meaning that it is transmitted over secure connections only. (typically HTTPS)
     * 
     */
    @JsonProperty("secure")
    public boolean isSecure() {
        return secure;
    }

    /**
     * Indicates if the 'secure' flag is set on the Cookie, meaning that it is transmitted over secure connections only. (typically HTTPS)
     * 
     */
    @JsonProperty("secure")
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * True if the cookie is a session cookie.
     * 
     */
    @JsonProperty("session")
    public boolean isSession() {
        return session;
    }

    /**
     * True if the cookie is a session cookie.
     * 
     */
    @JsonProperty("session")
    public void setSession(boolean session) {
        this.session = session;
    }

    /**
     * The value of the Cookie.
     * 
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * The value of the Cookie.
     * 
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Custom attributes for a cookie go here, such as the [Priority Field](https://code.google.com/p/chromium/issues/detail?id=232693)
     * 
     */
    @JsonProperty("extensions")
    public List<Object> getExtensions() {
        return extensions;
    }

    /**
     * Custom attributes for a cookie go here, such as the [Priority Field](https://code.google.com/p/chromium/issues/detail?id=232693)
     * 
     */
    @JsonProperty("extensions")
    public void setExtensions(List<Object> extensions) {
        this.extensions = extensions;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("domain", domain).append("expires", expires).append("maxAge", maxAge).append("hostOnly", hostOnly).append("httpOnly", httpOnly).append("name", name).append("path", path).append("secure", secure).append("session", session).append("value", value).append("extensions", extensions).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(secure).append(extensions).append(maxAge).append(expires).append(session).append(additionalProperties).append(name).append(value).append(path).append(domain).append(httpOnly).append(hostOnly).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cookie) == false) {
            return false;
        }
        Cookie rhs = ((Cookie) other);
        return new EqualsBuilder().append(secure, rhs.secure).append(extensions, rhs.extensions).append(maxAge, rhs.maxAge).append(expires, rhs.expires).append(session, rhs.session).append(additionalProperties, rhs.additionalProperties).append(name, rhs.name).append(value, rhs.value).append(path, rhs.path).append(domain, rhs.domain).append(httpOnly, rhs.httpOnly).append(hostOnly, rhs.hostOnly).isEquals();
    }

}
