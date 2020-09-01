package com.kms.katalon.core.webservice.definition;

import java.io.InputStream;

public interface DefinitionLoader {

    String getDefinitionLocation();
    
    InputStream load();
}
