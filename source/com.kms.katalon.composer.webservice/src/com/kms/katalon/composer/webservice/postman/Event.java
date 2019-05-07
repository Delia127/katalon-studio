
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
 * Event
 * <p>
 * Defines a script associated with an associated event name
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "listen",
    "script",
    "disabled"
})
public class Event implements Serializable
{

    /**
     * A unique identifier for the enclosing event.
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("A unique identifier for the enclosing event.")
    private String id;
    /**
     * Can be set to `test` or `prerequest` for test scripts or pre-request scripts respectively.
     * (Required)
     * 
     */
    @JsonProperty("listen")
    @JsonPropertyDescription("Can be set to `test` or `prerequest` for test scripts or pre-request scripts respectively.")
    private String listen;
    /**
     * Script
     * <p>
     * A script is a snippet of Javascript code that can be used to to perform setup or teardown operations on a particular response.
     * 
     */
    @JsonProperty("script")
    @JsonPropertyDescription("A script is a snippet of Javascript code that can be used to to perform setup or teardown operations on a particular response.")
    private Script script;
    /**
     * Indicates whether the event is disabled. If absent, the event is assumed to be enabled.
     * 
     */
    @JsonProperty("disabled")
    @JsonPropertyDescription("Indicates whether the event is disabled. If absent, the event is assumed to be enabled.")
    private boolean disabled = false;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -2894937909754081936L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Event() {
    }

    /**
     * 
     * @param id
     * @param script
     * @param listen
     * @param disabled
     */
    public Event(String id, String listen, Script script, boolean disabled) {
        super();
        this.id = id;
        this.listen = listen;
        this.script = script;
        this.disabled = disabled;
    }

    /**
     * A unique identifier for the enclosing event.
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * A unique identifier for the enclosing event.
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Can be set to `test` or `prerequest` for test scripts or pre-request scripts respectively.
     * (Required)
     * 
     */
    @JsonProperty("listen")
    public String getListen() {
        return listen;
    }

    /**
     * Can be set to `test` or `prerequest` for test scripts or pre-request scripts respectively.
     * (Required)
     * 
     */
    @JsonProperty("listen")
    public void setListen(String listen) {
        this.listen = listen;
    }

    /**
     * Script
     * <p>
     * A script is a snippet of Javascript code that can be used to to perform setup or teardown operations on a particular response.
     * 
     */
    @JsonProperty("script")
    public Script getScript() {
        return script;
    }

    /**
     * Script
     * <p>
     * A script is a snippet of Javascript code that can be used to to perform setup or teardown operations on a particular response.
     * 
     */
    @JsonProperty("script")
    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * Indicates whether the event is disabled. If absent, the event is assumed to be enabled.
     * 
     */
    @JsonProperty("disabled")
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Indicates whether the event is disabled. If absent, the event is assumed to be enabled.
     * 
     */
    @JsonProperty("disabled")
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
        return new ToStringBuilder(this).append("id", id).append("listen", listen).append("script", script).append("disabled", disabled).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(additionalProperties).append(script).append(listen).append(disabled).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Event) == false) {
            return false;
        }
        Event rhs = ((Event) other);
        return new EqualsBuilder().append(id, rhs.id).append(additionalProperties, rhs.additionalProperties).append(script, rhs.script).append(listen, rhs.listen).append(disabled, rhs.disabled).isEquals();
    }

}
