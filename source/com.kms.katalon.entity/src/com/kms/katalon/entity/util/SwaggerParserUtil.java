package com.kms.katalon.entity.util;

import v2.io.swagger.parser.SwaggerParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

import v2.io.swagger.models.ArrayModel;
import v2.io.swagger.models.ComposedModel;
import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.models.Model;
import v2.io.swagger.models.ModelImpl;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.RefModel;
import v2.io.swagger.models.Scheme;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.Parameter;
import v2.io.swagger.models.properties.Property;

public class SwaggerParserUtil {
	private final static String IN_QUERY = "query";
	private final static String IN_PATH = "path";
	private final static String IN_BODY = "body";
	private final static String IN_FORM_DATA = "formData";
	private final static String ACCEPT_FORM_DATA = "form-data";
	private final static String ACCEPT_URL_ENCODED = "x-www-form-urlencoded";

	@SuppressWarnings("unchecked")
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocationOrUrl){
		
		List<WebServiceRequestEntity> newWSTestObject = new ArrayList<WebServiceRequestEntity>();
		
		try{
			Swagger swagger = new SwaggerParser().read(fileLocationOrUrl);
			String urlCommonPrefix = "https";
			if(swagger.getSchemes() != null 
					&& !swagger.getSchemes().isEmpty() 
					&& !swagger.getSchemes().contains(Scheme.HTTPS)){				
				urlCommonPrefix = swagger.getSchemes().get(0).toString();
			}
			
			urlCommonPrefix += "://" + swagger.getHost() + swagger.getBasePath();	
		
			for(Object oPathEntry : safeSet(swagger.getPaths().entrySet())){
				if(oPathEntry != null){
					
					Entry<String, Path> pathEntry = (Entry<String, Path>) oPathEntry;					
					Path aPath = pathEntry.getValue();												
					String urlCommonPrefix2 = urlCommonPrefix;				
					String katalonVariablePath = "";
					String[] pathAndVariables = safeString(pathEntry.getKey()).split("[\\{||\\}]");
					
					katalonVariablePath += pathAndVariables[0];
					// Replace every occurrence of { .. } with ${ .. }
					if(pathAndVariables.length > 1 ){
						for(int i = 1; i < pathAndVariables.length; i++){
							katalonVariablePath += "${" + pathAndVariables[i] + "}";
						}										
					}
					
					urlCommonPrefix2 += katalonVariablePath;
					
					for(Object oMethodAndOperation : safeSet(aPath.getOperationMap().entrySet())){
						if(oMethodAndOperation != null){
							
							Entry<HttpMethod, Operation> methodAndOperation = (Entry<HttpMethod, Operation>) oMethodAndOperation;							
							Operation operation = methodAndOperation.getValue();						
							WebServiceRequestEntity entity = new WebServiceRequestEntity();
							HttpMethod method = methodAndOperation.getKey();		
							String wsObjectName = safeString(operation.getOperationId());
							List<WebElementPropertyEntity> parametersInQuery = new ArrayList<>();
							List<WebElementPropertyEntity> parametersInHeader = new ArrayList<>();
							List<VariableEntity> parametersInPath = new ArrayList<>();
							ParameterizedBodyContent<FormDataBodyParameter> katalonFormParams = new 
									ParameterizedBodyContent<FormDataBodyParameter>();
							ParameterizedBodyContent<UrlEncodedBodyParameter> urlEncodedBodyParams = new 
									ParameterizedBodyContent<UrlEncodedBodyParameter>();
							
							entity.setName(wsObjectName.isEmpty() ? StringConstants.DEFAULT_WEB_SERVICE_NAME : wsObjectName);
							entity.setRestUrl(urlCommonPrefix2);
							entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
							entity.setRestRequestMethod(method.toString());
							
							for(Object oParam : safeList(operation.getParameters())){
								if(oParam != null){
									
									Parameter param = (Parameter) oParam;									
									String name = safeString(param.getName());
									String pattern = safeString(param.getPattern());
									
									switch(param.getIn()){
										case IN_QUERY:
											parametersInQuery.add(new WebElementPropertyEntity(name, pattern));
											break;
										case IN_PATH:
											VariableEntity variable = new VariableEntity();
											variable.setName(name);								
											variable.setDefaultValue(pattern);
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
							

							for(Object produce : safeList(operation.getProduces())){
								if(produce != null && !((String) produce).isEmpty()){
									WebElementPropertyEntity produceProperty = new WebElementPropertyEntity("Content-type", (String) produce);
									parametersInHeader.add(produceProperty);
								}
							}					
							
							String consumeType = StringUtils.EMPTY;
							for(Object consume : safeList(operation.getConsumes())){
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
							newWSTestObject.add(entity);
						}
					}
				}
			}
			
		} catch (Exception ex) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), com.kms.katalon.entity.constants.StringConstants.ERROR,
					com.kms.katalon.entity.constants.StringConstants.EXC_INVALID_SWAGGER_FILE);
        }
			
		return newWSTestObject;
	}
	
	public static List<?> safeList(List<?> list){
		return list == null ? Collections.EMPTY_LIST : list;
	}
	
	public static String safeString(String string){
		return string == null ? StringUtils.EMPTY : string;
	}
	
	public static Set<?> safeSet(Set<?> entrySet){
		return entrySet == null ? Collections.EMPTY_SET : entrySet;
		
	}
	
}
