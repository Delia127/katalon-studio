package com.kms.katalon.composer.webservice.postman;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Host {
    API, ENVIRONMENT;

    @JsonValue
    public String toValue() {
        switch (this) {
        case API: return "api";
        case ENVIRONMENT: return "{{environment}}";
        }
        return null;
    }

    @JsonCreator
    public static Host forValue(String value) throws IOException {
        if (value.equals("api")) return API;
        if (value.equals("{{environment}}")) return ENVIRONMENT;
        throw new IOException("Cannot deserialize Host");
    }
}
