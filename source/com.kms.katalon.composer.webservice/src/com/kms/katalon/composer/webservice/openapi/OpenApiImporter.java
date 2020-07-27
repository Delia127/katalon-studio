package com.kms.katalon.composer.webservice.openapi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.common.base.Preconditions;
import com.kms.katalon.composer.webservice.constants.HttpMethod;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.Paths;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Content;
import io.swagger.oas.models.media.MediaType;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.servers.Server;
import io.swagger.oas.models.servers.ServerVariables;
import io.swagger.parser.OpenAPIParser;
import io.swagger.parser.models.ParseOptions;
import io.swagger.parser.models.SwaggerParseResult;

public final class OpenApiImporter {

    private static final OpenApiImporter INSTANCE = new OpenApiImporter();

    public static OpenApiImporter getInstance() {
        return INSTANCE;
    }

    public OpenApiProjectImportResult importServices(String projectFilePath, FolderEntity rootFolder) throws Exception {
        OpenApiProjectImportResult projectImportResult;

        Preconditions.checkNotNull(projectFilePath, "OpenAPI 3.0 project file path must not be null or empty.");
        File projectFile = new File(projectFilePath);
        Preconditions.checkArgument(projectFile.exists(), "OpenAPI 3.0 project file does not exist.");
        Preconditions.checkNotNull(rootFolder, "Root folder must not be null.");

        OpenAPIParser parser = new OpenAPIParser();
        ParseOptions options = new ParseOptions();
        options.setResolveFully(true);
        options.setResolve(true);
        SwaggerParseResult result = parser.readLocation(projectFilePath, null, options);

        OpenAPI openAPI = result.getOpenAPI();

        String name = null;
        Info info = openAPI.getInfo();
        if (info != null) {
            String title = info.getTitle();
            if (title != null) {
                name = title;
            }
        }
        FolderEntity projectImportFolder = getProjectImportFolder(name, rootFolder);
        projectImportResult = new OpenApiProjectImportResult(projectImportFolder);

        List<Server> servers = openAPI.getServers();
        String url = "/";
        if (servers != null) {
            Server server = servers.get(0);
            url = server.getUrl();
            if (server.getVariables() != null) {
                ServerVariables serverVariables = server.getVariables();
                for (String variable : serverVariables.keySet()) {
                    String defaultValue = serverVariables.get(variable).getDefault();
                    if (defaultValue == null) {
                        continue;
                    }
                    String variableTemplate = "{" + variable + "}";
                    if (url.contains(variableTemplate)) {
                        url = url.replace(variableTemplate, defaultValue);
                    }
                }
            }
        }
        OpenApiRestServiceImportResult serviceImportResult = parseService(projectImportResult, url, name);

        Paths paths = openAPI.getPaths();
        for (String pathKey : paths.keySet()) {
            PathItem path = paths.get(pathKey);
            String resourceName = path.getSummary() != null ? path.getSummary() : pathKey;
            OpenApiRestResourceImportResult resourceImportResult = serviceImportResult
                    .newResource(toValidFileName(resourceName), pathKey);
            parseMethods(resourceImportResult, path);
        }
        return projectImportResult;
    }

    private FolderEntity getProjectImportFolder(String name, FolderEntity parentFolder) throws Exception {
        if (StringUtils.isBlank(name)) {
            name = "Imported from OpenAPI 3.0";
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

    private OpenApiRestServiceImportResult parseService(OpenApiProjectImportResult projectImportResult, String url,
            String name) {
        OpenApiRestServiceImportResult serviceImportResult = projectImportResult.newService(name);
        serviceImportResult.setBasePath(url);
        return serviceImportResult;
    }

    private String toValidFileName(String fileName) {
        return fileName.replaceAll("\\W+", "_");
    }

    private void parseMethods(OpenApiRestResourceImportResult resourceImportResult, PathItem pathItem) {
        if (resourceImportResult == null || pathItem == null) {
            return;
        }
        if (pathItem.getGet() != null) {
            parseMethod(resourceImportResult, HttpMethod.GET, pathItem.getGet());
        }
        if (pathItem.getPost() != null) {
            parseMethod(resourceImportResult, HttpMethod.POST, pathItem.getPost());
        }
        if (pathItem.getDelete() != null) {
            parseMethod(resourceImportResult, HttpMethod.DELETE, pathItem.getDelete());
        }
        if (pathItem.getPut() != null) {
            parseMethod(resourceImportResult, HttpMethod.PUT, pathItem.getPut());
        }
        if (pathItem.getOptions() != null) {
            parseMethod(resourceImportResult, HttpMethod.OPTIONS, pathItem.getOptions());
        }
        if (pathItem.getTrace() != null) {
            parseMethod(resourceImportResult, HttpMethod.TRACE, pathItem.getTrace());
        }
        if (pathItem.getHead() != null) {
            parseMethod(resourceImportResult, HttpMethod.HEAD, pathItem.getHead());
        }
    }

    private void parseMethod(OpenApiRestResourceImportResult resourceImportResult, HttpMethod httpMethod,
            Operation operation) {
        if (operation == null || operation.getDeprecated() != null) {
            return;
        }
        String name;
        if (!StringUtils.isBlank(operation.getOperationId())) {
            name = operation.getOperationId();
        } else {
            name = httpMethod.toString();
        }
        OpenApiRestMethodImportResult methodImportResult = resourceImportResult.newMethod(toValidFileName(name),
                httpMethod.toString());
        addParameters(methodImportResult, operation.getParameters());
        parseRequests(methodImportResult, operation);
    }

    private void parseRequests(OpenApiRestMethodImportResult methodImportResult, Operation operation) {
        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            Content content = operation.getRequestBody().getContent();
            for (String mediaTypeName : content.keySet()) {
                MediaType mediaType = content.get(mediaTypeName);
                Schema<?> schema = mediaType.getSchema();
                if (mediaTypeName.equals(OpenApiRestRequestImportResult.FORM_URLENCODED_CONTENT_TYPE)) {
                    List<UrlEncodedBodyParameter> params = parseUrlEncodedRequestBody(schema);
                    OpenApiRestRequestImportResult request = methodImportResult
                            .newRequest(getRequestFileName(methodImportResult, "Request"));
                    request.setMediaType(mediaTypeName);
                    request.setHttpMethod(methodImportResult.getHttpMethod());
                    request.setUrlEncodedBodyParameters(params);
                } else if (mediaTypeName.equals(OpenApiRestRequestImportResult.APPLICATION_JSON_CONTENT_TYPE)) {
                    String bodyContent;
                    if (mediaType.getExample() != null) {
                        bodyContent = parseExample(mediaType.getExample());
                    } else if (schema.getExample() != null) {
                        bodyContent = parseExample(schema.getExample());
                    } else {
                        bodyContent = JsonUtil.toJson(parseJsonObject(schema));
                    }
                    OpenApiRestRequestImportResult request = methodImportResult
                            .newRequest(getRequestFileName(methodImportResult, "Request"));
                    request.setRequestBodyContent(bodyContent);
                    request.setMediaType(mediaTypeName);
                    request.setHttpMethod(methodImportResult.getHttpMethod());
                } else if (mediaTypeName.equals(OpenApiRestRequestImportResult.MULTIPART_FORM_DATA_CONTENT_TYPE)) {
                    List<FormDataBodyParameter> params = parseFormDataRequestBody(schema);
                    OpenApiRestRequestImportResult request = methodImportResult
                            .newRequest(getRequestFileName(methodImportResult, "Request"));
                    request.setMediaType(mediaTypeName);
                    request.setHttpMethod(methodImportResult.getHttpMethod());
                    request.setFormDataBodyParameters(params);
                } else if (mediaTypeName.equals(OpenApiRestRequestImportResult.MULTIPLE_CONTENT_TYPE)) {
                    String bodyContent;
                    if (schema.getExample() != null) {
                        bodyContent = parseExample(schema.getExample());
                    } else {
                        bodyContent = JsonUtil.toJson(parseJsonValue(schema));
                    }
                    OpenApiRestRequestImportResult request = methodImportResult
                            .newRequest(getRequestFileName(methodImportResult, "Request"));
                    request.setRequestBodyContent(bodyContent);
                    request.setMediaType(mediaTypeName);
                    request.setHttpMethod(methodImportResult.getHttpMethod());
                }
            }
        } else {
            OpenApiRestRequestImportResult request = methodImportResult
                    .newRequest(getRequestFileName(methodImportResult, "Request"));
            request.setHttpMethod(methodImportResult.getHttpMethod());
        }
    }

    private String parseExample(Object ex) {
        JSONObject json = new JSONObject(ex.toString());
        return json.toString(2);
    }

    @SuppressWarnings("rawtypes")
    private List<UrlEncodedBodyParameter> parseUrlEncodedRequestBody(Schema<?> schema) {
        Map<String, Schema> propertyMap = schema.getProperties();
        List<UrlEncodedBodyParameter> params = new ArrayList<>();
        for (Map.Entry<String, Schema> entry : propertyMap.entrySet()) {
            UrlEncodedBodyParameter param = new UrlEncodedBodyParameter();
            param.setName(entry.getKey());
            String value = entry.getValue().getExample() != null ? entry.getValue().getExample().toString()
                    : entry.getValue().getType();
            param.setValue("<" + value + ">");
            params.add(param);
        }
        return params;
    }

    @SuppressWarnings("rawtypes")
    private List<FormDataBodyParameter> parseFormDataRequestBody(Schema<?> schema) {
        Map<String, Schema> propertyMap = schema.getProperties();
        List<FormDataBodyParameter> params = new ArrayList<>();
        for (Map.Entry<String, Schema> entry : propertyMap.entrySet()) {
            FormDataBodyParameter param = new FormDataBodyParameter();
            param.setName(entry.getKey());
            param.setValue(JsonUtil.toJson(parseJsonValue(entry.getValue())));
            String format = entry.getValue().getFormat();
            if (format != null) {
                if (entry.getValue().getType().equals("string") && format.equals("binary") || format.equals("byte")) {
                    param.setType(FormDataBodyParameter.PARAM_TYPE_FILE);
                } else {
                    param.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
                }
            }
            params.add(param);
        }
        return params;
    }

    private String getRequestFileName(OpenApiRestMethodImportResult methodImportResult, String requestName) {
        String suggestion = requestName;
        int numberSuffix = 0;
        while (!methodImportResult.isRequestFileNameAvailable(suggestion)) {
            numberSuffix++;
            suggestion += " (" + numberSuffix + ")";
        }
        return suggestion;
    }

    private void addParameters(OpenApiRestResourceImportNode holder, List<Parameter> restParameters) {
        if (restParameters == null)
            return;
        for (Parameter param : restParameters) {
            String in = param.getIn();
            String name = param.getName();
            String value = null;
            if (param.getSchema() != null) {
                value = param.getSchema().getType();
            }
            String description = param.getDescription();
            if (in.equals("path")) {
                holder.addParameter(name, null, description, OpenApiRestParameter.Style.TEMPLATE);
            } else if (in.equals("query")) {
                holder.addParameter(name, "<" + value + ">", description, OpenApiRestParameter.Style.QUERY);
            } else if (in.equals("header")) {
                holder.addParameter(name, "<" + value + ">", description, OpenApiRestParameter.Style.HEADER);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> parseJsonObject(Schema<?> schema) {
        Map<String, Schema> propertyMap = schema.getProperties();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Schema> entry : propertyMap.entrySet()) {
            String name = entry.getKey();
            Object value = parseJsonValue(entry.getValue());
            if (value != null)
                result.put(name, value);
        }
        return result;
    }

    private Object parseJsonValue(Schema<?> schema) {
        Object output = null;
        if (schema.getExample() != null) {
            return schema.getExample();
        }
        if (schema.getEnum() != null) {
            return schema.getEnum().get(0);
        }
        switch (schema.getType()) {
        case "integer":
            output = 0;
            break;
        case "number":
            output = 1.1f;
            break;
        case "string":
            output = "";
            break;
        case "boolean":
            output = true;
            break;
        case "array":
            Schema<?> items = ((ArraySchema) schema).getItems();
            List<Object> arr = new ArrayList<>();
            arr.add(parseJsonValue(items));
            output = arr;
            break;
        case "object":
            output = parseJsonObject(schema);
            break;
        }
        return output;
    }
}
