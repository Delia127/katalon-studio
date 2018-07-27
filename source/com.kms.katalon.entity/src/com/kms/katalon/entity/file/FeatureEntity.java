package com.kms.katalon.entity.file;

public class FeatureEntity extends FileEntity {

    private static final long serialVersionUID = -3057404402287295394L;
    
    public static final String FILE_EXTENSION = ".feature";

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

}
