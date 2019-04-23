package com.kms.katalon.composer.webservice.postman;

import java.util.*;
import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Protocol {
    HTTPS;

    @JsonValue
    public String toValue() {
        switch (this) {
        case HTTPS: return "https";
        }
        return null;
    }

    @JsonCreator
    public static Protocol forValue(String value) throws IOException {
        if (value.equals("https")) return HTTPS;
        throw new IOException("Cannot deserialize Protocol");
    }
}
