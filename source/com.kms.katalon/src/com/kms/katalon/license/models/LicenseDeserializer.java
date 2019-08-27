package com.kms.katalon.license.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LicenseDeserializer  extends StdDeserializer<License> {
	private static final long serialVersionUID = -6680547539976257292L;

	public LicenseDeserializer() {
        this(null);
    }

    public LicenseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public License deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        License license = new License();
        JsonNode node = jp.getCodec().readTree(jp);

        if (node.has("machineId")) {
            String machineId = node.get("machineId").asText();
            license.setMachineId(machineId);
        }
        if (node.has("expirationDate")) {
            Date expiration = new Date(node.get("expirationDate").asLong());
            license.setExpirationDate(expiration);
        }
        if (node.has("organizationId")) {
            long organizationId = node.get("organizationId").asLong();
            license.setOrganizationId(organizationId);
        }
        if (node.has("features")) {
            List<Feature> features = deserializeFeaturesField(node);
            license.setFeatures(features);
        }
        return license;
    }

    private List<Feature> deserializeFeaturesField(JsonNode node) {
        ArrayNode featureNodes = (ArrayNode)node.get("features");
        List<Feature> features = new ArrayList<>();
        featureNodes.forEach(featureNode -> {
            Feature feature = new Feature();
            feature.setName(featureNode.get("name").asText());
            feature.setKey(featureNode.get("key").asText());
            feature.setId(featureNode.get("id").asLong());
            features.add(feature);
        });
        return features;
    }
	
}
