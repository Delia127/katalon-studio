package com.kms.katalon.composer.webservice.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kms.katalon.composer.webservice.postman.Header;
import com.kms.katalon.composer.webservice.postman.Item;
import com.kms.katalon.composer.webservice.postman.Method;
import com.kms.katalon.composer.webservice.postman.PostmanCollection;
import com.kms.katalon.composer.webservice.postman.Request;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;

public class PostmanParseUtils {

    @SuppressWarnings({ "unchecked", "finally" })
    public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder,
            String fileLocationOrUrl) throws Exception {

        List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            PostmanCollection postman;
            postman = objectMapper.readValue(new File(fileLocationOrUrl), PostmanCollection.class);
            List<Item> rootItems = postman.getItem();
            for (Item root : rootItems) {
                pushPostManItemToWSTestObjectSet(root, newWSTestObjects);
            }
        } catch (Throwable ex) {
            throw ex;
        } finally {
            if (newWSTestObjects.size() > 0) {
                return newWSTestObjects;
            } else return null;
        }
    }

    public static void pushPostManItemToWSTestObjectSet(Item item, List<WebServiceRequestEntity> newWSTestObjects) {
        if (item.getRequest() == null) {
            for (Item childItem : item.getItem()) {
                pushPostManItemToWSTestObjectSet(childItem, newWSTestObjects);
            }
        }
        for (Item childItem : item.getItem()) {
            ArrayList<Object> listRaw = new ArrayList<>();
            ArrayList<Object> listName = new ArrayList<>();
            WebServiceRequestEntity entity = new WebServiceRequestEntity();
            Request request = childItem.getRequest();
            List<Header> header = childItem.getRequest().getHeader();

            String urlCommonPrefix = "";
            String raw = request.getURL().getRaw();

            Method method = childItem.getRequest().getMethod();

            String name = childItem.getName();
            listName.add(name);

            for (Object oName : listName) {
                if (oName != null) {
                    String katalonVariableName = "";
                    String[] pathAndVariables = oName.toString().split("\\/");
                    katalonVariableName += pathAndVariables[0];

                    if (pathAndVariables.length > 1) {
                        for (int i = 1; i < pathAndVariables.length; i++) {
                            katalonVariableName += " or " + pathAndVariables[i];
                        }
                    }
                    entity.setName(katalonVariableName);
                }

            }

            listRaw.add(raw);
            entity.setRestRequestMethod(method.toString());
            for (Object oRaw : listRaw) {
                urlCommonPrefix += request.getURL().getRaw();
                String urlCommonPrefix2 = urlCommonPrefix;
                String katalonVariablePath = "";
                String[] pathAndVariables = oRaw.toString().split("[\\{||\\}]");
                katalonVariablePath += pathAndVariables[0];

                if (pathAndVariables.length > 1) {
                    for (int i = 1; i < pathAndVariables.length; i++) {
                        if (!(pathAndVariables[i].equals("")) && !(pathAndVariables[i].contains("/"))) {
                            katalonVariablePath += "${" + pathAndVariables[i] + "}";
                        } else if (pathAndVariables[i].contains("/")) {
                            katalonVariablePath += pathAndVariables[i];
                        }

                    }
                }
                String key = "";
                String value = "";

                List<WebElementPropertyEntity> propertiesEntity = new ArrayList<WebElementPropertyEntity>();
                for (int i = 0; i < header.size(); i++) {
                    WebElementPropertyEntity webElementProperty = new WebElementPropertyEntity();
                    key = header.get(i).getKey();
                    value = header.get(i).getValue();
                    webElementProperty.setName(key);
                    webElementProperty.setValue(value);
                    propertiesEntity.add(i, webElementProperty);
                }

                String keyVar = "";
                String valueVar = "";
                String id = "";
                String decription = "";
                List<VariableEntity> variable = new ArrayList<VariableEntity>();
                if (request.getURL().getVariable() != null) {
                    for (int i = 0; i < request.getURL().getVariable().size(); i++) {
                        VariableEntity variableEntity = new VariableEntity();
                        keyVar = request.getURL().getVariable().get(i).getKey();
                        id = request.getURL().getVariable().get(i).getId();
                        valueVar = request.getURL().getVariable().get(i).getValue();
                        decription = request.getURL().getVariable().get(i).getDescription();
                        variableEntity.setName(keyVar);
                        variableEntity.setId(id);
                        variableEntity.setDefaultValue(valueVar);
                        variableEntity.setDescription(decription);
                        variable.add(i, variableEntity);
                    }
                }

                urlCommonPrefix2 = katalonVariablePath;
                entity.setRestUrl(urlCommonPrefix2);
                entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
                entity.setVariables(variable);
                entity.setHttpBody(request.getBody().getRaw());
                entity.setHttpHeaderProperties(propertiesEntity);

            }
            // TODO: get require properties from request and put into entity

            newWSTestObjects.add(entity);
        }
    }

    public static List<WebServiceRequestEntity> newWSTestObjectsFromPostman(FolderEntity parentFolder,
            String directoryOfJsonFile) throws Exception {
        if (parentFolder == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<WebServiceRequestEntity> newWSTestObjects = (List<WebServiceRequestEntity>) PostmanParseUtils
                .parseFromFileLocationToWSTestObject(parentFolder, directoryOfJsonFile);
        for (WebServiceRequestEntity entity : newWSTestObjects) {
            entity.setElementGuidId(Util.generateGuid());
            entity.setParentFolder(parentFolder);
            entity.setProject(parentFolder.getProject());
        }

        return newWSTestObjects;
    }
}
