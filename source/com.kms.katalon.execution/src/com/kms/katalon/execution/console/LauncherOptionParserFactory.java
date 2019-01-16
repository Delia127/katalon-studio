package com.kms.katalon.execution.console;

import java.util.Arrays;
import java.util.List;

import com.kms.katalon.execution.platform.PlatformLauncherOptionParserBuilder;

public class LauncherOptionParserFactory {
	
	private PlatformLauncherOptionParserBuilder platformBuilder;
	
	private static LauncherOptionParserFactory _instance;
	
	public static LauncherOptionParserFactory getInstance(){
		if(_instance == null){
			_instance = new LauncherOptionParserFactory();
		}
		return _instance;
	}
	
	public void setPlatformBuilder(PlatformLauncherOptionParserBuilder platformBuilder){
		this.platformBuilder = platformBuilder;
	}
	
	List<PlatformLauncherOptionParserBuilder> getBuilders(){
		return Arrays.asList(platformBuilder);
	}
}
