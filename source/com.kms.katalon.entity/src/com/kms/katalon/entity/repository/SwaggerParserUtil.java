package com.kms.katalon.entity.repository;

import v2.io.swagger.parser.SwaggerParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.variable.VariableEntity;

import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Scheme;
import v2.io.swagger.models.Swagger;

public class SwaggerParserUtil {
	private final static String QUERY = "query";
	private final static String PATH = "path";
	
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
		
			for(Entry<String, Path> pathEntry : swagger.getPaths().entrySet()){
				Path aPath = pathEntry.getValue();
				String urlCommonPrefix2 = urlCommonPrefix + pathEntry.getKey();

				for(Entry<HttpMethod, Operation> methodAndOperation : aPath.getOperationMap().entrySet()){
					Operation operation = methodAndOperation.getValue();
					HttpMethod method = methodAndOperation.getKey();
					WebServiceRequestEntity entity = new WebServiceRequestEntity();
					entity.setName(operation.getSummary());
					entity.setRestUrl(urlCommonPrefix2);
					entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
					entity.setRestRequestMethod(method.toString());
					
					List<WebElementPropertyEntity> parametersInQuery = new ArrayList<>();
					List<VariableEntity> parametersInPath = new ArrayList<>();
					
					operation.getParameters().forEach(param -> {
						String name = param.getName() == null ? StringUtils.EMPTY : param.getName();
						String pattern = param.getPattern() == null ? StringUtils.EMPTY : param.getPattern();
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
					});	

					entity.setRestParameters(parametersInQuery);
					entity.setVariables(parametersInPath);
					newWSTestObject.add(entity);
				}
			}
			
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
			
		return newWSTestObject;
	}
}
