package com.kms.katalon.entity.dal.exception;

import java.util.HashMap;


public class WsException {
	
	private WsExceptionType exceptionType;
	private String message;
	private HashMap<Object, Object> properties;
	
	public WsException() {
		exceptionType = WsExceptionType.Unknown;
		message = null;
		properties = new HashMap<>();
	}
	
	public WsException(WsExceptionType exceptionType, String message) {
		this.exceptionType = exceptionType;
		this.message = message;
		properties = new HashMap<>();
	}
	
	public void setWsExceptionType(WsExceptionType exceptionType) { this.exceptionType = exceptionType; }
	public WsExceptionType getWsExceptionType() { return this.exceptionType; }
	
	public void setMessage(String message) { this.message = message; }
	public String getMessage() { return this.message; }
	
	public void setProperties(HashMap<Object, Object> properties) { this.properties = properties; }
	public HashMap<Object, Object> getProperties() { return this.properties; }
	
	public void addProperty(Object key, Object value) {
		properties.put(key, value);
	}
}
