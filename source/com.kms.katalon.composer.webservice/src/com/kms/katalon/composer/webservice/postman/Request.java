package com.kms.katalon.composer.webservice.postman;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class Request {
    private String method;
    private List<Header> header;
    private Body body;
    private URL url;
    private String description;
    private Auth auth;

    @JsonProperty("method")
    public String getMethod() { return method; }
    @JsonProperty("method")
    public void setMethod(String value) { this.method = value; }

    @JsonProperty("header")
    public List<Header> getHeader() { return header; }
    @JsonProperty("header")
    public void setHeader(List<Header> value) { this.header = value; }

    @JsonProperty("body")
    public Body getBody() { return body; }
    @JsonProperty("body")
    public void setBody(Body value) { this.body = value; }

    @JsonProperty("url")
    public URL getURL() { return url; }
    @JsonProperty("url")
    public void setURL(URL value) { this.url = value; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String value) { this.description = value; }

    @JsonProperty("auth")
    public Auth getAuth() { return auth; }
    @JsonProperty("auth")
    public void setAuth(Auth value) { this.auth = value; }
}