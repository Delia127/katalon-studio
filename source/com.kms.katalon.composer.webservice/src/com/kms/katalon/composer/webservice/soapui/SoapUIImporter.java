package com.kms.katalon.composer.webservice.soapui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlException;

import com.eviware.soapui.config.Interface;
import com.eviware.soapui.config.Project;
import com.eviware.soapui.config.RestMethod;
import com.eviware.soapui.config.RestParameter;
import com.eviware.soapui.config.RestRequest;
import com.eviware.soapui.config.RestResource;
import com.eviware.soapui.config.RestService;
import com.eviware.soapui.config.SoapuiProjectDocument;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class SoapUIImporter {

    public WebServiceRequestEntity[] importRestRequests(String projectFilePath, FolderEntity rootFolder)
            throws XmlException, IOException {
        if (StringUtils.isBlank(projectFilePath)) {
            throw new IllegalArgumentException("projectFilePath must not be null or empty");
        }

        File projectFile = new File(projectFilePath);
        if (!projectFile.exists()) {
            throw new FileNotFoundException("File " + projectFilePath + " does not exist.");
        }

        SoapUIImportFileHierarchy folderHierarchy = new SoapUIImportFileHierarchy(rootFolder);

        SoapuiProjectDocument projectDocument = SoapuiProjectDocument.Factory.parse(projectFile);
        Project project = projectDocument.getSoapuiProject();
        Interface[] interfaces = project.getInterfaceArray();
        for (Interface iface : interfaces) {
            if (iface instanceof RestService) {
                RestService restService = (RestService) iface;
                String endpoint = restService.getEndpoints().getEndpointArray()[0];
                RestResource[] restResources = restService.getResourceArray();
                List<SoapUIResourceImportResult> resourceImportResults = new ArrayList<>();
                for (RestResource restResource : restResources) {
                    SoapUIResourceImportResult resourceImportResult = parseResource(endpoint, restResource, folderHierarchy);
                    resourceImportResults.add(resourceImportResult);
                }
            }
        }
        return new WebServiceRequestEntity[0];
    }

    private SoapUIResourceImportResult parseResource(String endpoint, RestResource restResource,
            SoapUIImportFileHierarchy folderHierarchy) {
        String resourcePath = restResource.getPath();
        SoapUIResourceImportResult resourceResult = new SoapUIResourceImportResult(
                endpoint,
                resourcePath,
                folderHierarchy);
        addParameters(resourceResult, restResource.getParameters().getParameterArray());
        for (RestMethod restMethod : restResource.getMethodArray()) {
            createMethod(resourceResult, restMethod);
        }
        return resourceResult;
    }

    private void createMethod(SoapUIResourceImportResult resourceResult, RestMethod restMethod) {
        String methodName = restMethod.getName();
        String httpMethod = restMethod.getMethod();
        SoapUIMethodImportResult methodResult = resourceResult.newMethod(methodName, httpMethod);
        addParameters(methodResult, restMethod.getParameters().getParameterArray());
        for (RestRequest restRequest : restMethod.getRequestArray()) {
            createRequest(methodResult, restRequest);
        }
    }

    private void createRequest(SoapUIMethodImportResult methodResult, RestRequest restRequest) {
    }

    private void addParameters(SoapUIResourceElementImportResult holder, RestParameter[] restParameters) {
        final int INT_MATRIX = 1;
        final int INT_HEADER = 2;
        final int INT_QUERY = 3;
        final int INT_TEMPLATE = 4;

        for (RestParameter parameter : restParameters) {
            int parameterStyleCode = parameter.getStyle().intValue();
            String name = parameter.getName();
            String value = parameter.getValue();
            if (parameterStyleCode == INT_MATRIX) {
                holder.newParameter(name, value, SoapUIRestParameter.Style.MATRIX);
                continue;
            }
            if (parameterStyleCode == INT_HEADER) {
                holder.newParameter(name, value, SoapUIRestParameter.Style.HEADER);
                continue;
            }
            if (parameterStyleCode == INT_QUERY) {
                holder.newParameter(name, value, SoapUIRestParameter.Style.QUERY);
                continue;
            }
            if (parameterStyleCode == INT_TEMPLATE) {
                holder.newParameter(name, value, SoapUIRestParameter.Style.TEMPLATE);
                continue;
            }
        }
    }

}
