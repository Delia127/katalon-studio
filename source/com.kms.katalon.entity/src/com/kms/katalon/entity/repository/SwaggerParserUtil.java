package com.kms.katalon.entity.repository;

import v2.io.swagger.parser.SwaggerParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.kms.katalon.entity.folder.FolderEntity;

import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Scheme;
import v2.io.swagger.models.Swagger;

public class SwaggerParserUtil {
	public static List<WebServiceRequestEntity> parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocation){
		List<WebServiceRequestEntity> newWSTestObject = new ArrayList<WebServiceRequestEntity>();
		Swagger swagger = new SwaggerParser().read(fileLocation);
		
		if(swagger != null){
			String urlCommonPrefix = "https";
			if(swagger.getSchemes() != null 
					&& !swagger.getSchemes().isEmpty() 
					&& !swagger.getSchemes().contains(Scheme.HTTPS)){				
				urlCommonPrefix = swagger.getSchemes().get(0).toString();
			}
			urlCommonPrefix += "//" + swagger.getHost() + swagger.getBasePath();
		
			for(Entry<String, Path> pathEntry : swagger.getPaths().entrySet()){
				Path aPath = pathEntry.getValue();
				String urlCommonPrefix2 = urlCommonPrefix + pathEntry.getKey();
				
				for(Operation operation : aPath.getOperations()){
					WebServiceRequestEntity entity = new WebServiceRequestEntity();
					entity.setName(operation.getSummary());
					entity.setRestUrl(urlCommonPrefix2);
					entity.setServiceType(WebServiceRequestEntity.SERVICE_TYPES[1]);
					List<WebElementPropertyEntity> props = new ArrayList<>();
					operation.getParameters().forEach(param -> {
						props.add(new WebElementPropertyEntity(param.getName(), param.getPattern()));
					});
					entity.setRestParameters(props);
				}
			}
		}
			
		return newWSTestObject;
	}
}
