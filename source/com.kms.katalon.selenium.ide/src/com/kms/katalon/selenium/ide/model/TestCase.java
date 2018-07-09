package com.kms.katalon.selenium.ide.model;

import java.util.List;

public class TestCase {
	private String filePath;
	private String name;
	private String baseUrl;
	private List<Command> commands;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String toString() {
		return "TestCase [name=" + name + ", baseUrl=" + baseUrl + ", commands=" + commands + "]";
	}
}
