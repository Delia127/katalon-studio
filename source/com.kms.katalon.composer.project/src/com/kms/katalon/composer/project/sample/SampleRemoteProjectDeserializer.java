package com.kms.katalon.composer.project.sample;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class SampleRemoteProjectDeserializer implements JsonDeserializer<SampleRemoteProject> {

    @Override
    public SampleRemoteProject deserialize(JsonElement jsElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        SampleRemoteProject sampleRemoteProject = new SampleRemoteProject();
        JsonObject jsObject = jsElement.getAsJsonObject();
        sampleRemoteProject.setName(jsObject.get("name").getAsString());
        sampleRemoteProject.setSourceUrl(jsObject.get("source_url").getAsString());
        sampleRemoteProject.setDefaultBranch(jsObject.get("default_branch").getAsString());
        sampleRemoteProject.setType(SampleProjectType.fromString(jsObject.get("type").getAsString()));

        JsonObject thumbnailsJson = jsObject.get("thumbnails").getAsJsonObject();
        Map<Integer, String> thumbnails = new HashMap<>();
        thumbnailsJson.entrySet().forEach(e -> {
            String sizeAsString = e.getKey();
            int size = Math.round(NumberUtils.toFloat(sizeAsString.replaceAll("x", "")) * 100);
            thumbnails.put(size, e.getValue().getAsString());
        });
        sampleRemoteProject.setThumbnails(thumbnails);
        return sampleRemoteProject;
    }
}
