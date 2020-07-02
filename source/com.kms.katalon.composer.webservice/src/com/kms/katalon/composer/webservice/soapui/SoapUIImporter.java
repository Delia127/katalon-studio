package com.kms.katalon.composer.webservice.soapui;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.eviware.soapui.config.Attachment;
import com.eviware.soapui.config.Credentials;
import com.eviware.soapui.config.Interface;
import com.eviware.soapui.config.OAuth1ProfileContainer;
import com.eviware.soapui.config.OAuth2ProfileContainer;
import com.eviware.soapui.config.Project;
import com.eviware.soapui.config.RestMethod;
import com.eviware.soapui.config.RestParameter;
import com.eviware.soapui.config.RestRequest;
import com.eviware.soapui.config.RestResource;
import com.eviware.soapui.config.RestService;
import com.eviware.soapui.config.Setting;
import com.eviware.soapui.config.SoapuiProjectDocument;
import com.eviware.soapui.config.StringToStringMap.Entry;
import com.google.common.base.Preconditions;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIImporter {

    public SoapUIProjectImportResult importServices(String projectFilePath, FolderEntity rootFolder) throws Exception {
        Preconditions.checkNotNull(projectFilePath, "SoapUI project file path must not be null or empty.");
        File projectFile = new File(projectFilePath);
        Preconditions.checkArgument(projectFile.exists(), "SoapUI project file does not exist.");
        Preconditions.checkNotNull(rootFolder, "Root folder must not be null.");

        SoapuiProjectDocument projectDocument = SoapuiProjectDocument.Factory.parse(projectFile);
        Project project = projectDocument.getSoapuiProject();
        SoapUIProjectImportResult projectImportResult = parseProject(project, rootFolder);
        Interface[] interfaces = project.getInterfaceArray();
        for (Interface iface : interfaces) {
            if (iface instanceof RestService) {
                RestService restService = (RestService) iface;
                SoapUIRestServiceImportResult serviceImportResult = parseService(projectImportResult, restService);
                RestResource[] restResources = restService.getResourceArray();
                for (RestResource restResource : restResources) {
                    parseResource(serviceImportResult, restResource);
                }
            }
        }
        return projectImportResult;
    }

    private SoapUIProjectImportResult parseProject(Project project, FolderEntity parentFolder) throws Exception {
        FolderEntity projectImportFolder = getProjectImportFolder(project, parentFolder);
        SoapUIProjectImportResult projectResult = new SoapUIProjectImportResult(projectImportFolder);
        addOAuth1Credentials(projectResult, project);
        addOAuth2Credentials(projectResult, project);
        return projectResult;
    } 
  
    private void addOAuth1Credentials(SoapUIProjectImportResult projectImportResult,
            Project project) {
        OAuth1ProfileContainer oAuth1ProfileContainer = project.getOAuth1ProfileContainer();
        if (oAuth1ProfileContainer == null) {
            return;
        }
        Stream.of(oAuth1ProfileContainer.getOAuth1ProfileArray()).forEach(p -> {
            SoapUIOAuth1Credential credential = projectImportResult.newOAuth1Credential(p.getName());
            credential.setConsumerKey(p.getConsumerKey());
            credential.setConsumerSecret(p.getConsumerSecret());
            credential.setToken(p.getAccessToken());
            credential.setTokenSecret(p.getTokenSecret());
        });
    }
    
    private void addOAuth2Credentials(SoapUIProjectImportResult projectImportResult,
            Project project) {
        OAuth2ProfileContainer oAuth2ProfileContainer = project.getOAuth2ProfileContainer();
        if (oAuth2ProfileContainer == null) {
            return;
        }
        Stream.of(oAuth2ProfileContainer.getOAuth2ProfileArray()).forEach(p -> {
            SoapUIOAuth2Credential credential = projectImportResult.newOAuth2Credential(p.getName());
            credential.setAccessToken(p.getAccessToken());
        });
    }

    private FolderEntity getProjectImportFolder(Project project, FolderEntity parentFolder) throws Exception {
        String name = project.getName();
        if (StringUtils.isBlank(name)) {
            name = "Imported from SoapUI";
        }
        name = toValidFileName(name);
        name = EntityNameController.getInstance().getAvailableName(name, parentFolder, true);
        FolderEntity folder = new FolderEntity();
        folder.setName(name);
        folder.setParentFolder(parentFolder);
        folder.setProject(parentFolder.getProject());
        folder.setFolderType(parentFolder.getFolderType());
        folder.setDescription("folder");
        return folder;
    }

    private SoapUIRestServiceImportResult parseService(SoapUIProjectImportResult projectImportResult,
            RestService restService) {
        String name = getServiceFolderName(projectImportResult, restService);
        SoapUIRestServiceImportResult serviceImportResult = projectImportResult.newService(name);
        serviceImportResult.setBasePath(restService.getBasePath());
        return serviceImportResult;
    }

    private String getServiceFolderName(SoapUIProjectImportResult projectImportResult, RestService restService) {
        String suggestion = restService.getName();
        if (StringUtils.isBlank(suggestion)) {
            suggestion = "Service";
        }
        suggestion = toValidFileName(suggestion);
        int numberSuffix = 0;
        while (!projectImportResult.isServiceFolderNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }

    private SoapUIRestResourceImportResult parseResource(SoapUIRestServiceImportResult serviceImportResult,
            RestResource restResource) {
        String resourcePath = restResource.getPath();
        String resourceName = getResourceFolderName(serviceImportResult, restResource);
        SoapUIRestResourceImportResult resourceResult = serviceImportResult.newResource(resourceName, resourcePath);
        addParameters(resourceResult, restResource.getParameters().getParameterArray());
        for (RestMethod restMethod : restResource.getMethodArray()) {
            parseMethod(resourceResult, restMethod);
        }
        for (RestResource childResource : restResource.getResourceArray()) {
            parseResource(resourceResult, childResource);
        }
        return resourceResult;
    }

    private SoapUIRestResourceImportResult parseResource(SoapUIRestResourceImportResult parentResourceImportResult,
            RestResource restResource) {
        String resourcePath = restResource.getPath();
        String resourceName = getResourceFolderName(parentResourceImportResult, restResource);
        SoapUIRestResourceImportResult resourceResult = parentResourceImportResult.newResource(resourceName,
                resourcePath);
        addParameters(resourceResult, restResource.getParameters().getParameterArray());
        for (RestMethod restMethod : restResource.getMethodArray()) {
            parseMethod(resourceResult, restMethod);
        }
        for (RestResource childResource : restResource.getResourceArray()) {
            parseResource(resourceResult, childResource);
        }

        return resourceResult;
    }

    private String getResourceFolderName(SoapUIRestServiceImportResult serviceImportResult, RestResource restResource) {
        String suggestion = restResource.getName();
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

    private String getResourceFolderName(SoapUIRestResourceImportResult resourceImportResult,
            RestResource restResource) {
        String suggestion = restResource.getName();
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

    private SoapUIRestMethodImportResult parseMethod(SoapUIRestResourceImportResult resourceImportResult,
            RestMethod restMethod) {
        String name = getMethodFolderName(resourceImportResult, restMethod);
        String httpMethod = restMethod.getMethod();
        SoapUIRestMethodImportResult methodResult = resourceImportResult.newMethod(name, httpMethod);
        addParameters(methodResult, restMethod.getParameters().getParameterArray());
        for (RestRequest restRequest : restMethod.getRequestArray()) {
            parseRequest(methodResult, restRequest);
        }
        return methodResult;
    }

    private String getMethodFolderName(SoapUIRestResourceImportResult resourceImportResult, RestMethod restMethod) {
        String suggestion = restMethod.getName();
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

    private SoapUIRestRequestImportResult parseRequest(SoapUIRestMethodImportResult methodImportResult,
            RestRequest restRequest) {
        String name = getRequestFileName(methodImportResult, restRequest);
        SoapUIRestRequestImportResult requestResult = methodImportResult.newRequest(name);
        requestResult.setEndpoint(restRequest.getEndpoint());
        requestResult.setMediaType(restRequest.getMediaType());
        requestResult.setPostQueryString(restRequest.getPostQueryString());
        if (restRequest.getRequest() != null) {
            requestResult.setRequestBodyContent(restRequest.getRequest().getStringValue());
        }
        requestResult.setEncoding(restRequest.getEncoding());
        setParameters(requestResult, restRequest.getParameters().getEntryArray());
        if (restRequest.getParameterOrder() != null) {
            requestResult.setParameterOrder(restRequest.getParameterOrder().getEntryArray());
        }
        addHeaders(requestResult, restRequest);
        addAttachments(requestResult, restRequest);
        addCredential(requestResult, restRequest);
        requestResult.setHttpMethod(methodImportResult.getHttpMethod());
        return requestResult;
    }

    private void setParameters(SoapUIRestRequestImportResult requestResult, Entry[] parameterEntries) {
        for (Entry parameterEntry : parameterEntries) {
            requestResult.setParameter(parameterEntry.getKey(), parameterEntry.getValue());
        }
    }

    private void addAttachments(SoapUIRestRequestImportResult requestResult, RestRequest restRequest) {
        for (Attachment attachment : restRequest.getAttachmentArray()) {
            requestResult.newAttachment(attachment.getName(), attachment.getContentType(), attachment.getContentId(),
                    attachment.getUrl());
        }
    }

    private void addHeaders(SoapUIRestRequestImportResult requestImportResult, RestRequest restRequest) {
        for (Setting setting : restRequest.getSettings().getSettingArray()) {
            if ("com.eviware.soapui.impl.wsdl.WsdlRequest@request-headers".equals(setting.getId())) {
                String xml = StringEscapeUtils.unescapeXml(setting.getStringValue());
                SAXReader reader = new SAXReader();

                try {
                    Document document = reader.read(IOUtils.toInputStream(xml));
                    Element rootElement = document.getRootElement();
                    List<?> headerObjects = rootElement.elements("entry");
                    for (Object headerObject : headerObjects) {
                        Element headerElement = (Element) headerObject;
                        String headerName = headerElement.attributeValue("key");
                        String headerValue = headerElement.attributeValue("value");
                        requestImportResult.setHeader(headerName, headerValue);
                    }
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    private void addCredential(SoapUIRestRequestImportResult requestImportResult, RestRequest restRequest) {
        Credentials credentials = restRequest.getCredentials();
        if (credentials != null && StringUtils.isNotBlank(credentials.getSelectedAuthProfile())) {
            String authProfile = credentials.getSelectedAuthProfile();
            if ("Basic".equals(authProfile)) {
                requestImportResult.setBasicCredential(credentials.getUsername(), credentials.getPassword());
                return;
            }
            
            if (credentials.getAuthType().toString().equals("OAuth 1.0")) {
                requestImportResult.setOAuth1Profile(authProfile);
                return;
            }
            
            if (credentials.getAuthType().toString().equals("OAuth 2.0")) {
                requestImportResult.setOAuth2Profile(authProfile);
                return;
            }
        }
    }

    private String getRequestFileName(SoapUIRestMethodImportResult methodImportResult, RestRequest restRequest) {
        String suggestion = restRequest.getName();
        if (StringUtils.isBlank(suggestion)) {
            suggestion = "Request";
        }
        suggestion = toValidFileName(suggestion);
        int numberSuffix = 0;
        while (!methodImportResult.isRequestFileNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }

    private String toValidFileName(String fileName) {
        return fileName.replaceAll("[\\W&&\\S]+", "_");
    }

    private void addParameters(SoapUIRestResourceImportNode holder, RestParameter[] restParameters) {
        final int INT_MATRIX = 1;
        final int INT_HEADER = 2;
        final int INT_QUERY = 3;
        final int INT_TEMPLATE = 4;

        for (RestParameter parameter : restParameters) {
            int parameterStyleCode = parameter.getStyle().intValue();
            String name = parameter.getName();
            String value = parameter.getValue();
            if (parameterStyleCode == INT_MATRIX) {
                holder.addParameter(name, value, SoapUIRestParameter.Style.MATRIX);
                continue;
            }
            if (parameterStyleCode == INT_HEADER) {
                holder.addParameter(name, value, SoapUIRestParameter.Style.HEADER);
                continue;
            }
            if (parameterStyleCode == INT_QUERY) {
                holder.addParameter(name, value, SoapUIRestParameter.Style.QUERY);
                continue;
            }
            if (parameterStyleCode == INT_TEMPLATE) {
                holder.addParameter(name, value, SoapUIRestParameter.Style.TEMPLATE);
                continue;
            }
        }
    }
}
