package com.kms.katalon.integration.qtest.setting;

public enum QTestVersion {
    V6("6"), V7("7");

    private final String text;

    private QTestVersion(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
    
    public int getVersionNumber() {
        return Integer.parseInt(toString());
    }
    
    public boolean higherThan(QTestVersion that) {
        return this.getVersionNumber() > that.getVersionNumber();
    }

    public static QTestVersion getLastest() {
        int lastest = 0;
        QTestVersion lastestVersion = null;
        for (QTestVersion version : values()) {
            if (lastest < version.getVersionNumber()) {
                lastest = version.getVersionNumber();
                lastestVersion = version;
            }
        }
        return lastestVersion;
    }
    
    public static String[] valuesAsStrings() {        
        QTestVersion[] arrayOfValues = values();
        String[] arrayOfNames = new String[arrayOfValues.length];
        for (int index = 0; index < arrayOfValues.length; index++) {
            StringBuilder displayedValue =  new StringBuilder(arrayOfValues[index].toString());
            if (arrayOfValues[index] == getLastest()) {
                displayedValue.append(" or higher");
            }
            arrayOfNames[index] = displayedValue.toString();
        }
        return arrayOfNames;
    }
    
    public static QTestVersion valueOf(int ordinal) {
        for (QTestVersion version : values()) {
            if (version.ordinal() == ordinal) {
                return version;
            }
        }
        return getLastest();
    }
}
