package com.kms.katalon.composer.webservice.postman;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class Body {
    private Mode mode;
    private String raw;

    @JsonProperty("mode")
    public Mode getMode() { return mode; }
    @JsonProperty("mode")
    public void setMode(Mode value) { this.mode = value; }

    @JsonProperty("raw")
    public String getRaw() { return raw; }
    @JsonProperty("raw")
    public void setRaw(String value) { this.raw = value; }
}