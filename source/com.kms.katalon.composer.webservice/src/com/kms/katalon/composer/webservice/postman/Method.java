
package com.kms.katalon.composer.webservice.postman;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Method {
    DELETE, GET, PATCH, POST, PUT,COPY,HEAD,OPTIONS,LINK,UNLINK, PURGE, LOCK, UNLOCK,PROPFIND, VIEW;
    

    @JsonValue
    public String toValue() {
        switch (this) {
        case DELETE: return "DELETE";
        case GET: return "GET";
        case PATCH: return "PATCH";
        case POST: return "POST";
        case PUT: return "PUT";
        case COPY: return "COPY";
        case HEAD : return "HEAD";
        case OPTIONS: return "OPTIONS";
        case LINK: return "LINK";
        case UNLINK : return "UNLINK";
        case PURGE : return "PURGE";
        case LOCK: return "LOCK";
        case UNLOCK: return "UNLOCK";
        case PROPFIND: return "PROPFIND";
        case VIEW : return "VIEW";
        }
        return null;
    }

    @JsonCreator
    public static Method forValue(String value) throws IOException {
        if (value.equals("DELETE")) return DELETE;
        if (value.equals("GET")) return GET;
        if (value.equals("PATCH")) return PATCH;
        if (value.equals("POST")) return POST;
        if (value.equals("PUT")) return PUT;
        if (value.equals("COPY")) return COPY;
        if (value.equals("HEAD")) return HEAD;
        if (value.equals("OPTIONS")) return OPTIONS;
        if (value.equals("LINK")) return LINK;
        if (value.equals("UNLINK")) return UNLINK;
        if (value.equals("PURGE")) return PURGE;
        if (value.equals("LOCK")) return LOCK;
        if (value.equals("UNLOCK")) return UNLOCK;
        if (value.equals("PROPFIND")) return PROPFIND;
        if (value.equals("VIEW")) return VIEW;
        throw new IOException("Cannot deserialize Method");
    }
    
}
