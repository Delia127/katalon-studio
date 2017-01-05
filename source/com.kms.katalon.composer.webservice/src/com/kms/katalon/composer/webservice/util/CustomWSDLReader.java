package com.kms.katalon.composer.webservice.util;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpHeaders;
import org.javalite.http.Http;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOMUtils;
import com.ibm.wsdl.util.xml.QNameUtils;
import com.ibm.wsdl.util.xml.XPathUtils;
import com.ibm.wsdl.xml.WSDLReaderImpl;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CustomWSDLReader extends WSDLReaderImpl {

    private String authorizationValue;

    @Override
    protected Import parseImport(Element importEl, Definition def, Map importedDefs) throws WSDLException {
        Import importDef = def.createImport();

        try {
            String namespaceURI = DOMUtils.getAttribute(importEl, Constants.ATTR_NAMESPACE);
            String locationURI = DOMUtils.getAttribute(importEl, Constants.ATTR_LOCATION);
            String contextURI = null;

            if (namespaceURI != null) {
                importDef.setNamespaceURI(namespaceURI);
            }

            if (locationURI != null) {
                importDef.setLocationURI(locationURI);

                if (importDocuments) {
                    try {
                        contextURI = def.getDocumentBaseURI();
                        Definition importedDef = null;
                        InputStream inputStream = null;
                        InputSource inputSource = null;
                        URL url = null;

                        if (loc != null) {
                            inputSource = loc.getImportInputSource(contextURI, locationURI);

                            /*
                             * We now have available the latest import URI. This might
                             * differ from the locationURI so check the importedDefs for it
                             * since it is this that we pass as the documentBaseURI later.
                             */
                            String liu = loc.getLatestImportURI();

                            importedDef = (Definition) importedDefs.get(liu);

                            inputSource.setSystemId(liu);
                        } else {
                            URL contextURL = (contextURI != null) ? StringUtils.getURL(null, contextURI) : null;

                            url = StringUtils.getURL(contextURL, locationURI);
                            importedDef = (Definition) importedDefs.get(url.toString());

                            if (importedDef == null) {
                                if (authorizationValue != null) {
                                    inputStream = Http.get(url.toString())
                                            .header(HttpHeaders.AUTHORIZATION, authorizationValue)
                                            .getInputStream();
                                } else {
                                    inputStream = StringUtils.getContentAsInputStream(url);
                                }

                                if (inputStream != null) {
                                    inputSource = new InputSource(inputStream);
                                    inputSource.setSystemId(url.toString());
                                }
                            }
                        }

                        if (importedDef == null) {
                            if (inputSource == null) {
                                throw new WSDLException(WSDLException.OTHER_ERROR,
                                        "Unable to locate imported document " + "at '" + locationURI + "'"
                                                + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                            }

                            Document doc = getDocument(inputSource, inputSource.getSystemId());

                            if (inputStream != null) {
                                inputStream.close();
                            }

                            Element documentElement = doc.getDocumentElement();

                            /*
                             * Check if it's a wsdl document.
                             * If it's not, don't retrieve and process it.
                             * This should later be extended to allow other types of
                             * documents to be retrieved and processed, such as schema
                             * documents (".xsd"), etc...
                             */
                            if (QNameUtils.matches(Constants.Q_ELEM_DEFINITIONS, documentElement)) {
                                if (verbose) {
                                    System.out.println("Retrieving document at '" + locationURI + "'"
                                            + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                                }

                                String urlString = (loc != null) ? loc.getLatestImportURI()
                                        : (url != null) ? url.toString() : locationURI;

                                importedDef = readWSDL(urlString, documentElement, importedDefs);
                            } else {
                                QName docElementQName = QNameUtils.newQName(documentElement);

                                if (SchemaConstants.XSD_QNAME_LIST.contains(docElementQName)) {
                                    if (verbose) {
                                        System.out.println("Retrieving schema wsdl:imported from '" + locationURI + "'"
                                                + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                                    }

                                    WSDLFactory factory = getWSDLFactory();

                                    importedDef = factory.newDefinition();

                                    if (extReg != null) {
                                        importedDef.setExtensionRegistry(extReg);
                                    }

                                    String urlString = (loc != null) ? loc.getLatestImportURI()
                                            : (url != null) ? url.toString() : locationURI;

                                    importedDef.setDocumentBaseURI(urlString);

                                    Types types = importedDef.createTypes();
                                    types.addExtensibilityElement(
                                            parseSchema(Types.class, documentElement, importedDef));
                                    importedDef.setTypes(types);
                                }
                            }
                        }

                        if (importedDef != null) {
                            importDef.setDefinition(importedDef);
                        }
                    } catch (WSDLException e) {
                        throw e;
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new WSDLException(WSDLException.OTHER_ERROR, "Unable to resolve imported document at '"
                                + locationURI + (contextURI == null ? "'." : "', relative to '" + contextURI + "'"), e);
                    }
                } // end importDocs
            } // end locationURI

        } catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(importEl));
            } else {
                // If definitions are being parsed recursively for nested imports
                // the exception location must be built up recursively too so
                // prepend this element's xpath to exception location.
                String loc = XPathUtils.getXPathExprFromNode(importEl) + e.getLocation();
                e.setLocation(loc);
            }

            throw e;
        }

        // register any NS decls with the Definition
        NamedNodeMap attrs = importEl.getAttributes();
        registerNSDeclarations(attrs, def);

        Element tempEl = DOMUtils.getFirstChildElement(importEl);

        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                importDef.setDocumentationElement(tempEl);
            } else {
                importDef.addExtensibilityElement(parseExtensibilityElement(Import.class, tempEl, def));
            }

            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }

        parseExtensibilityAttributes(importEl, Import.class, importDef, def);

        return importDef;

    }

    private static Document getDocument(InputSource inputSource, String desc) throws WSDLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setValidating(false);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputSource);

            return doc;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WSDLException(WSDLException.PARSER_ERROR, "Problem parsing '" + desc + "'.", e);
        }
    }

    private static void registerNSDeclarations(NamedNodeMap attrs, Definition def) {
        int size = attrs.getLength();

        for (int i = 0; i < size; i++) {
            Attr attr = (Attr) attrs.item(i);
            String namespaceURI = attr.getNamespaceURI();
            String localPart = attr.getLocalName();
            String value = attr.getValue();

            if (namespaceURI != null && namespaceURI.equals(Constants.NS_URI_XMLNS)) {
                if (localPart != null && !localPart.equals(Constants.ATTR_XMLNS)) {
                    DOMUtils.registerUniquePrefix(localPart, value, def);
                } else {
                    DOMUtils.registerUniquePrefix(null, value, def);
                }
            }
        }
    }

    @Override
    protected Types parseTypes(Element typesEl, Definition def) throws WSDLException {
        // register any NS decls with the Definition
        NamedNodeMap attrs = typesEl.getAttributes();
        registerNSDeclarations(attrs, def);

        Types types = def.createTypes();
        Element tempEl = DOMUtils.getFirstChildElement(typesEl);
        QName tempElType;

        while (tempEl != null) {
            tempElType = QNameUtils.newQName(tempEl);

            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                types.setDocumentationElement(tempEl);
            } else if ((SchemaConstants.XSD_QNAME_LIST).contains(tempElType)) {
                // the element qname indicates it is a schema.
                types.addExtensibilityElement(parseSchema(Types.class, tempEl, def));
            } else {
                types.addExtensibilityElement(parseExtensibilityElement(Types.class, tempEl, def));
            }

            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }

        parseExtensibilityAttributes(typesEl, Types.class, types, def);

        return types;
    }

    @Override
    protected ExtensibilityElement parseSchema(Class parentType, Element el, Definition def) throws WSDLException {
        QName elementType = null;
        ExtensionRegistry extReg = null;

        try {
            extReg = def.getExtensionRegistry();
            if (extReg == null) {
                throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
                        "No ExtensionRegistry set for this " + "Definition, so unable to deserialize " + "a '"
                                + elementType + "' element in the " + "context of a '" + parentType.getName() + "'.");
            }

            return parseSchema(parentType, el, def, extReg);
        } catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            }
            throw e;
        }
    }

    @Override
    protected ExtensibilityElement parseSchema(Class parentType, Element el, Definition def, ExtensionRegistry extReg)
            throws WSDLException {
        /*
         * This method returns ExtensibilityElement rather than Schema because we
         * do not insist that a suitable XSD schema deserializer is registered.
         * PopulatedExtensionRegistry registers SchemaDeserializer by default, but
         * if the user chooses not to register a suitable deserializer then the
         * UnknownDeserializer will be used, returning an UnknownExtensibilityElement.
         */

        Schema schema = null;
        SchemaReference schemaRef = null;
        try {

            QName elementType = QNameUtils.newQName(el);

            ExtensionDeserializer exDS = extReg.queryDeserializer(parentType, elementType);

            // Now unmarshall the DOM element.
            ExtensibilityElement ee = exDS.unmarshall(parentType, elementType, el, def, extReg);

            if (ee instanceof Schema) {
                schema = (Schema) ee;
            } else {
                // Unknown extensibility element, so don't do any more schema parsing on it.
                return ee;
            }

            // Keep track of parsed schemas to avoid duplicating Schema objects
            // through duplicate or circular references (eg: A imports B imports A).
            if (schema.getDocumentBaseURI() != null) {
                this.allSchemas.put(schema.getDocumentBaseURI(), schema);
            }

            // At this point, any SchemaReference objects held by the schema will not
            // yet point to their referenced schemas, so we must now retrieve these
            // schemas and set the schema references.

            // First, combine the schema references for imports, includes and redefines
            // into a single list

            ArrayList allSchemaRefs = new ArrayList();

            Collection ic = schema.getImports().values();
            Iterator importsIterator = ic.iterator();
            while (importsIterator.hasNext()) {
                allSchemaRefs.addAll((Collection) importsIterator.next());
            }

            allSchemaRefs.addAll(schema.getIncludes());
            allSchemaRefs.addAll(schema.getRedefines());

            // Then, retrieve the schema referred to by each schema reference. If the
            // schema has been read in previously, use the existing schema object.
            // Otherwise unmarshall the DOM element into a new schema object.

            ListIterator schemaRefIterator = allSchemaRefs.listIterator();

            while (schemaRefIterator.hasNext()) {
                try {
                    schemaRef = (SchemaReference) schemaRefIterator.next();

                    if (schemaRef.getSchemaLocationURI() == null) {
                        // cannot get the referenced schema, so ignore this schema reference
                        continue;
                    }

                    if (verbose) {
                        System.out.println("Retrieving schema at '" + schemaRef.getSchemaLocationURI()
                                + (schema.getDocumentBaseURI() == null ? "'."
                                        : "', relative to '" + schema.getDocumentBaseURI() + "'."));
                    }

                    InputStream inputStream = null;
                    InputSource inputSource = null;

                    // This is the child schema referred to by the schemaReference
                    Schema referencedSchema = null;

                    // This is the child schema's location obtained from the WSDLLocator or the URL
                    String location = null;

                    if (loc != null) {
                        // Try to get the referenced schema using the wsdl locator
                        inputSource = loc.getImportInputSource(schema.getDocumentBaseURI(),
                                schemaRef.getSchemaLocationURI());

                        if (inputSource == null) {
                            throw new WSDLException(WSDLException.OTHER_ERROR,
                                    "Unable to locate with a locator " + "the schema referenced at '"
                                            + schemaRef.getSchemaLocationURI() + "' relative to document base '"
                                            + schema.getDocumentBaseURI() + "'");
                        }
                        location = loc.getLatestImportURI();

                        // if a schema from this location has been read previously, use it.
                        referencedSchema = (Schema) this.allSchemas.get(location);
                    } else {
                        // We don't have a wsdl locator, so try to retrieve the schema by its URL
                        String contextURI = schema.getDocumentBaseURI();
                        URL contextURL = (contextURI != null) ? StringUtils.getURL(null, contextURI) : null;
                        URL url = StringUtils.getURL(contextURL, schemaRef.getSchemaLocationURI());
                        location = url.toExternalForm();

                        // if a schema from this location has been retrieved previously, use it.
                        referencedSchema = (Schema) this.allSchemas.get(location);

                        if (referencedSchema == null) {
                            // We haven't read this schema in before so do it now
                            if (authorizationValue != null) {
                                inputStream = Http.get(url.toString())
                                        .header(HttpHeaders.AUTHORIZATION, authorizationValue)
                                        .getInputStream();
                            } else {
                                inputStream = StringUtils.getContentAsInputStream(url);
                            }

                            if (inputStream != null) {
                                inputSource = new InputSource(inputStream);
                            }

                            if (inputSource == null) {
                                throw new WSDLException(WSDLException.OTHER_ERROR,
                                        "Unable to locate with a url " + "the document referenced at '"
                                                + schemaRef.getSchemaLocationURI() + "'"
                                                + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                            }
                        }

                    } // end if loc

                    // If we have not previously read the schema, get its DOM element now.
                    if (referencedSchema == null) {
                        inputSource.setSystemId(location);
                        Document doc = getDocument(inputSource, location);

                        if (inputStream != null) {
                            inputStream.close();
                        }

                        Element documentElement = doc.getDocumentElement();

                        // If it's a schema doc process it, otherwise the schema reference remains null

                        QName docElementQName = QNameUtils.newQName(documentElement);

                        if (SchemaConstants.XSD_QNAME_LIST.contains(docElementQName)) {
                            // We now need to call parseSchema recursively to parse the referenced
                            // schema. The document base URI of the referenced schema will be set to
                            // the document base URI of the current schema plus the schemaLocation in
                            // the schemaRef. We cannot explicitly pass in a new document base URI
                            // to the schema deserializer, so instead we will create a dummy
                            // Definition and set its documentBaseURI to the new document base URI.
                            // We can leave the other definition fields empty because we know
                            // that the SchemaDeserializer.unmarshall method uses the definition
                            // parameter only to get its documentBaseURI. If the unmarshall method
                            // implementation changes (ie: its use of definition changes) we may need
                            // to rethink this approach.

                            WSDLFactory factory = getWSDLFactory();
                            Definition dummyDef = factory.newDefinition();

                            dummyDef.setDocumentBaseURI(location);

                            // By this point, we know we have a SchemaDeserializer registered
                            // so we can safely cast the ExtensibilityElement to a Schema.
                            referencedSchema = (Schema) parseSchema(parentType, documentElement, dummyDef, extReg);
                        }

                    } // end if referencedSchema

                    schemaRef.setReferencedSchema(referencedSchema);
                } catch (WSDLException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new WSDLException(WSDLException.OTHER_ERROR,
                            "An error occurred trying to resolve schema referenced at '"
                                    + schemaRef.getSchemaLocationURI() + "'" + (schema.getDocumentBaseURI() == null
                                            ? "." : ", relative to '" + schema.getDocumentBaseURI() + "'."),
                            e);
                }

            } // end while loop

            return schema;

        } catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            } else {
                // If this method has been called recursively for nested schemas
                // the exception location must be built up recursively too so
                // prepend this element's xpath to exception location.
                String loc = XPathUtils.getXPathExprFromNode(el) + e.getLocation();
                e.setLocation(loc);
            }

            throw e;
        }

    }

    public void setAuthorizationValue(String authorizationValue) {
        this.authorizationValue = authorizationValue;
    }

}
