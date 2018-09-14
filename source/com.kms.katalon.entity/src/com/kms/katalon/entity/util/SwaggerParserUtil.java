package com.kms.katalon.entity.util;

import v2.io.swagger.parser.SwaggerParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;

import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Scheme;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.Parameter;

public class SwaggerParserUtil {
	private final static String QUERY = "query";
	private final static String PATH = "path";
	
	@SuppressWarnings("unchecked")
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocation){
		List<WebServiceRequestEntity> newWSTestObject = new ArrayList<WebServiceRequestEntity>();
		
		try{
			
			Swagger swagger = new SwaggerParser().read(fileLocation);
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
										case QUERY:
											parametersInQuery.add(new WebElementPropertyEntity(name, pattern));
											break;
										case PATH:
											VariableEntity variable = new VariableEntity();
											variable.setName(name);								
											variable.setDefaultValue(pattern);
											parametersInPath.add(variable);
											break;
										default:
											break;
									}
								}
							}	
							
							// Parse produces						
							for(Object produce : safeList(operation.getProduces())){
								if(produce != null){
									WebElementPropertyEntity produceProperty = new WebElementPropertyEntity("Content-type", (String) produce);
									parametersInHeader.add(produceProperty);
								}
							}					
							
							// parse consumes only for GET						
							for(Object consume : safeList(operation.getConsumes())){
								if(consume != null){
									WebElementPropertyEntity consumeProperty = new WebElementPropertyEntity("Accept", (String) consume);
									parametersInHeader.add(consumeProperty);
								}
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
