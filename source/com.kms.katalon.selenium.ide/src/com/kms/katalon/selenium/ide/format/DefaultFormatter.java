package com.kms.katalon.selenium.ide.format;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;

public class DefaultFormatter implements Formatter {

	@Override
	public String format(Command command) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("selenium.");
		buffer.append(command.getCommand());
		buffer.append("(");
		if (StringUtils.isNotBlank(command.getTarget())) {
			buffer.append("\"" + command.getTarget() + "\"");
			if (StringUtils.isNotBlank(command.getValue())) {
				buffer.append(", \"" + command.getValue() + "\"");
			}
		}
		buffer.append(")\n");
		return buffer.toString();
	}

}
