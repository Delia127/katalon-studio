package com.kms.katalon.composer.webservice.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.webservice.postman.ApiKey;
import com.kms.katalon.composer.webservice.postman.Auth;
import com.kms.katalon.composer.webservice.postman.Basic;
import com.kms.katalon.composer.webservice.postman.FormData;
import com.kms.katalon.composer.webservice.postman.Header;
import com.kms.katalon.composer.webservice.postman.Item;
import com.kms.katalon.composer.webservice.postman.Oauth2;
import com.kms.katalon.composer.webservice.postman.PostmanCollection;
import com.kms.katalon.composer.webservice.postman.Query;
import com.kms.katalon.composer.webservice.postman.Request;
import com.kms.katalon.composer.webservice.postman.Urlencoded;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.Base64;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;
import com.kms.katalon.entity.webservice.TextBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

public class PostmanParseUtils {
    static FolderEntity folder =null;
    public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder,
            String fileLocationOrUrl) throws JsonParseException, JsonMappingException, IOException, ControllerException {
        FolderEntity folderEntity = FolderController.getInstance().addNewFolder(parentFolder, "Postman");
        List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        PostmanCollection postman = objectMapper.readValue(new File(fileLocationOrUrl), PostmanCollection.class);

        //List<Item> rootItems = postman.getItem().stream().map(i -> flatten(i)).flatMap(List::stream).collect(Collectors.toList());
        ExecutionProfileEntity profileEntity = GlobalVariableController.getInstance().getExecutionProfile("default",
                ProjectController.getInstance().getCurrentProject());
        List<GlobalVariableEntity> globalVariableEntites = profileEntity.getGlobalVariableEntities();
        List<VariableEntity> allVariables = new ArrayList<>();
        List<FileEntity> childrenEntities = new ArrayList<FileEntity>();
        List<Item> rootItems = postman.getItem();
        for (Item root : rootItems) {
            if(root.getRequest() == null){
                folder = FolderController.getInstance().addNewFolder(folderEntity, root.getName());
                folder.setChildrenEntities(childrenEntities); 
                pushPostManItemToWSTestObjectSet(root, newWSTestObjects);
            }
            if (root.getRequest() != null) {
                WebServiceRequestEntity entity = new WebServiceRequestEntity();
                Request request = root.getRequest();
                String rawURL = request.getURL().getRaw();
                String method = root.getRequest().getMethod();
                String name = root.getName();
                String body = StringUtils.defaultString(request.getBody().getRaw());
                List<VariableEntity> variablesFromURL = collectVariablesFromRawString(rawURL);
                List<VariableEntity> variablesFromBody = collectVariablesFromRawString(body);
                HttpHeaderVariable httpHeaderVariable = collectHttpHeaderVariable(request, root);
                entity.setName(name);
                entity.setRestRequestMethod(method.toString());
                entity.setRestUrl(getVariableString(variablesFromURL, rawURL));
                entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
                List<WebElementPropertyEntity> httpHeaders = httpHeaderVariable.getHttpHeaders();
                httpHeaders.addAll(0, getHttpAuthentication(root.getRequest().getAuth()));
                entity.setHttpHeaderProperties(httpHeaders);

                List<VariableEntity> collectedVariables = new ArrayList<>();
                collectedVariables.addAll(variablesFromURL);
                collectedVariables.addAll(httpHeaderVariable.getVariables());
                collectedVariables.addAll(variablesFromBody);
                List<VariableEntity> entityVariables = filterDuplicatedVariables(collectedVariables);
                entity.setVariables(entityVariables);

                if (root.getRequest().getBody().getFormdata() != null) {
                    ParameterizedBodyContent<FormDataBodyParameter> formDataBodyParameters = collectFormDataBody(
                            root);
                    entity.setHttpBodyType("form-data");
                    entity.setHttpBodyContent(JsonUtil.toJson(formDataBodyParameters));
                } else if (root.getRequest().getBody().getUrlencoded() != null) {
                    ParameterizedBodyContent<UrlEncodedBodyParameter> urlEncodedBodyParameters = collectUrlEncodedBody(
                            root);
                    entity.setHttpBodyType("x-www-form-urlencoded");
                    entity.setHttpBodyContent(JsonUtil.toJson(urlEncodedBodyParameters));
                } else {
                    String textBody = getVariableString(variablesFromBody, body);
                    TextBodyContent textBodyContent = new TextBodyContent();
                    String contentType = getContentType(httpHeaders);
                    entity.setHttpBodyType("text");
                    textBodyContent.setContentType(TextContentType.evaluateContentType(contentType).getContentType());
                    textBodyContent.setText(textBody);
                    entity.setHttpBodyContent(JsonUtil.toJson(textBodyContent));
                }
                entity.setParentFolder(folderEntity);
                newWSTestObjects.add(entity);

                allVariables.addAll(entityVariables);
            }
        }

        globalVariableEntites.addAll(toGlobalVariables(allVariables));
        profileEntity.setGlobalVariableEntities(filterDuplicatedGlobalVariables(globalVariableEntites));
        GlobalVariableController.getInstance().updateExecutionProfile(profileEntity);
        
        return newWSTestObjects;
    }
    
    public static List<WebElementPropertyEntity> getHttpAuthentication(Auth auth) {
        if (auth == null || StringUtils.isEmpty(auth.getType())) {
            return Collections.emptyList();
        }
        List<WebElementPropertyEntity> authenticationHeaders = new ArrayList<>();
        switch (auth.getType()) {
            case "basic": {
                List<Basic> basicItems = auth.getBasic();
                String username = "";
                String password = "";
                for (Basic b : basicItems) {
                    if ("username".equals(b.getKey())) {
                        username = b.getValue();
                    }
                    
                    if ("password".equals(b.getKey())) {
                        password = b.getValue();
                    }
                }

                WebElementPropertyEntity authenticationProp = new WebElementPropertyEntity();
                authenticationProp.setName("Authorization");
                authenticationProp.setValue("Basic " + Base64.basicEncode(username, password));
                authenticationProp.setIsSelected(true);
                
                authenticationHeaders.add(authenticationProp);
                break;
            }
            case "oauth2": {
                List<Oauth2> oath2Items = auth.getOauth2();
                String accessToken = "";
                String tokenType = "";
                for (Oauth2 b : oath2Items) {
                    if ("accessToken".equals(b.getKey())) {
                        accessToken = b.getValue();
                    }
                    
                    if ("tokenType".equals(b.getKey())) {
                        tokenType = b.getValue();
                    }
                }

                WebElementPropertyEntity authenticationProp = new WebElementPropertyEntity();
                authenticationProp.setName("Authorization");
                authenticationProp.setValue(tokenType + " " + accessToken);
                authenticationProp.setIsSelected(true);

                authenticationHeaders.add(authenticationProp);
                break;
            }
            case "apikey": {
                List<ApiKey> oath2Items = auth.getApikey();
                String key = "";
                String value = "";
                for (ApiKey b : oath2Items) {
                    if ("key".equals(b.getKey())) {
                        key = b.getValue();
                    }

                    if ("tokenType".equals(b.getKey())) {
                        value = b.getValue();
                    }
                }

                WebElementPropertyEntity authenticationProp = new WebElementPropertyEntity();
                authenticationProp.setName(key);
                authenticationProp.setValue(value);
                authenticationProp.setIsSelected(true);

                authenticationHeaders.add(authenticationProp);
                break;
            }
        }
        return authenticationHeaders;
    }
    
    public static List<Item> flatten(Item item) {
        List<Item> leafItems = new ArrayList<>();
        if (item.getRequest() != null) {
            leafItems.add(item);
        }
        if (item.getItem() != null) {
            for (Item child : item.getItem()) {
                leafItems.addAll(flatten(child));
            }
        }
        return leafItems;
    }
    
    public static List<GlobalVariableEntity> filterDuplicatedGlobalVariables(List<GlobalVariableEntity> variableEntities) {
        Set<String> currentNames = new LinkedHashSet<>();
        List<GlobalVariableEntity> filtedVariables = new ArrayList<>();
        for (GlobalVariableEntity variable : variableEntities) {
            String variableName = variable.getName();
            if (!currentNames.contains(variableName)) {
                filtedVariables.add(variable);
                currentNames.add(variable.getName());
            }
        }
        return filtedVariables;
    }

    public static List<GlobalVariableEntity> toGlobalVariables(List<VariableEntity> variableEntities) {
        List<GlobalVariableEntity> globalVariableEntities = new ArrayList<>();
        for (VariableEntity varible : variableEntities) {
            GlobalVariableEntity globalVariable = new GlobalVariableEntity();
            globalVariable.setName(varible.getName());
            globalVariable.setInitValue("''");
            
            globalVariableEntities.add(globalVariable);
        }
        return globalVariableEntities;
    }
    
    public static List<VariableEntity> filterDuplicatedVariables(List<VariableEntity> variableEntities) {
        Set<String> currentNames = new LinkedHashSet<>();
        List<VariableEntity> filtedVariables = new ArrayList<>();
        for (VariableEntity variable : variableEntities) {
            String variableName = variable.getName();
            if (!currentNames.contains(variableName)) {
                filtedVariables.add(variable);
                currentNames.add(variable.getName());
            }
        }
        return filtedVariables;
    }
    
    public static String getContentType(List<WebElementPropertyEntity> headerProperties) {
        for (WebElementPropertyEntity property : headerProperties) {
            if ("Content-Type".equals(property.getName())) {
                return property.getValue();
            }
        }
        return "";
    }

    public static List<VariableEntity> collectVariablesFromRawString(String rawString) {
        List<VariableEntity> variables = new ArrayList<>(); 
        Pattern pattern = Pattern.compile("\\{\\{((?!\\{).)*\\}\\}");
        Matcher matcher = pattern.matcher(rawString);
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            VariableEntity variable = new VariableEntity();
            String variableName = rawString.substring(matchResult.start() + 2, matchResult.end() - 2);
            variable.setName(variableName);
            variable.setDefaultValue("GlobalVariable." + variableName);
            variables.add(variable);
        }
        return variables;
    }

    public static String getVariableString(List<VariableEntity> variables, String rawString) {
        String variableString = rawString;
        for (VariableEntity variable : variables) {
            String variableName = variable.getName();
            variableString = variableString.replace("{{" + variableName + "}}", "${" + variable.getName() + "}");
        }
        return variableString;
    }

    public static void pushPostManItemToWSTestObjectSet(Item item, List<WebServiceRequestEntity> newWSTestObjects) {
        for (Item childItem : item.getItem()) {
            WebServiceRequestEntity entity = new WebServiceRequestEntity();
            Request request = childItem.getRequest();

            String raw = request.getURL().getRaw();
            String method = childItem.getRequest().getMethod();
            String name = childItem.getName();
            String nameVariable = collectNameItem(name, childItem);
            String pathVariable = collectPathItem(raw, childItem);
            List<WebElementPropertyEntity> propertiesEntity = collectHttpHeaderItem(request, childItem);
            List<VariableEntity> variable = collectVariableItem(request);
            // List<WebElementPropertyEntity> parametersInQuery = collectQueryParameter(childItem);

            entity.setName(nameVariable);
            entity.setRestRequestMethod(method.toString());
            entity.setRestUrl(pathVariable);
            entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
            entity.setVariables(variable);
            entity.setHttpBody(request.getBody().getRaw());
            entity.setHttpHeaderProperties(propertiesEntity);
            if (childItem.getRequest().getBody().getFormdata() != null) {
                ParameterizedBodyContent<FormDataBodyParameter> formDataBodyParameters = collectFormDataBody(childItem);

                entity.setHttpBodyType("form-data");
                entity.setHttpBodyContent(JsonUtil.toJson(formDataBodyParameters));
            }
            if (childItem.getRequest().getBody().getUrlencoded() != null) {
                ParameterizedBodyContent<UrlEncodedBodyParameter> urlEncodedBodyParameters = collectUrlEncodedBody(
                        childItem);
                entity.setHttpBodyType("x-www-form-urlencoded");
                entity.setHttpBodyContent(JsonUtil.toJson(urlEncodedBodyParameters));
            }
            entity.setParentFolder(folder);
            newWSTestObjects.add(entity);
        }
    }

    public static String collectPathItem(String variablePath, Item item) {
        String katalonVariablePath = "";
        String[] pathAndVariables = variablePath.toString().split("[\\{||\\}]");
        katalonVariablePath += pathAndVariables[0];
        String variables = "";

        if (pathAndVariables.length > 1) {
            for (int i = 1; i < pathAndVariables.length; i++) {
                if (!(pathAndVariables[i].equals("")) && !(pathAndVariables[i].contains("/"))) {
                    katalonVariablePath += "${" + pathAndVariables[i] + "}";
                } else if (pathAndVariables[i].contains(":")) {
                    variables = pathAndVariables[i];
                } else if (!pathAndVariables[i].contains(":") && pathAndVariables[i].contains("/")) {
                    katalonVariablePath += pathAndVariables[i];
                }
            }
            String[] splitVariables = variables.split("[\\:]");
            katalonVariablePath += splitVariables[0];
            if (splitVariables.length > 1) {
                for (int j = 1; j < splitVariables.length; j++) {
                    if (splitVariables[j].contains("/")) {
                        String[] var = splitVariables[j].split("[\\/]");
                        if (katalonVariablePath.endsWith("/")) {
                            katalonVariablePath += "${" + var[0] + "}";
                        } else {
                            katalonVariablePath += "/" + "${" + var[0] + "}";
                        }
                        if (var.length > 1) {
                            for (int k = 1; k < var.length; k++) {
                                katalonVariablePath += "/" + var[k];
                            }
                        }
                    } else {
                        if (katalonVariablePath.endsWith("/")) {
                            katalonVariablePath += "${" + splitVariables[j] + "}";
                        } else {
                            katalonVariablePath += "/" + "${" + splitVariables[j] + "}";
                        }
                    }
                }
            }
        }
        variablePath = katalonVariablePath;
        return variablePath;

    }
    
    public static HttpHeaderVariable collectHttpHeaderVariable(Request request, Item childItem) {
        HttpHeaderVariable httpHeaderVariables = new HttpHeaderVariable();
        List<Header> header = childItem.getRequest().getHeader();

        List<WebElementPropertyEntity> propertyEntities = new ArrayList<WebElementPropertyEntity>();
        
        List<VariableEntity> variables = new ArrayList<>();
        for (int i = 0; i < header.size(); i++) {
            WebElementPropertyEntity webElementProperty = new WebElementPropertyEntity();
            String rawKey = header.get(i).getKey();
            String rawValue = header.get(i).getValue();
            
            List<VariableEntity> variablesFromKey = collectVariablesFromRawString(rawKey);
            webElementProperty.setName(getVariableString(variablesFromKey, rawKey));
            
            List<VariableEntity> variablesFromValue = collectVariablesFromRawString(rawValue);
            webElementProperty.setValue(getVariableString(variablesFromValue, rawValue));

            propertyEntities.add(i, webElementProperty);
            
            variables.addAll(variablesFromKey);
            variables.addAll(variablesFromValue);
        }

        httpHeaderVariables.setHttpHeaders(propertyEntities);
        httpHeaderVariables.setVariables(variables);

        return httpHeaderVariables;

    }

    public static List<WebElementPropertyEntity> collectHttpHeaderItem(Request request, Item childItem) {
        List<Header> header = childItem.getRequest().getHeader();

        List<WebElementPropertyEntity> propertiesEntity = new ArrayList<WebElementPropertyEntity>();
        for (int i = 0; i < header.size(); i++) {
            WebElementPropertyEntity webElementProperty = new WebElementPropertyEntity();
            String key = header.get(i).getKey();
            String value = header.get(i).getValue();
            webElementProperty.setName(key);
            webElementProperty.setValue(value);
            propertiesEntity.add(i, webElementProperty);
        }
        return propertiesEntity;
    }

    public static String collectNameItem(String name, Item item) {
        String katalonVariableName = "";
        String[] pathAndVariables = name.toString().split("\\/");
        katalonVariableName += pathAndVariables[0];
        if (pathAndVariables.length > 1) {
            for (int i = 1; i < pathAndVariables.length; i++) {
                katalonVariableName += " or " + pathAndVariables[i];
            }
        }
        name = katalonVariableName;
        return name;
    }

    public static List<VariableEntity> collectVariableItem(Request request) {
        List<VariableEntity> variable = new ArrayList<VariableEntity>();
        if (request.getURL().getVariable() != null) {
            for (int i = 0; i < request.getURL().getVariable().size(); i++) {
                VariableEntity variableEntity = new VariableEntity();
                String keyVar = request.getURL().getVariable().get(i).getKey();
                String id = request.getURL().getVariable().get(i).getId();
                String valueVar = request.getURL().getVariable().get(i).getValue();
                String decription = request.getURL().getVariable().get(i).getDescription();
                variableEntity.setName(keyVar);
                variableEntity.setId(id);
                variableEntity.setDefaultValue(valueVar);
                variableEntity.setDescription(decription);
                variable.add(i, variableEntity);
            }
        }
        return variable;
    }

    public static ParameterizedBodyContent<FormDataBodyParameter> collectFormDataBody(Item item) {
        List<FormData> data = item.getRequest().getBody().getFormdata();

        ParameterizedBodyContent<FormDataBodyParameter> katalonFormParams = new ParameterizedBodyContent<FormDataBodyParameter>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                FormDataBodyParameter katalonFormParam = new FormDataBodyParameter();
                katalonFormParam.setName(data.get(i).getKey());
                katalonFormParam.setValue(data.get(i).getValue());
                katalonFormParam.setType(data.get(i).getType());
                katalonFormParams.addParameter(katalonFormParam);
            }

        }
        return katalonFormParams;

    }

    public static ParameterizedBodyContent<UrlEncodedBodyParameter> collectUrlEncodedBody(Item item) {

        List<Urlencoded> urlEncoded = item.getRequest().getBody().getUrlencoded();
        ParameterizedBodyContent<UrlEncodedBodyParameter> urlEncodedBodyParams = new ParameterizedBodyContent<UrlEncodedBodyParameter>();
        if (urlEncoded != null) {
            for (int i = 0; i < urlEncoded.size(); i++) {
                UrlEncodedBodyParameter urlEncodedBodyParam = new UrlEncodedBodyParameter();
                urlEncodedBodyParam.setName(urlEncoded.get(i).getKey());
                urlEncodedBodyParam.setValue(urlEncoded.get(i).getValue());
                urlEncodedBodyParams.addParameter(urlEncodedBodyParam);
            }
        }
        return urlEncodedBodyParams;

    }

    public static List<WebElementPropertyEntity> collectQueryParameter(Item childItem) {
        List<Query> query = childItem.getRequest().getURL().getQuery();
        List<WebElementPropertyEntity> propertiesEntity = new ArrayList<WebElementPropertyEntity>();
        for (int i = 0; i < query.size(); i++) {
            WebElementPropertyEntity webElementProperty = new WebElementPropertyEntity();
            String key = query.get(i).getKey();
            String value = query.get(i).getValue();
            webElementProperty.setName(key);
            webElementProperty.setValue(value);
            propertiesEntity.add(i, webElementProperty);
        }
        return propertiesEntity;

    }

    public static List<WebServiceRequestEntity> newWSTestObjectsFromPostman(FolderEntity parentFolder,
            String directoryOfJsonFile) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        List<WebServiceRequestEntity> newWSTestObjects = (List<WebServiceRequestEntity>) PostmanParseUtils
                .parseFromFileLocationToWSTestObject(parentFolder, directoryOfJsonFile);
        for (WebServiceRequestEntity entity : newWSTestObjects) {
            entity.setElementGuidId(Util.generateGuid());
            entity.setProject(parentFolder.getProject());
        }

        return newWSTestObjects;
    }
    
    public static class HttpHeaderVariable {
        private List<WebElementPropertyEntity> httpHeaders;
        
        private List<VariableEntity> variables;

        public List<WebElementPropertyEntity> getHttpHeaders() {
            return httpHeaders;
        }

        public void setHttpHeaders(List<WebElementPropertyEntity> httpHeaders) {
            this.httpHeaders = httpHeaders;
        }

        public List<VariableEntity> getVariables() {
            return variables;
        }

        public void setVariables(List<VariableEntity> variables) {
            this.variables = variables;
        }
    }
}
