package com.kms.katalon.selenium.ide.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class TestObjectParser {

	public static String parse(String target) {
		if (StringUtils.isNotBlank(target)) {
			if (target.startsWith("link")) {
				return formatLink(target);
			}
			String ret = formatXpath(target);
			if (StringUtils.isBlank(ret)) {
				ret = formatNormal(target);
			}
			return ret;
		}
		return StringUtils.EMPTY;
	}
	
	private static String formatLink(String link) {
		Pattern pattern = Pattern.compile("(link=)(.*?)$");
        Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
        	return String.format("//*[text()='%s']", matcher.group(2));
        }
        return StringUtils.EMPTY;
	}
	
	private static String formatNormal(String text) {
		Pattern pattern = Pattern.compile("^(id|name|class|href|title|css|type|value)(=)(.*?)$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
        	return String.format("//*[%s='%s']", matcher.group(1), matcher.group(3));
        }
        return StringUtils.EMPTY;
	}
	
	private static String formatXpath(String xpath) {
        if (xpath.startsWith("//")) {
        	return xpath;
        }
        if (xpath.startsWith("xpath=")) {
        	return xpath.replaceAll("xpath=", "");
        }
        return StringUtils.EMPTY;
	}
	
}
