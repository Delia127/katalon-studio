package com.kms.katalon.composer.webservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMHelper {
    public static NodeList getChildNodes(Node node) {
        if (node == null) {
            return null;
        }

        if (node.hasChildNodes()) {
            return node.getChildNodes();
        }

        return null;
    }

    public static List<Node> getNodeList(Node node) {
        List<Node> nodes = new ArrayList<>();
        NodeList childNodes = getChildNodes(node);
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                nodes.add(childNodes.item(i));
            }
        }
        return nodes;
    }

    public static Map<String, String> getAttributes(Node node) {
        Map<String, String> attributes = new HashMap<>();
        if (node == null || !node.hasAttributes()) {
            return attributes;
        }

        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            List<Node> attrNodes = getNodeList(attrs.item(i));
            for (Node attr : attrNodes) {
                attributes.put(attr.getNodeName(), attr.getNodeValue());
            }
        }

        return attributes;
    }
}
