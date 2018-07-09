package com.kms.katalon.integration.kobiton.entity;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class KobitonJsonDeserializer implements JsonDeserializer<KobitonDevice> {

    @Override
    public KobitonDevice deserialize(JsonElement jsElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        KobitonDevice device = new Gson().fromJson(jsElement, KobitonDevice.class);
        KobitonDeviceCapabilities deviceCapabilities = new Gson().fromJson(jsElement, KobitonDeviceCapabilities.class);
                device.setCapabilities(deviceCapabilities);
        return device;
    }

}
