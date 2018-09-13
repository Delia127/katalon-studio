package com.kms.katalon.entity.repository;

import v2.io.swagger.parser.SwaggerParser;

import com.kms.katalon.entity.folder.FolderEntity;

import v2.io.swagger.models.Swagger;

public class SwaggerParserUtil {
	public static WebServiceRequestEntity parseFromFileLocationToWSTestObject(FolderEntity parentFolder, String fileLocation){
		WebServiceRequestEntity newWSTestObject = new WebServiceRequestEntity();
		fileLocation = "C://Users//thanhto//Downloads//openapi-example.json";
		Swagger swagger = new SwaggerParser().read(fileLocation);
		String restURL = swagger.getSchemes().get(0).toString()
				+ "://" + swagger.getHost().toString() 
				+ swagger.getBasePath().toString() 
				+ swagger.getPaths().keySet().toArray()[0];

		return newWSTestObject;
	}
}
