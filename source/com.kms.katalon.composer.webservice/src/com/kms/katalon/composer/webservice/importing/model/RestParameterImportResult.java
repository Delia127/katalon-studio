package com.kms.katalon.composer.webservice.importing.model;

public class RestParameterImportResult {

    private String name;

    private String value;

    private Style style;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RestParameterImportResult other = (RestParameterImportResult) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public static enum Style {
        HEADER, MATRIX, QUERY, TEMPLATE, PLAIN
    }

    @Override
    public String toString() {
        return "RestParameterImportResult [name=" + name + ", value=" + value + ", style=" + style + "]";
    }
}
