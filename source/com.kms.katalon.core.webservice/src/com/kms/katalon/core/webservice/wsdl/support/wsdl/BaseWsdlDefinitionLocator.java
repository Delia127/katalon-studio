package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.io.InputStream;

import org.xml.sax.InputSource;

import com.kms.katalon.util.Tools;

public abstract class BaseWsdlDefinitionLocator implements WsdlDefinitionLocator {

	private String last;

	protected String wsdlLocation;

	@Override
	public InputSource getBaseInputSource() {
		try {
			InputStream is = load(wsdlLocation);
			return new InputSource(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract InputStream load(String url);

	@Override
	public String getBaseURI() {
		return wsdlLocation;
	}

	@Override
	public InputSource getImportInputSource(String parent, String imp) {
		if (isAbsoluteUrl(imp)) {
			last = imp;
		} else {
			last = Tools.joinRelativeUrl(parent, imp);
		}

		try {
			InputStream input = load(last);
			return input == null ? null : new InputSource(input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract boolean isAbsoluteUrl(String url);
	
	public String getWsdlLocation() {
	    return wsdlLocation;
	}

	@Override
	public String getLatestImportURI() {
		String result = last == null ? wsdlLocation : last;
		return result;
	}
	
	@Override
	public void close() {
	}
}
