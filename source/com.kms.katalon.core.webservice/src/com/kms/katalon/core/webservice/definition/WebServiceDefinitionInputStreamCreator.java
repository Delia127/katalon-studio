package com.kms.katalon.core.webservice.definition;

import java.io.InputStream;
import java.util.Map;

public interface WebServiceDefinitionInputStreamCreator {

    InputStream createInputStream(String definitionLocation, Map<String, Object> params);
}
