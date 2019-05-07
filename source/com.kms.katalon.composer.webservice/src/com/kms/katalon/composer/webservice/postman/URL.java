package com.kms.katalon.composer.webservice.postman;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class URL {
    private String raw;
    private Protocol protocol;
    private List<Host> host;
    private String[] path;
    private List<Variable> variable;

    @JsonProperty("raw")
    public String getRaw() { return raw; }
    @JsonProperty("raw")
    public void setRaw(String value) { this.raw = value; }

    @JsonProperty("protocol")
    public Protocol getProtocol() { return protocol; }
    @JsonProperty("protocol")
    public void setProtocol(Protocol value) { this.protocol = value; }

    @JsonProperty("host")
    public List<Host> getHost() { return host; }
    @JsonProperty("host")
    public void setHost(List<Host> value) { this.host = value; }

    @JsonProperty("path")
    public String[] getPath() { return path; }
    @JsonProperty("path")
    public void setPath(String[] value) { this.path = value; }

    @JsonProperty("variable")
    public List<Variable> getVariable() { return variable; }
    @JsonProperty("variable")
    public void setVariable(List<Variable> value) { this.variable = value; }
}
