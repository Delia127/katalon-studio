package com.kms.katalon.composer.webservice.wadl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.importer.APIImporter;
import com.kms.katalon.composer.webservice.importing.model.RestMethodImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestParameterImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestResourceImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestServiceImportResult;
import com.kms.katalon.core.webservice.definition.DefinitionLoader;
import com.kms.katalon.core.webservice.definition.DefinitionLoaderProvider;
import com.kms.katalon.entity.folder.FolderEntity;

import net.java.dev.wadl.x2009.x02.ApplicationDocument;
import net.java.dev.wadl.x2009.x02.ApplicationDocument.Application;
import net.java.dev.wadl.x2009.x02.DocDocument.Doc;
import net.java.dev.wadl.x2009.x02.MethodDocument.Method;
import net.java.dev.wadl.x2009.x02.ParamDocument.Param;
import net.java.dev.wadl.x2009.x02.ParamStyle;
import net.java.dev.wadl.x2009.x02.ResourceDocument.Resource;
import net.java.dev.wadl.x2009.x02.ResourceTypeDocument;
import net.java.dev.wadl.x2009.x02.ResourcesDocument.Resources;

public class WadlImporter extends APIImporter {

    private static final String WADL11_NS = "http://wadl.dev.java.net/2009/02";

    private static final Pattern PROPERTY_EXPANSION_CONTAINS_PATTERN = Pattern
            .compile("(\\$\\{(.*?)\\})|(%24%7B.*?%7D)|(%2524%257B.*?%257D)|(%252524%25257B.*?%25257D)");

    private Application application;

    private List<Resources> resourcesList;

    private Map<String, ApplicationDocument> refCache = new HashMap<String, ApplicationDocument>();

    public RestServiceImportResult importService(String wadlUrl, FolderEntity rootFolder) throws Exception {
        DefinitionLoader wadlLoader = getWadlLoader(wadlUrl);
        XmlObject xmlObject = XmlObject.Factory.parse(wadlLoader.load());

        String content = removePropertyExpansions(xmlObject.xmlText());

        Element element = ((Document) xmlObject.getDomNode()).getDocumentElement();

        if (element.getLocalName().equals("application")
                && element.getNamespaceURI().startsWith("http://research.sun.com/wadl")) {
            content = content.replaceAll("\"" + element.getNamespaceURI() + "\"", "\"" + WADL11_NS + "\"");
        } else if (!element.getLocalName().equals("application") || !element.getNamespaceURI().equals(WADL11_NS)) {
            throw new RuntimeException("Document is not a WADL application with " + WADL11_NS + " namespace");
        }

        ApplicationDocument applicationDocument = ApplicationDocument.Factory.parse(content);
        application = applicationDocument.getApplication();

        resourcesList = Arrays.asList(application.getResourcesArray());

        String serviceName = getFirstTitle(application.getDocArray(), "Imported from WADL");
        FolderEntity importFolder = getRootImportFolder(serviceName, rootFolder);
        RestServiceImportResult serviceImportResult = new RestServiceImportResult(null, importFolder);

        String base = resourcesList.size() == 1 ? resourcesList.get(0).getBase() : "";

        try {
            URL baseURL = new URL(base);
            serviceImportResult.setBasePath(baseURL.getPath());

            serviceImportResult.setEndpoint(getEndpointFromUrl(baseURL));
        } catch (Exception e) {
            serviceImportResult.setBasePath(base);
        }

        for (Resources resources : resourcesList) {
            RestResourceImportResult baseResourceImportResult = null;
            if (resourcesList.size() > 1) {
                String path = resources.getBase();
                String baseResourceFolderName = getResourceFolderName(serviceImportResult, path);
                baseResourceImportResult = serviceImportResult.newResource(baseResourceFolderName, path);
            }
            for (Resource resource : resources.getResourceArray()) {
                String title = getFirstTitle(resource.getDocArray(), resource.getPath());
                String path = resource.getPath();

                RestResourceImportResult newResourceImportResult = null;

                if (baseResourceImportResult != null && path != null) {
                    String resourceName = getResourceFolderName(baseResourceImportResult, title);
                    for (RestResourceImportResult res : baseResourceImportResult.getChildResourceImportResults()) {
                        if (path.equals(res.getPath())) {
                            newResourceImportResult = res;
                            break;
                        }
                    }

                    if (newResourceImportResult == null) {
                        newResourceImportResult = baseResourceImportResult.newResource(resourceName, path);
                    }
                } else if (path != null) {
                    for (RestResourceImportResult res : serviceImportResult.getResourceImportResults()) {
                        if (path.equals(res.getPath())) {
                            newResourceImportResult = res;
                            break;
                        }
                    }

                    if (newResourceImportResult == null) {
                        String resourceName = getResourceFolderName(serviceImportResult, title);
                        newResourceImportResult = serviceImportResult.newResource(resourceName, path);
                    }
                } else {
                    String resourceName = getResourceFolderName(serviceImportResult, title);
                    newResourceImportResult = serviceImportResult.newResource(resourceName, "");
                }

                initResourceFromWadlResource(newResourceImportResult, resource);
                addSubResources(newResourceImportResult, resource);
            }
        }

        return serviceImportResult;
    }
    
    private DefinitionLoader getWadlLoader(String wadlLocation) {
        DefinitionLoader loader = DefinitionLoaderProvider.getLoader(wadlLocation);
        return loader;
    }
    
    private String getResourceFolderName(RestServiceImportResult serviceImportResult, String suggestion) {
        if (StringUtils.isBlank(suggestion)) {
            suggestion = "Resource";
        }
        suggestion = toValidFileName(suggestion);
        int numberSuffix = 0;
        while (!serviceImportResult.isResourceFolderNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }
    
    private String getResourceFolderName(RestResourceImportResult resourceImportResult, String suggestion) {
        if (StringUtils.isBlank(suggestion)) {
            suggestion = "Resource";
        }
        suggestion = toValidFileName(suggestion);
        int numberSuffix = 0;
        while (!resourceImportResult.isChildResultFolderNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }
    

    private void addSubResources(RestResourceImportResult newRestResourceImportResult, Resource resource) {
        for (Resource res : resource.getResourceArray()) {
            String path = res.getPath();
            if (path == null) {
                path = "";
            }

            String title = getFirstTitle(res.getDocArray(), path);
            String name = getResourceFolderName(newRestResourceImportResult, title);

            RestResourceImportResult newChildResourceImportResult = null;

            for (RestResourceImportResult child : newRestResourceImportResult.getChildResourceImportResults()) {
                if (path.equals(child.getPath())) {
                    newRestResourceImportResult = child;
                    break;
                }
            }

            if (newChildResourceImportResult == null) {
                newChildResourceImportResult = newRestResourceImportResult.newResource(name, path);
            }

            initResourceFromWadlResource(newChildResourceImportResult, res);

            addSubResources(newChildResourceImportResult, res);
        }
    }

    private String getFirstTitle(Doc[] docs, String defaultTitle) {
        for (Doc doc : docs) {
            if (StringUtils.isNotBlank(doc.getTitle())) {
                return doc.getTitle();
            }
        }
        return defaultTitle;
    }

    private String removePropertyExpansions(String definition) {
        Matcher matcher = PROPERTY_EXPANSION_CONTAINS_PATTERN.matcher(definition);
        return matcher.replaceAll("");
    }

    public String getEndpointFromUrl(URL baseUrl) {
        StringBuilder result = new StringBuilder();
        result.append(baseUrl.getProtocol()).append("://");
        result.append(baseUrl.getHost());
        if (baseUrl.getPort() > 0) {
            result.append(':').append(baseUrl.getPort());
        }

        return result.toString();
    }

    private void initResourceFromWadlResource(RestResourceImportResult resourceImportResult, Resource resource) {
        for (Param param : resource.getParamArray()) {
            param = resolveParameter(param);
            if (param != null) {
                String nm = param.getName();
                RestParameterImportResult parameterImportResult = resourceImportResult.hasParameter(nm)
                        ? resourceImportResult.getParameter(nm) : resourceImportResult.addNewParameter(nm);

                initParam(param, parameterImportResult);
            }
        }

        for (Method method : resource.getMethodArray()) {
            method = resolveMethod(method);
            initMethod(resourceImportResult, method);
        }

        List<?> types = resource.getType();
        if (types != null && types.size() > 0) {
            for (Object obj : types) {
                ResourceTypeDocument.ResourceType type = resolveResource(obj.toString());
                if (type != null) {
                    for (Method method : type.getMethodArray()) {
                        method = resolveMethod(method);
                        RestMethodImportResult methodImportResult = initMethod(resourceImportResult, method);

                        for (Param param : type.getParamArray()) {
                            param = resolveParameter(param);
                            if (param != null) {
                                String nm = param.getName();
                                RestParameterImportResult parameterImportResult = methodImportResult.hasParameter(nm)
                                        ? methodImportResult.getParameter(nm) : methodImportResult.addNewParameter(nm);

                                initParam(param, parameterImportResult);
                            }
                        }
                    }
                }
            }
        }
    }

    private void initParam(Param param, RestParameterImportResult parameterImportResult) {
        parameterImportResult.setValue(param.getDefault());
        ParamStyle.Enum paramStyle = param.getStyle();
        if (paramStyle == null) {
            paramStyle = ParamStyle.QUERY;
        }

        parameterImportResult.setStyle(RestParameterImportResult.Style.valueOf(paramStyle.toString().toUpperCase()));
    }

    private Method resolveMethod(Method method) {
        String href = method.getHref();
        if (StringUtils.isBlank(href)) {
            return method;
        }

        for (Method m : application.getMethodArray()) {
            if (m.getId().equals(href.substring(1))) {
                return m;
            }
        }

        try {
            ApplicationDocument applicationDocument = loadReferencedWadl(href);
            if (applicationDocument != null) {
                int ix = href.lastIndexOf('#');
                if (ix > 0) {
                    href = href.substring(ix + 1);
                }

                for (Method m : application.getMethodArray()) {
                    if (m.getId().equals(href)) {
                        return m;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return method;
    }

    private ApplicationDocument loadReferencedWadl(String id) throws URISyntaxException, XmlException, IOException {
        int ix = id.indexOf('#');
        if (ix != -1) {
            id = id.substring(0, ix);
        }
        ApplicationDocument applicationDocument = refCache.get(id);

        if (applicationDocument == null) {
            URI uri = new URI(id);
            applicationDocument = ApplicationDocument.Factory.parse(uri.toURL());
            refCache.put(id, applicationDocument);
        }

        return applicationDocument;
    }

    private RestMethodImportResult initMethod(RestResourceImportResult resourceImportResult, Method method) {
        String name = getFirstTitle(method.getDocArray(), method.getName());
        name = getMethodFolderName(resourceImportResult, name);
        RestMethodImportResult methodImportResult = resourceImportResult.newMethod(name, method.getName());
        if (method.getRequest() != null) {
            for (Param param : method.getRequest().getParamArray()) {
                param = resolveParameter(param);
                if (param != null) {
                    RestParameterImportResult parameterImportResult = methodImportResult.addNewParameter(param.getName());
                    initParam(param, parameterImportResult);
                }
            }
        }

        WadlRestRequestImportResult request = methodImportResult.newRequest("Request 1", () -> new WadlRestRequestImportResult(methodImportResult));
        String httpMethod = StringUtils.defaultIfBlank(method.getName(), "GET");
        request.setHttpMethod(httpMethod);
        
        return methodImportResult;
    }

    private String getMethodFolderName(RestResourceImportResult resourceImportResult, String suggestion) {
        if (StringUtils.isBlank(suggestion)) {
            suggestion = "Method";
        }
        suggestion = toValidFileName(suggestion);
        int numberSuffix = 0;
        while (!resourceImportResult.isChildResultFolderNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }

    private ResourceTypeDocument.ResourceType resolveResource(String id) {
        for (ResourceTypeDocument.ResourceType resourceType : Arrays.asList(application.getResourceTypeArray())) {
            if (resourceType.getId().equals(id)) {
                return resourceType;
            }
        }

        try {
            ApplicationDocument applicationDocument = loadReferencedWadl(id);
            if (applicationDocument != null) {
                int ix = id.lastIndexOf('#');
                if (ix > 0) {
                    id = id.substring(ix + 1);
                }

                for (ResourceTypeDocument.ResourceType resourceType : applicationDocument.getApplication()
                        .getResourceTypeArray()) {
                    if (resourceType.getId().equals(id)) {
                        return resourceType;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return null;
    }

    private Param resolveParameter(Param param) {
        String href = param.getHref();
        if (!StringUtils.isNotBlank(href)) {
            return param;
        }

        try {
            Application app = application;

            if (!href.startsWith("#")) {
                ApplicationDocument applicationDocument = loadReferencedWadl(href);
                app = applicationDocument.getApplication();
            }

            if (app != null) {
                int ix = href.lastIndexOf('#');
                if (ix >= 0) {
                    href = href.substring(ix + 1);
                }

                for (Param p : application.getParamArray()) {
                    if (p.getId().equals(href)) {
                        return p;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return null;
    }
}
