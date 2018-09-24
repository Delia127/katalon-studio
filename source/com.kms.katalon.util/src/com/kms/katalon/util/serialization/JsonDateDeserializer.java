package com.kms.katalon.util.serialization;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.kms.katalon.util.DateTimes;

public class JsonDateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        String iso8601Date = jsonElement.getAsJsonPrimitive().getAsString();
        return DateTimes.parseISO8601(iso8601Date);
    }
}
