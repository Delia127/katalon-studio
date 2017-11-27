package com.kms.katalon.integration.kobiton.entity;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class KobitonJsonDeserializer implements JsonDeserializer<KobitonDevice> {

    @Override
    public KobitonDevice deserialize(JsonElement jsElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject deviceAsJson = jsElement.getAsJsonObject();
        KobitonDevice device = new KobitonDevice();
        device.setId(deviceAsJson.getAsJsonPrimitive("id").getAsInt());
        device.setBooked(deviceAsJson.getAsJsonPrimitive("isBooked").getAsBoolean());
        KobitonDeviceCapabilities deviceCapabilities = new Gson().fromJson(jsElement, KobitonDeviceCapabilities.class);
                device.setCapabilities(deviceCapabilities);
        return device;
    }

}
