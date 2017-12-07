package com.kms.katalon.selenium.ide.format;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.util.ClazzUtils;

public class DefaultFormatter implements Formatter {

	@Override
	public String format(Command command) {
		String method = getCleanCommandTail(command.getCommand());
		boolean hasMethod = ClazzUtils.hasMethod(method);
		if (!hasMethod) {
			return String.format("Method %s is not found\n", command.getCommand());
		}
				
		StringBuffer buffer = new StringBuffer();
		buffer.append("selenium.");
		buffer.append(method);
		buffer.append("(");
		
		int paramCount = ClazzUtils.getParamCount(method);
		
		if (paramCount == 2) {
			buffer.append("\"" + command.getTarget() + "\"");
			buffer.append(", \"" + command.getValue() + "\"");
		} else if (paramCount == 1){
			buffer.append("\"" + command.getTarget() + "\"");
		}
		buffer.append(")");
		
		String wait = getWaitIfHas(command.getCommand());
		if (StringUtils.isNotBlank(wait)) {
			buffer.append(wait);
		}
		buffer.append("\n");
		
		return buffer.toString();
	}
}
