package com.kms.katalon.composer.webservice.postman;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class URL {
    private String raw;
    private String protocol;
    private String[] host;
    private String[] path;
    private List<Variable> variable;
    private List<Query> query;
    
    @JsonProperty("query")
    public List<Query> getQuery() {
        return query;
    }
    @JsonProperty("query")
    public void setQuery(List<Query> query) {
        this.query = query;
    }
    @JsonProperty("raw")
    public String getRaw() { return raw; }
    @JsonProperty("raw")
    public void setRaw(String value) { this.raw = value; }

    @JsonProperty("protocol")
    public String getProtocol() { return protocol; }
    @JsonProperty("protocol")
    public void setProtocol(String value) { this.protocol = value; }

    @JsonProperty("host")
    public String[] getHost() { return host; }
    @JsonProperty("host")
    public void setHost(String[] value) { this.host = value; }

    @JsonProperty("path")
    public String[] getPath() { return path; }
    @JsonProperty("path")
    public void setPath(String[] value) { this.path = value; }

    @JsonProperty("variable")
    public List<Variable> getVariable() { return variable; }
    @JsonProperty("variable")
    public void setVariable(List<Variable> value) { this.variable = value; }
}
