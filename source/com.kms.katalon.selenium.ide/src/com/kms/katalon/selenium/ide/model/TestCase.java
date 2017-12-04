package com.kms.katalon.selenium.ide.model;

import java.util.List;

public class TestCase {
	private String name;
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
	
	@Override
	public String toString() {
		return "TestCase [name=" + name + ", commands=" + commands + "]";
	}
}
