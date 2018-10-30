package com.kms.katalon.dal;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public interface IEntityDataProvider {

    public boolean update(FileEntity entity) throws DALException;
    
    public String toXmlString(Object entity) throws DALException;

<<<<<<< HEAD
	public <T> T toEntity(String xmlString, Class<T> clazz) throws DALException;
=======
	public Object toObject(String xmlString) throws DALException;
>>>>>>> origin/KAT-3778

}
