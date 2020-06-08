package com.kms.katalon.selenium.ide.model;

import java.util.List;

public class Command {
	private String command;
	private String target;
	private String value;
    private String comment;
	private List<String> options;
	
	public Command(){}
	
	public Command(String command, String target, String value) {
		this.command = command;
		this.target = target;
		this.value = value;
        this.comment = "";
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

	public List<String> getOptions() {
		return options;
	}
	
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	@Override
	public String toString() {
		return "Command [command=" + this.command + ", target=" + target + ", value=" + value + ", options=" + options + "]";
	}
}
