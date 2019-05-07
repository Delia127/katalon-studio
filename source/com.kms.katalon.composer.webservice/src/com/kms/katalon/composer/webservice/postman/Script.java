
package com.kms.katalon.composer.webservice.postman;

import java.io.Serializable;
import java.util.HashMap;
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
 * Script
 * <p>
 * A script is a snippet of Javascript code that can be used to to perform setup or teardown operations on a particular response.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "type",
    "exec",
    "src",
    "name"
})
public class Script implements Serializable
{

    /**
     * A unique, user defined identifier that can  be used to refer to this script from requests.
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("A unique, user defined identifier that can  be used to refer to this script from requests.")
    private String id;
    /**
     * Type of the script. E.g: 'text/javascript'
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("Type of the script. E.g: 'text/javascript'")
    private String type;
    @JsonProperty("exec")
    private Object exec;
    /**
     * Url
     * <p>
     * If object, contains the complete broken-down URL for this request. If string, contains the literal request URL.
     * 
     */
    @JsonProperty("src")
    @JsonPropertyDescription("If object, contains the complete broken-down URL for this request. If string, contains the literal request URL.")
    private Object src;
    /**
     * Script name
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Script name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 556067109555570876L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Script() {
    }

    /**
     * 
     * @param id
     * @param name
     * @param exec
     * @param src
     * @param type
     */
    public Script(String id, String type, Object exec, Object src, String name) {
        super();
        this.id = id;
        this.type = type;
        this.exec = exec;
        this.src = src;
        this.name = name;
    }

    /**
     * A unique, user defined identifier that can  be used to refer to this script from requests.
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * A unique, user defined identifier that can  be used to refer to this script from requests.
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Type of the script. E.g: 'text/javascript'
     * 
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * Type of the script. E.g: 'text/javascript'
     * 
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("exec")
    public Object getExec() {
        return exec;
    }

    @JsonProperty("exec")
    public void setExec(Object exec) {
        this.exec = exec;
    }

    /**
     * Url
     * <p>
     * If object, contains the complete broken-down URL for this request. If string, contains the literal request URL.
     * 
     */
    @JsonProperty("src")
    public Object getSrc() {
        return src;
    }

    /**
     * Url
     * <p>
     * If object, contains the complete broken-down URL for this request. If string, contains the literal request URL.
     * 
     */
    @JsonProperty("src")
    public void setSrc(Object src) {
        this.src = src;
    }

    /**
     * Script name
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Script name
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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
        return new ToStringBuilder(this).append("id", id).append("type", type).append("exec", exec).append("src", src).append("name", name).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(additionalProperties).append(name).append(exec).append(src).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Script) == false) {
            return false;
        }
        Script rhs = ((Script) other);
        return new EqualsBuilder().append(id, rhs.id).append(additionalProperties, rhs.additionalProperties).append(name, rhs.name).append(exec, rhs.exec).append(src, rhs.src).append(type, rhs.type).isEquals();
    }

}
