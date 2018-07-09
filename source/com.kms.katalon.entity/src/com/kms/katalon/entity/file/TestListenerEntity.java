package com.kms.katalon.entity.file;

public class TestListenerEntity extends FileEntity {
    private static final long serialVersionUID = 607716101885640231L;

    public static final String FILE_EXTENSION = ".groovy";
    
    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

}
