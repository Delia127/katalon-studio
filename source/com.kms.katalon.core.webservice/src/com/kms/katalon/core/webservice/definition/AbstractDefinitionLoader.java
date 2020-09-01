package com.kms.katalon.core.webservice.definition;

public abstract class AbstractDefinitionLoader implements DefinitionLoader {

    protected String definitionLocation;

    public String getDefinitionLocation() {
        return definitionLocation;
    }
}
