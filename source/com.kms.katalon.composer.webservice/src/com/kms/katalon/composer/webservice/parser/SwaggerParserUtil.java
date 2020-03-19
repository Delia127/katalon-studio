package com.kms.katalon.composer.webservice.parser;

import v2.io.swagger.parser.SwaggerParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.webservice.util.SafeUtils;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Scheme;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.Parameter;

public class SwaggerParserUtil {
	private final static String IN_QUERY = "query";
	private final static String IN_PATH = "path";
	private final static String IN_BODY = "body";
	private final static String IN_FORM_DATA = "formData";
	private final static String ACCEPT_FORM_DATA = "form-data";
	private final static String ACCEPT_URL_ENCODED = "x-www-form-urlencoded";


	@SuppressWarnings({ "unchecked", "finally" })
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocationOrUrl) throws Exception{
		
		List<WebServiceRequestEntity> newWSTestObjects = new ArrayList<WebServiceRequestEntity>();
		List<String> wsTestObjectNames = new ArrayList<>();
		
		try{
			Swagger swagger = new SwaggerParser().read(fileLocationOrUrl);
			String urlCommonPrefix = "https";
			if(swagger.getSchemes() != null 
					&& !swagger.getSchemes().isEmpty() 
					&& !swagger.getSchemes().contains(Scheme.HTTPS)){				
				urlCommonPrefix = swagger.getSchemes().get(0).toString();
			}
			
			urlCommonPrefix += "://" + swagger.getHost() + swagger.getBasePath();	
		
			for(Object oPathEntry : SafeUtils.safeSet(swagger.getPaths().entrySet())){
				if(oPathEntry != null){
					
					Entry<String, Path> pathEntry = (Entry<String, Path>) oPathEntry;					
					Path aPath = pathEntry.getValue();												
					String urlCommonPrefix2 = urlCommonPrefix;				
					String katalonVariablePath = "";
					String[] pathAndVariables = SafeUtils.safeString(pathEntry.getKey()).split("[\\{||\\}]");
					
					katalonVariablePath += pathAndVariables[0];
					// Replace every occurrence of { .. } with ${ .. }
					if(pathAndVariables.length > 1 ){
						for(int i = 1; i < pathAndVariables.length; i++){
							katalonVariablePath += "${" + pathAndVariables[i] + "}";
						}										
					}
					
					urlCommonPrefix2 += katalonVariablePath;
					
					for(Object oMethodAndOperation : SafeUtils.safeSet(aPath.getOperationMap().entrySet())){
						if(oMethodAndOperation != null){
							
							Entry<HttpMethod, Operation> methodAndOperation = (Entry<HttpMethod, Operation>) oMethodAndOperation;							
							Operation operation = methodAndOperation.getValue();						
							WebServiceRequestEntity entity = new WebServiceRequestEntity();
							HttpMethod method = methodAndOperation.getKey();		
							String wsObjectName = SafeUtils.safeString(operation.getOperationId());
							List<WebElementPropertyEntity> parametersInQuery = new ArrayList<>();
							List<WebElementPropertyEntity> parametersInHeader = new ArrayList<>();
							List<VariableEntity> parametersInPath = new ArrayList<>();
							ParameterizedBodyContent<FormDataBodyParameter> katalonFormParams = new 
									ParameterizedBodyContent<FormDataBodyParameter>();
							ParameterizedBodyContent<UrlEncodedBodyParameter> urlEncodedBodyParams = new 
									ParameterizedBodyContent<UrlEncodedBodyParameter>();
							
							String entitySuggestionName = wsObjectName.isEmpty() ? "Web service Object" : wsObjectName;
							setRequestEntityName(entitySuggestionName, entity, wsTestObjectNames);
							entity.setRestUrl(urlCommonPrefix2);
							entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
							entity.setRestRequestMethod(method.toString());
							
							for(Object oParam : SafeUtils.safeList(operation.getParameters())){
								if(oParam != null){
									
									Parameter param = (Parameter) oParam;									
									String name = SafeUtils.safeString(param.getName());
									String pattern = SafeUtils.safeString(param.getPattern());
									
									switch(param.getIn()){
										case IN_QUERY:
											parametersInQuery.add(new WebElementPropertyEntity(name, pattern));
											break;
										case IN_PATH:
											VariableEntity variable = new VariableEntity();
											variable.setName(name);											
											variable.setDefaultValue("''");
											parametersInPath.add(variable);
											break;
										case IN_BODY:
											// TODO: Handle body content
											break;
										case IN_FORM_DATA:
											FormParameter formParam = (FormParameter) param;
											
											FormDataBodyParameter katalonFormParam = new FormDataBodyParameter();
											katalonFormParam.setName(formParam.getName());
											katalonFormParam.setType(formParam.getType());
											katalonFormParam.setValue(StringUtils.EMPTY);
											katalonFormParams.addParameter(katalonFormParam);
											
											UrlEncodedBodyParameter urlEncodedBodyParam = new UrlEncodedBodyParameter();
											urlEncodedBodyParam.setName(formParam.getName());
											urlEncodedBodyParams.addParameter(urlEncodedBodyParam);
											break;										
										default:
											break;
									}
								}
							}	
							

							for(Object produce : SafeUtils.safeList(operation.getProduces())){
								if(produce != null && !((String) produce).isEmpty()){
									WebElementPropertyEntity produceProperty = new WebElementPropertyEntity("Content-type", (String) produce);
									parametersInHeader.add(produceProperty);
								}
							}					
							
							String consumeType = StringUtils.EMPTY;
							for(Object consume : SafeUtils.safeList(operation.getConsumes())){
								if(consume != null && !((String) consume).isEmpty()){
									WebElementPropertyEntity consumeProperty = new WebElementPropertyEntity("Accept", (String) consume);
									parametersInHeader.add(consumeProperty);
									if(consumeType.isEmpty()){
										String [] consumeStrings = ((String) consume).split("/");
										consumeType = (consumeStrings.length > 1 ) ? consumeStrings[consumeStrings.length - 1] : null;
									}
								}
							}	
							
							switch(consumeType){
								case ACCEPT_FORM_DATA:
									if(katalonFormParams.getParameters().size() > 0){
										entity.setHttpBodyType("form-data");
										entity.setHttpBodyContent(JsonUtil.toJson(katalonFormParams));
									}
									break;
								case ACCEPT_URL_ENCODED:
									if(urlEncodedBodyParams.getParameters().size() > 0){
										entity.setHttpBodyType("x-www-form-urlencoded");
										entity.setHttpBodyContent(JsonUtil.toJson(urlEncodedBodyParams));
									}
									break;
								default:
									break;
							}
							
							entity.setRestParameters(parametersInQuery);
							entity.setVariables(parametersInPath);
							entity.setHttpHeaderProperties(parametersInHeader);
							newWSTestObjects.add(entity);
						}
					}
				}
			}
			
		} catch (Throwable  ex) {
			throw ex;
        } finally {
	    	if(newWSTestObjects.size() > 0 ) { 
	    		return newWSTestObjects;
	    	} else 
	    		return null;
        }
	}
	
	private static void setRequestEntityName(String suggestion, WebServiceRequestEntity entity, List<String> availableNames) {
        int index = 0;
        String entityName = suggestion;
        while (isNameDuplicated(entityName, availableNames)) {
            index++;
            entityName = suggestion + " (" + index + ")";
        }
        entity.setName(entityName);
        availableNames.add(entityName);
    }

    private static boolean isNameDuplicated(String name, List<String> availableNames) {
        return availableNames.stream().anyMatch(n -> n.equalsIgnoreCase(name));
    }
	
	
	public static List<WebServiceRequestEntity> newWSTestObjectsFromSwagger(FolderEntity parentFolder, String directoryOfJsonFile)
	            throws Exception {
        if (parentFolder == null) {
            return null;
        }
        List<WebServiceRequestEntity> newWSTestObjects = SwaggerParserUtil.parseFromFileLocationToWSTestObject(parentFolder, directoryOfJsonFile);
        for(WebServiceRequestEntity entity : newWSTestObjects){
        	entity.setElementGuidId(Util.generateGuid());
            entity.setParentFolder(parentFolder);
            entity.setProject(parentFolder.getProject());
        }
        
        return newWSTestObjects;
    }

}
