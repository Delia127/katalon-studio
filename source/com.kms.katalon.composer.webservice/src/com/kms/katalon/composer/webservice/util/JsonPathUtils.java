package com.kms.katalon.composer.webservice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPathUtils {

    public static Map<Integer, String> evaluateJsonPath(String jsonString) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return Collections.emptyMap();
        }
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(jsonString);
        IndexedJsonNode indexedNode = new IndexedJsonNode();
        indexedNode.key = "";
        indexedNode.index = 0;
        indexedNode.jsonPath = "$";
        indexedNode.jsonProperty = "";
        indexedNode.endLine = indexedNode.startLine = 0;
        IndexedJsonNode evaluatedRootNode = walk(null, indexedNode, rootNode);
        return collectJsonPath(evaluatedRootNode);
    }
    
    public static Map<Integer, String> evaluateJsonProperty(String jsonString) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return Collections.emptyMap();
        }
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(jsonString);
        IndexedJsonNode indexedNode = new IndexedJsonNode();
        indexedNode.key = "";
        indexedNode.index = 0;
        indexedNode.jsonPath = "$";
        indexedNode.jsonProperty = "";
        indexedNode.endLine = indexedNode.startLine = 0;
        IndexedJsonNode evaluatedRootNode = walk(null, indexedNode, rootNode);
        return collectJsonProperty(evaluatedRootNode);
    }

    private static Map<Integer, String> collectJsonPath(IndexedJsonNode node) {
        Map<Integer, String> jsonPathCollection = new HashMap<>();
        jsonPathCollection.put(node.startLine, node.jsonPath);
        for (IndexedJsonNode childNode : node.children) {
            jsonPathCollection.putAll(collectJsonPath(childNode));
        }
        return jsonPathCollection;
    }

    private static Map<Integer, String> collectJsonProperty(IndexedJsonNode node) {
        Map<Integer, String> jsonPathCollection = new HashMap<>();
        jsonPathCollection.put(node.startLine, node.jsonProperty);
        for (IndexedJsonNode childNode : node.children) {
            jsonPathCollection.putAll(collectJsonProperty(childNode));
        }
        return jsonPathCollection;
    }

    private static IndexedJsonNode walk(IndexedJsonNode parentIndexedNode, IndexedJsonNode indexedNode, JsonNode node) {
        if (parentIndexedNode == null) {
            indexedNode.endLine = indexedNode.startLine = 0;
        } else {
            indexedNode.endLine = indexedNode.startLine = parentIndexedNode.endLine + 1;
        }

        switch (node.getNodeType()) {
            case ARRAY: {
                Iterator<JsonNode> childrenIterator = node.iterator();
                int index = 0;
                while (childrenIterator.hasNext()) {
                    JsonNode childNode = childrenIterator.next();
                    IndexedJsonNode indexedChild = new IndexedJsonNode();
                    indexedChild.index = index;
                    indexedChild.jsonPath = indexedNode.jsonPath + "[" + index + "]";
                    indexedChild.jsonProperty = indexedNode.jsonProperty + "[" + index + "]";
                    indexedChild.key = "";
                    walk(indexedNode, indexedChild, childNode);
                    indexedNode.endLine = indexedChild.endLine;

                    indexedNode.children.add(indexedChild);
                    index++;
                }
                break;
            }
            case OBJECT: {
                Iterator<Entry<String, JsonNode>> childrenIterator = node.fields();
                int index = 0;
                while (childrenIterator.hasNext()) {
                    Entry<String, JsonNode> childEntry = childrenIterator.next();
                    JsonNode childNode = childEntry.getValue();
                    IndexedJsonNode indexedChild = new IndexedJsonNode();
                    indexedChild.index = index;
                    indexedChild.jsonPath = indexedNode.jsonPath + "." + childEntry.getKey();
                    indexedChild.jsonProperty = (indexedNode.jsonProperty.isEmpty() ? "" : (indexedNode.jsonProperty + ".")) + childEntry.getKey();
                    indexedChild.key = childEntry.getKey();
                    walk(indexedNode, indexedChild, childNode);

                    indexedNode.endLine = indexedChild.endLine;

                    indexedNode.children.add(indexedChild);
                    index++;
                }
                indexedNode.endLine++;
                break;
            }
            default: {
                break;
            }

        }

        return indexedNode;
    }

    private static class IndexedJsonNode {

        @Override
        public String toString() {
            return "IndexedJsonNode [key=" + key + ", index=" + index + ", jsonPath=" + jsonPath + ", jsonProperty="
                    + jsonProperty + ", startLine=" + startLine + ", endLine=" + endLine + ", children=" + children
                    + "]\n";
        }

        private String key;

        private int index;

        private String jsonPath;

        private String jsonProperty;

        private int startLine;

        private int endLine;

        private List<IndexedJsonNode> children = new ArrayList<>();
    }
}
