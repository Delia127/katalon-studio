package com.kms.katalon.core.testobject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class ResponseObject {

    private String contentType = "text";

    private String responseText;

    private int statusCode;

    private Map<String, List<String>> headerFields;

    public ResponseObject() {
    }

    public ResponseObject(String responseText) {
        this.responseText = responseText;
    }

    /**
     * Get the response body content as a String
     * 
     * @return the response body content as a String
     * @throws Exception if errors happened
     */
    // TODO: Detect the source to see if it is JSON, XML, HTML or plain text
    public String getResponseBodyContent() throws Exception {
        if (responseText != null) {
            if (contentType != null && contentType.startsWith("application/xml")) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(responseText)));
                XPath xPath = XPathFactory.newInstance().newXPath();
                Node node = (Node) xPath.evaluate("//*//*//*", doc, XPathConstants.NODE);
                return nodeToString(node);
            } else if (contentType != null && contentType.startsWith("application/json")) {
                return responseText;
            }
            // plain text/html
            else {
                return responseText;
            }
        }
        return "";
    }

    /**
     * Get the raw response text
     * 
     * @return the raw response text
     */
    public String getResponseText() {
        return responseText;
    }

    /**
     * Set the raw response text
     * 
     * @param responseText the new raw response text
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    /**
     * Get the content type
     * 
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the content type
     * 
     * @param contentType the new content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String nodeToString(Node node) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer xform = TransformerFactory.newInstance().newTransformer();
        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xform.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Check if the content type of this response is json
     * 
     * @return true if the content type of this response is json, otherwise false
     */
    public boolean isJsonContentType() {
        return contentType != null && contentType.toLowerCase().startsWith("application/json");
    }

    /**
     * Check if the content type of this response is xml
     * 
     * @return true if the content type of this response is xml, otherwise false
     */
    public boolean isXmlContentType() {
        String contentTypeString = contentType.toLowerCase();
        return contentType != null && (contentTypeString.startsWith("application/xml")
                || contentTypeString.equals("application/soap+xml") || contentTypeString.equals("text/xml"));
    }

    /**
     * Check if the content type of this response is raw text
     * 
     * @return true if the content type of this response is raw text, otherwise false
     */
    public boolean isTextContentType() {
        return !isJsonContentType() && !isXmlContentType();
    }

    /**
     * Get the header fields as a {@link Map}
     * 
     * @return the header fields as a {@link Map}
     */
    public Map<String, List<String>> getHeaderFields() {
        if (headerFields == null) {
            headerFields = Collections.emptyMap();
        }
        return headerFields;
    }

    /**
     * Set the header fields
     * 
     * @param headerFields the new header fields as a {@link Map}
     */
    public void setHeaderFields(Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }

    /**
     * Get the status code
     * 
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set the status code
     * 
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
