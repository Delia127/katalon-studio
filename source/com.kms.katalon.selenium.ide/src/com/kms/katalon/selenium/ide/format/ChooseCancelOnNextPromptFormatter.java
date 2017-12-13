package com.kms.katalon.selenium.ide.format;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;

public class ChooseCancelOnNextPromptFormatter implements Formatter {

	@Override
	public String format(Command command) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("driver.switchTo().alert().dismiss()");
		String wait = getWaitIfHas(command.getCommand());
		if (StringUtils.isNotBlank(wait)) {
			buffer.append(wait);
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
