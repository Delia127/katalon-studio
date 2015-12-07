package com.kms.katalon.core.testobject;

import java.io.StringReader;
import java.io.StringWriter;
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
	
	private Map<String, List<String>> headerFields;
	
	public ResponseObject(){}
	
	public ResponseObject(String responseText){
		this.responseText = responseText;
	}
	
	//TODO: Detect the source to see if it is JSON, XML, HTML or plain text
	public String getResponseBodyContent() throws Exception{
		if(responseText != null){
			if(contentType != null && contentType.startsWith("application/xml")){
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(responseText)));
				XPath xPath = XPathFactory.newInstance().newXPath();
				Node node = (Node) xPath.evaluate("//*//*//*", doc, XPathConstants.NODE);
				return nodeToString(node);
			}
			else if(contentType != null && contentType.startsWith("application/json")){
				return responseText;
			}
			//plain text/html
			else{
				return responseText;
			}
		}
		return "";
	}
	
	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String ct) {
		this.contentType = ct;;
	}

	private String nodeToString(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		Transformer xform = TransformerFactory.newInstance().newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		xform.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
	
	public boolean isJsonContentType(){
		return contentType != null && contentType.toLowerCase().startsWith("application/json");
	}
	
	public boolean isXmlContentType(){
	    String contentTypeString = contentType.toLowerCase();
		return contentType != null && (
		        contentTypeString.startsWith("application/xml")
		        || contentTypeString.equals("application/soap+xml") 
		        || contentTypeString.equals("text/xml") 
		        );
	}
	
	public boolean isTextContentType(){
		return !isJsonContentType() && !isXmlContentType();
	}
	
	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, List<String>> headerFields) {
		this.headerFields = headerFields;
	}
}
