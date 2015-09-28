package com.kms.katalon.core.webservice.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.webservice.support.UrlEncoder;

public class RestfulClient implements Requestor {
	
	private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
	private static final String DEFAULT_ACCEPT_CONTENT_TYPE = "application/json";

	@Override
	public ResponseObject send(RequestObject request) throws Exception {
		ResponseObject responseObject;
		if("GET".equalsIgnoreCase(request.getRestRequestMethod())){
			responseObject = sendGetRequest(request);
		}
		else{
			//POST, PUT, DELETE are technically the same
			responseObject = sendPostRequest(request);
		}
		return responseObject;
	}

	private ResponseObject sendGetRequest(RequestObject request) throws Exception {
		//If there are some parameters, they should be append after the Service URL
		StringBuilder sbServiceUrl = new StringBuilder(request.getRestUrl());
		for(TestObjectProperty property : request.getRestParameters()){
			sbServiceUrl.append(UrlEncoder.encode(property.getName()));
			sbServiceUrl.append("&");
			sbServiceUrl.append(UrlEncoder.encode(property.getValue()));  
		}
		
		URL obj = new URL(sbServiceUrl.toString());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		
		//HTTP Headers
		//con.setRequestProperty ("Authorization", token);
		//Default if not set
		con.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
		//RESTFul usually return JSON, but it is not always, if user not set it, default is JSON
		con.setRequestProperty("Content-Type", DEFAULT_ACCEPT_CONTENT_TYPE);	
		for(TestObjectProperty property : request.getHttpHeaderProperties()){
			con.setRequestProperty(property.getName(), property.getValue());
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		
		ResponseObject responseObject = new ResponseObject(sb.toString());
		responseObject.setContentType(con.getContentType());
		
		return responseObject;
	}
	
	private ResponseObject sendPostRequest(RequestObject request) throws Exception{
		URL obj = new URL(request.getRestUrl());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		//con.setRequestProperty("Authorization", token);
		//Default if not set
		con.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
		con.setRequestProperty("Content-Type", DEFAULT_ACCEPT_CONTENT_TYPE);
		for(TestObjectProperty property : request.getHttpHeaderProperties()){
			con.setRequestProperty(property.getName(), property.getValue());
		}
		con.setDoOutput(true);
		
		// Send post request
		OutputStream os = con.getOutputStream();
		os.write((request.getHttpBody()==null ? "" : request.getHttpBody()).getBytes());
		os.flush();
		os.close();
 
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			sb.append(inputLine);
		}
		reader.close();		
		
		ResponseObject responseObject = new ResponseObject(sb.toString());
		responseObject.setContentType(con.getContentType());
		
		return responseObject;
	}
	
	/*
	public static void main(String[] args) throws Exception {
		RequestObject request = new RequestObject("");
		request.setRestRequestMethod("GET");
		request.setRestUrl("http://ip.jsontest.com/");
		new RestfulClient().send(request);
	}
	*/
}
