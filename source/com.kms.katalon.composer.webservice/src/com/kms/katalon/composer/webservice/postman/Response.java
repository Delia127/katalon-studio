
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
 * Response
 * <p>
 * A response represents an HTTP response.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "originalRequest",
    "responseTime",
    "timings",
    "header",
    "cookie",
    "body",
    "status",
    "code"
})
public class Response implements Serializable
{

    /**
     * A unique, user defined identifier that can  be used to refer to this response from requests.
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("A unique, user defined identifier that can  be used to refer to this response from requests.")
    private String id;
    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * 
     */
    @JsonProperty("originalRequest")
    @JsonPropertyDescription("A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.")
    private Object originalRequest;
    /**
     * ResponseTime
     * <p>
     * The time taken by the request to complete. If a number, the unit is milliseconds. If the response is manually created, this can be set to `null`.
     * 
     */
    @JsonProperty("responseTime")
    @JsonPropertyDescription("The time taken by the request to complete. If a number, the unit is milliseconds. If the response is manually created, this can be set to `null`.")
    private String responseTime;
    /**
     * Response Timings
     * <p>
     * Set of timing information related to request and response in milliseconds
     * 
     */
    @JsonProperty("timings")
    @JsonPropertyDescription("Set of timing information related to request and response in milliseconds")
    private Timings timings;
    /**
     * Headers
     * <p>
     * 
     * 
     */
    @JsonProperty("header")
    private Object header;
    @JsonProperty("cookie")
    private List<Cookie> cookie = new ArrayList<Cookie>();
    /**
     * The raw text of the response.
     * 
     */
    @JsonProperty("body")
    @JsonPropertyDescription("The raw text of the response.")
    private String body;
    /**
     * The response status, e.g: '200 OK'
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("The response status, e.g: '200 OK'")
    private String status;
    /**
     * The numerical response code, example: 200, 201, 404, etc.
     * 
     */
    @JsonProperty("code")
    @JsonPropertyDescription("The numerical response code, example: 200, 201, 404, etc.")
    private int code;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -4746691791436203016L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Response() {
    }

    /**
     * 
     * @param cookie
     * @param id
     * @param body
     * @param status
     * @param timings
     * @param code
     * @param originalRequest
     * @param header
     * @param responseTime
     */
    public Response(String id, Object originalRequest, String responseTime, Timings timings, Object header, List<Cookie> cookie, String body, String status, int code) {
        super();
        this.id = id;
        this.originalRequest = originalRequest;
        this.responseTime = responseTime;
        this.timings = timings;
        this.header = header;
        this.cookie = cookie;
        this.body = body;
        this.status = status;
        this.code = code;
    }

    /**
     * A unique, user defined identifier that can  be used to refer to this response from requests.
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * A unique, user defined identifier that can  be used to refer to this response from requests.
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * 
     */
    @JsonProperty("originalRequest")
    public Object getOriginalRequest() {
        return originalRequest;
    }

    /**
     * Request
     * <p>
     * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
     * 
     */
    @JsonProperty("originalRequest")
    public void setOriginalRequest(Object originalRequest) {
        this.originalRequest = originalRequest;
    }

    /**
     * ResponseTime
     * <p>
     * The time taken by the request to complete. If a number, the unit is milliseconds. If the response is manually created, this can be set to `null`.
     * 
     */
    @JsonProperty("responseTime")
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * ResponseTime
     * <p>
     * The time taken by the request to complete. If a number, the unit is milliseconds. If the response is manually created, this can be set to `null`.
     * 
     */
    @JsonProperty("responseTime")
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * Response Timings
     * <p>
     * Set of timing information related to request and response in milliseconds
     * 
     */
    @JsonProperty("timings")
    public Timings getTimings() {
        return timings;
    }

    /**
     * Response Timings
     * <p>
     * Set of timing information related to request and response in milliseconds
     * 
     */
    @JsonProperty("timings")
    public void setTimings(Timings timings) {
        this.timings = timings;
    }

    /**
     * Headers
     * <p>
     * 
     * 
     */
    @JsonProperty("header")
    public Object getHeader() {
        return header;
    }

    /**
     * Headers
     * <p>
     * 
     * 
     */
    @JsonProperty("header")
    public void setHeader(Object header) {
        this.header = header;
    }

    @JsonProperty("cookie")
    public List<Cookie> getCookie() {
        return cookie;
    }

    @JsonProperty("cookie")
    public void setCookie(List<Cookie> cookie) {
        this.cookie = cookie;
    }

    /**
     * The raw text of the response.
     * 
     */
    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    /**
     * The raw text of the response.
     * 
     */
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * The response status, e.g: '200 OK'
     * 
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * The response status, e.g: '200 OK'
     * 
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * The numerical response code, example: 200, 201, 404, etc.
     * 
     */
    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    /**
     * The numerical response code, example: 200, 201, 404, etc.
     * 
     */
    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
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
        return new ToStringBuilder(this).append("id", id).append("originalRequest", originalRequest).append("responseTime", responseTime).append("timings", timings).append("header", header).append("cookie", cookie).append("body", body).append("status", status).append("code", code).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cookie).append(id).append(body).append(status).append(additionalProperties).append(timings).append(code).append(originalRequest).append(header).append(responseTime).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Response) == false) {
            return false;
        }
        Response rhs = ((Response) other);
        return new EqualsBuilder().append(cookie, rhs.cookie).append(id, rhs.id).append(body, rhs.body).append(status, rhs.status).append(additionalProperties, rhs.additionalProperties).append(timings, rhs.timings).append(code, rhs.code).append(originalRequest, rhs.originalRequest).append(header, rhs.header).append(responseTime, rhs.responseTime).isEquals();
    }

}
