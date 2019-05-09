package com.kms.katalon.composer.webservice.postman;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

public class Body {
    private String mode;

    private List<FormData> formdata;

    private String raw;

    private List<Urlencoded> urlencoded;

    @JsonProperty("urlencoded")

    public List<Urlencoded> getUrlencoded() {
        return urlencoded;
    }

    @JsonProperty("urlencoded")

    public void setUrlencoded(List<Urlencoded> urlencoded) {
        this.urlencoded = urlencoded;
    }

    @JsonProperty("formdata")
    public List<FormData> getFormdata() {
        return formdata;
    }

    @JsonProperty("formdata")
    public void setFormdata(List<FormData> formdata) {
        this.formdata = formdata;
    }

    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public void setMode(String value) {
        this.mode = value;
    }

    @JsonProperty("raw")
    public String getRaw() {
        return raw;
    }

    @JsonProperty("raw")
    public void setRaw(String value) {
        this.raw = value;
    }
}
