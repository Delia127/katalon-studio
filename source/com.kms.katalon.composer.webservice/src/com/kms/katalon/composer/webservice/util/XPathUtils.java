package com.kms.katalon.composer.webservice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XPathUtils {
    
    public static String getXmlPropertyForSoapBody(String xpath) {
        return StringUtils.replaceFirst(xpath, "Body.", "");
    }
    
    public static Map<Integer, String> evaluateXmlProperty(String jsonString) throws IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return Collections.emptyMap();
        }
        // Work around for https://github.com/FasterXML/jackson-dataformat-xml/issues/205
        ObjectMapper mapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .registerModule(new SimpleModule().addDeserializer(Object.class,
                        new FixedUntypedObjectDeserializer(null, null)));
        Object o = mapper.readValue(jsonString, Object.class);

        ObjectWriter w = new ObjectMapper().writerWithDefaultPrettyPrinter();
        JsonNode rootNode = new ObjectMapper().readTree(w.writeValueAsString(o));
        IndexedJsonNode indexedNode = new IndexedJsonNode();
        indexedNode.key = "";
        indexedNode.index = 0;
        indexedNode.xmlProperty = "";
        if (jsonString.startsWith("<?xml")) {
            indexedNode.endLine = indexedNode.startLine = 1;
        } else {
            indexedNode.endLine = indexedNode.startLine = 0;
        }

        IndexedJsonNode evaluatedRootNode = walk(null, null, indexedNode, rootNode);

        return collectXmlProperty(evaluatedRootNode);
    }

    private static Map<Integer, String> collectXmlProperty(IndexedJsonNode node) {
        Map<Integer, String> jsonPathCollection = new HashMap<>();
        jsonPathCollection.put(node.startLine, node.xmlProperty);
        for (IndexedJsonNode childNode : node.children) {
            jsonPathCollection.putAll(collectXmlProperty(childNode));
        }
        return jsonPathCollection;
    }

    private static IndexedJsonNode walk(IndexedJsonNode parentIndexedNode, JsonNode parentNode,
            IndexedJsonNode indexedNode, JsonNode node) {
        if (parentIndexedNode != null) {
            if (parentNode.getNodeType() != JsonNodeType.ARRAY) {
                indexedNode.endLine = indexedNode.startLine = parentIndexedNode.endLine + 1;
            } else {
                indexedNode.endLine = indexedNode.startLine = parentIndexedNode.endLine;
            }
        }

        switch (node.getNodeType()) {
            case ARRAY: {
                Iterator<JsonNode> childrenIterator = node.iterator();
                int index = 0;
                while (childrenIterator.hasNext()) {
                    JsonNode childNode = childrenIterator.next();
                    IndexedJsonNode indexedChild = new IndexedJsonNode();
                    indexedChild.index = index;
                    indexedChild.xmlProperty = indexedNode.xmlProperty + "[" + index + "]";
                    indexedChild.key = "";
                    walk(indexedNode, node, indexedChild, childNode);
                    indexedNode.endLine = indexedChild.endLine;

                    indexedNode.children.add(indexedChild);
                    index++;
                }
                break;
            }
            case OBJECT: {
                if (!hasAttribute(indexedNode, node)) {
                    Iterator<Entry<String, JsonNode>> childrenIterator = node.fields();
                    int index = 0;
                    while (childrenIterator.hasNext()) {
                        Entry<String, JsonNode> childEntry = childrenIterator.next();
                        JsonNode childNode = childEntry.getValue();
                        IndexedJsonNode indexedChild = new IndexedJsonNode();
                        indexedChild.index = index;
                        indexedChild.xmlProperty = (indexedNode.xmlProperty.isEmpty() ? ""
                                : indexedNode.xmlProperty + ".") + childEntry.getKey();
                        indexedChild.key = childEntry.getKey();
                        walk(indexedNode, node, indexedChild, childNode);

                        indexedNode.endLine = indexedChild.endLine;

                        indexedNode.children.add(indexedChild);
                        index++;
                    }
                }
                indexedNode.endLine++;
                break;
            }
            default: {
                indexedNode.endLine++;
                break;
            }

        }

        return indexedNode;
    }

    private static boolean hasAttribute(IndexedJsonNode indexedNode, JsonNode node) {
        Iterator<String> fieldNamesIterator = node.fieldNames();
        while (fieldNamesIterator.hasNext()) {
            if (fieldNamesIterator.next().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static class IndexedJsonNode {

        @Override
        public String toString() {
            return "IndexedJsonNode [key=" + key + ", index=" + index + ", xmlProperty=" + xmlProperty + ", startLine="
                    + startLine + ", endLine=" + endLine + ", children=" + children + "]\n";
        }

        private String key;

        private int index;

        private String xmlProperty;

        private int startLine;

        private int endLine;

        private List<IndexedJsonNode> children = new ArrayList<>();
    }

    // https://gist.github.com/joaovarandas/1543e792ed6204f0cf5fe860cb7d58ed
    public static class FixedUntypedObjectDeserializer extends UntypedObjectDeserializer {
        private static final long serialVersionUID = -5994468781168540578L;

        public FixedUntypedObjectDeserializer(JavaType listType, JavaType mapType) {
            super(listType, mapType);
        }

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
            String firstKey;

            JsonToken t = p.getCurrentToken();

            if (t == JsonToken.START_OBJECT) {
                firstKey = p.nextFieldName();
            } else if (t == JsonToken.FIELD_NAME) {
                firstKey = p.getCurrentName();
            } else {
                if (t != JsonToken.END_OBJECT) {
                    return ctxt.handleUnexpectedToken(handledType(), p);
                }
                firstKey = null;
            }

            // empty map might work; but caller may want to modify... so better
            // just give small modifiable
            LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(2);
            if (firstKey == null)
                return resultMap;

            p.nextToken();
            resultMap.put(firstKey, deserialize(p, ctxt));

            // 03-Aug-2016, jpvarandas: handle next objects and create an array
            Set<String> listKeys = new LinkedHashSet<>();

            String nextKey;
            while ((nextKey = p.nextFieldName()) != null) {
                p.nextToken();
                if (resultMap.containsKey(nextKey)) {
                    Object listObject = resultMap.get(nextKey);

                    if (!(listObject instanceof List)) {
                        listObject = new ArrayList<>();
                        ((List) listObject).add(resultMap.get(nextKey));

                        resultMap.put(nextKey, listObject);
                    }

                    ((List) listObject).add(deserialize(p, ctxt));

                    listKeys.add(nextKey);

                } else {
                    resultMap.put(nextKey, deserialize(p, ctxt));

                }
            }

            return resultMap;

        }

    }
}
