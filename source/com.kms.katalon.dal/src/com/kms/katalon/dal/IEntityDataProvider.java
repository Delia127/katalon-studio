package com.kms.katalon.dal;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;

public interface IEntityDataProvider {

    public boolean update(FileEntity entity) throws DALException;

}
