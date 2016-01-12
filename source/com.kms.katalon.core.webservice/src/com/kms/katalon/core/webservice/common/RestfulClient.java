package com.kms.katalon.core.webservice.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
		else if("DELETE".equalsIgnoreCase(request.getRestRequestMethod())){
			responseObject = sendDeleteRequest(request);
		}
		else{
			//POST, PUT are technically the same
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
		responseObject.setHeaderFields(con.getHeaderFields());
		
		return responseObject;
	}
	
	private ResponseObject sendPostRequest(RequestObject request) throws Exception{
		
		if(request.getRestUrl() != null && request.getRestUrl().toLowerCase().startsWith("https")){
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, getTrustManagers(), new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());			
		}
		URL url = new URL(request.getRestUrl());
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		if(request.getRestUrl() != null && request.getRestUrl().toLowerCase().startsWith("https")){
			((HttpsURLConnection)httpConnection).setHostnameVerifier(getHostnameVerifier());
		}
		
		httpConnection.setRequestMethod(request.getRestRequestMethod());
		//con.setRequestProperty("Authorization", token);
		//Default if not set
		httpConnection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
		httpConnection.setRequestProperty("Content-Type", DEFAULT_ACCEPT_CONTENT_TYPE);
		for(TestObjectProperty property : request.getHttpHeaderProperties()){
			httpConnection.setRequestProperty(property.getName(), property.getValue());
		}
		httpConnection.setDoOutput(true);
		
		// Send post request
		OutputStream os = httpConnection.getOutputStream();
		os.write((request.getHttpBody()==null ? "" : request.getHttpBody()).getBytes());
		os.flush();
		os.close();
 
		//Read response content
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			sb.append(inputLine);
		}
		reader.close();		
				
		ResponseObject responseObject = new ResponseObject(sb.toString());
		responseObject.setContentType(httpConnection.getContentType());
		responseObject.setHeaderFields(httpConnection.getHeaderFields());
		
		httpConnection.disconnect();
		
		return responseObject;
	}
	
	private ResponseObject sendDeleteRequest(RequestObject request) throws Exception {
		
		if(request.getRestUrl() != null && request.getRestUrl().toLowerCase().startsWith("https")){
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, getTrustManagers(), new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());			
		}
		URL url = new URL(request.getRestUrl());
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		if(request.getRestUrl() != null && request.getRestUrl().toLowerCase().startsWith("https")){
			((HttpsURLConnection)httpConnection).setHostnameVerifier(getHostnameVerifier());
		}
		
		httpConnection.setRequestMethod(request.getRestRequestMethod());
		//Default if not set
		httpConnection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
		httpConnection.setRequestProperty("Content-Type", DEFAULT_ACCEPT_CONTENT_TYPE);
		for(TestObjectProperty property : request.getHttpHeaderProperties()){
			httpConnection.setRequestProperty(property.getName(), property.getValue());
		}
		 
		//Read response content
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			sb.append(inputLine);
		}
		reader.close();		
				
		ResponseObject responseObject = new ResponseObject(sb.toString());
		responseObject.setContentType(httpConnection.getContentType());
		responseObject.setHeaderFields(httpConnection.getHeaderFields());
		
		httpConnection.disconnect();
		
		return responseObject;
	}
	
	private TrustManager[] getTrustManagers() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			} 
		};
		return trustAllCerts;
	}

	private HostnameVerifier getHostnameVerifier(){
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
		return hv;
	}
}
