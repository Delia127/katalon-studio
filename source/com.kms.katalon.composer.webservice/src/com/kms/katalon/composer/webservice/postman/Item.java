
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
 * Item
 * <p>
 * Items are entities which contain an actual HTTP request, and sample responses attached to it.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "description",
    "variable",
    "event",
    "request",
    "response",
    "protocolProfileBehavior"
})
public class Item implements Serializable
{

    /**
     * A unique ID that is used to identify collections internally
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("A unique ID that is used to identify collections internally")
    private String id;
    /**
     * A human readable identifier for the current item.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("A human readable identifier for the current item.")
    private String name;
    /**
     * A Description can be a raw text, or be an object, which holds the description along with its format.
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("A Description can be a raw text, or be an object, which holds the description along with its format.")
    private Object description;
    /**
     * Variable List
     * <p>
     * Collection variables allow you to define a set of variables, that are a *part of the collection*, as opposed to environments, which are separate entities.
     * *Note: Collection variables must not contain any sensitive information.*
     * 
     */
    @JsonProperty("variable")
    @JsonPropertyDescription("Collection variables allow you to define a set of variables, that are a *part of the collection*, as opposed to environments, which are separate entities.\n*Note: Collection variables must not contain any sensitive information.*")
    private List<Variable> variable = new ArrayList<Variable>();
    /**
     * Event List
     * <p>
     * Postman allows you to configure scripts to run when specific events occur. These scripts are stored here, and can be referenced in the collection by their ID.
     * 
     */
    @JsonProperty("event")
    @JsonPropertyDescription("Postman allows you to configure scripts to run when specific events occur. These scripts are stored here, and can be referenced in the collection by their ID.")
    private List<Event> event = new ArrayList<Event>();
    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * (Required)
     * 
     */
    @JsonProperty("request")
    @JsonPropertyDescription("A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.")
    private Request request;
    /**
     * Responses
     * <p>
     * 
     * 
     */
    @JsonProperty("response")
    private List<Response> response = new ArrayList<Response>();
    /**
     * Protocol Profile Behavior
     * <p>
     * Set of configurations used to alter the usual behavior of sending the request
     * 
     */
    @JsonProperty("protocolProfileBehavior")
    @JsonPropertyDescription("Set of configurations used to alter the usual behavior of sending the request")
    private ProtocolProfileBehavior protocolProfileBehavior;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 1907640689380590863L;

    @JsonProperty("auth")
    private Auth auth;
    
    /**
     * Items are entities which contain an actual HTTP request, and sample responses attached to it. Folders may contain many items.
     * (Required)
     * 
     */
    @JsonProperty("item")
    @JsonPropertyDescription("Items are entities which contain an actual HTTP request, and sample responses attached to it. Folders may contain many items.")
    private List<Item> item = new ArrayList<Item>();
    
    /**
     * No args constructor for use in serialization
     * 
     */
    public Item() {
    }

    /**
     * 
     * @param response
     * @param id
     * @param protocolProfileBehavior
     * @param event
     * @param description
     * @param request
     * @param name
     * @param variable
     */
    public Item(String id, String name, Object description, List<Variable> variable, List<Item> item, List<Event> event, Auth auth, Request request, List<Response> response, ProtocolProfileBehavior protocolProfileBehavior) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.variable = variable;
        this.event = event;
        this.request = request;
        this.response = response;
        this.protocolProfileBehavior = protocolProfileBehavior;
        this.auth = auth;
        this.item = item;
    }

    /**
     * A unique ID that is used to identify collections internally
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * A unique ID that is used to identify collections internally
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * A human readable identifier for the current item.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * A human readable identifier for the current item.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A Description can be a raw text, or be an object, which holds the description along with its format.
     * 
     */
    @JsonProperty("description")
    public Object getDescription() {
        return description;
    }

    /**
     * A Description can be a raw text, or be an object, which holds the description along with its format.
     * 
     */
    @JsonProperty("description")
    public void setDescription(Object description) {
        this.description = description;
    }

    /**
     * Variable List
     * <p>
     * Collection variables allow you to define a set of variables, that are a *part of the collection*, as opposed to environments, which are separate entities.
     * *Note: Collection variables must not contain any sensitive information.*
     * 
     */
    @JsonProperty("variable")
    public List<Variable> getVariable() {
        return variable;
    }

    /**
     * Variable List
     * <p>
     * Collection variables allow you to define a set of variables, that are a *part of the collection*, as opposed to environments, which are separate entities.
     * *Note: Collection variables must not contain any sensitive information.*
     * 
     */
    @JsonProperty("variable")
    public void setVariable(List<Variable> variable) {
        this.variable = variable;
    }

    /**
     * Event List
     * <p>
     * Postman allows you to configure scripts to run when specific events occur. These scripts are stored here, and can be referenced in the collection by their ID.
     * 
     */
    @JsonProperty("event")
    public List<Event> getEvent() {
        return event;
    }

    /**
     * Event List
     * <p>
     * Postman allows you to configure scripts to run when specific events occur. These scripts are stored here, and can be referenced in the collection by their ID.
     * 
     */
    @JsonProperty("event")
    public void setEvent(List<Event> event) {
        this.event = event;
    }

    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * (Required)
     * 
     */
    @JsonProperty("request")
    public Request getRequest() {
        return request;
    }

    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * (Required)
     * 
     */
    @JsonProperty("request")
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Responses
     * <p>
     * 
     * 
     */
    @JsonProperty("response")
    public List<Response> getResponse() {
        return response;
    }

    /**
     * Responses
     * <p>
     * 
     * 
     */
    @JsonProperty("response")
    public void setResponse(List<Response> response) {
        this.response = response;
    }

    /**
     * Items are entities which contain an actual HTTP request, and sample responses attached to it. Folders may contain many items.
     * (Required)
     * 
     */
    @JsonProperty("item")
    public List<Item> getItem() {
        return item;
    }

    /**
     * Items are entities which contain an actual HTTP request, and sample responses attached to it. Folders may contain many items.
     * (Required)
     * 
     */
    @JsonProperty("item")
    public void setItem(List<Item> item) {
        this.item = item;
    }

    /**
     * 
     */
    @JsonProperty("auth")
    public Auth getAuth() {
        return auth;
    }

    @JsonProperty("auth")
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     * Protocol Profile Behavior
     * <p>
     * Set of configurations used to alter the usual behavior of sending the request
     * 
     */
    @JsonProperty("protocolProfileBehavior")
    public ProtocolProfileBehavior getProtocolProfileBehavior() {
        return protocolProfileBehavior;
    }

    /**
     * Protocol Profile Behavior
     * <p>
     * Set of configurations used to alter the usual behavior of sending the request
     * 
     */
    @JsonProperty("protocolProfileBehavior")
    public void setProtocolProfileBehavior(ProtocolProfileBehavior protocolProfileBehavior) {
        this.protocolProfileBehavior = protocolProfileBehavior;
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
        return new ToStringBuilder(this).append("id", id).append("name", name).append("description", description).append("variable", variable).append("event", event).append("request", request).append("response", response).append("protocolProfileBehavior", protocolProfileBehavior).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(response).append(id).append(protocolProfileBehavior).append(additionalProperties).append(event).append(description).append(request).append(name).append(variable).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Item) == false) {
            return false;
        }
        Item rhs = ((Item) other);
        return new EqualsBuilder().append(response, rhs.response).append(id, rhs.id).append(protocolProfileBehavior, rhs.protocolProfileBehavior).append(additionalProperties, rhs.additionalProperties).append(event, rhs.event).append(description, rhs.description).append(request, rhs.request).append(name, rhs.name).append(variable, rhs.variable).isEquals();
    }

}
