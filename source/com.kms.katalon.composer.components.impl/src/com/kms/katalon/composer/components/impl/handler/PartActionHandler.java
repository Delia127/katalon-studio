package com.kms.katalon.composer.components.impl.handler;

import com.kms.katalon.entity.file.FileEntity;

public interface PartActionHandler<T extends FileEntity> {
    String getContributionURI();

    String getIconURI();

    String getPartId(T fileEntity);
}
